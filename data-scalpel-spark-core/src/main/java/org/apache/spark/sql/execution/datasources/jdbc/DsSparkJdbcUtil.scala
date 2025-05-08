package org.apache.spark.sql.execution.datasources.jdbc


import java.math.{BigDecimal => JBigDecimal}
import java.nio.charset.StandardCharsets
import java.sql.{Connection, Date, JDBCType, PreparedStatement, ResultSet, ResultSetMetaData, SQLException, Time, Timestamp}
import java.time.{Instant, LocalDate}
import java.util
import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters._
import scala.util.Try
import scala.util.control.NonFatal
import org.apache.spark.{SparkThrowable, SparkUnsupportedOperationException, TaskContext}
import org.apache.spark.executor.InputMetrics
import org.apache.spark.internal.{Logging, MDC}
import org.apache.spark.internal.LogKeys.{DEFAULT_ISOLATION_LEVEL, ISOLATION_LEVEL}
import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.sql.catalyst.{InternalRow, SQLConfHelper}
import org.apache.spark.sql.catalyst.analysis.{DecimalPrecision, Resolver}
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder
import org.apache.spark.sql.catalyst.expressions.SpecificInternalRow
import org.apache.spark.sql.catalyst.parser.CatalystSqlParser
import org.apache.spark.sql.catalyst.util.{CaseInsensitiveMap, CharVarcharUtils, GenericArrayData}
import org.apache.spark.sql.catalyst.util.DateTimeConstants.MICROS_PER_MILLIS
import org.apache.spark.sql.catalyst.util.DateTimeUtils._
import org.apache.spark.sql.connector.catalog.{Identifier, TableChange}
import org.apache.spark.sql.connector.catalog.index.{SupportsIndex, TableIndex}
import org.apache.spark.sql.connector.expressions.NamedReference
import org.apache.spark.sql.errors.{QueryCompilationErrors, QueryExecutionErrors}
import org.apache.spark.sql.execution.datasources.jdbc.JdbcUtils.{conf, getCommonJDBCType, getJdbcType}
import org.apache.spark.sql.jdbc.{JdbcDialect, JdbcDialects, JdbcType, NoopDialect}
import org.apache.spark.sql.types._
import org.apache.spark.sql.util.SchemaUtils
import org.apache.spark.unsafe.types.UTF8String
import org.apache.spark.util.ArrayImplicits._
import org.apache.spark.util.NextIterator

object DsSparkJdbcUtil extends  SQLConfHelper {


  @throws[SQLException]
  def setStatementParameters(stmt: PreparedStatement, conn: Connection, dialect: JdbcDialect, rddSchema: StructType, row: Row): Unit = {
    val setters = rddSchema.fields.map(f => makeSetter(conn, dialect, f.dataType))
    val nullTypes = rddSchema.fields.map(f => getJdbcType(f.dataType, dialect).jdbcNullType)
    val numFields = rddSchema.fields.length
    try {
      var i = 0
      while (i < numFields) {
        if (row.isNullAt(i)) stmt.setNull(i + 1, nullTypes(i))
        else setters(i).apply(stmt, row, i)
        i = i + 1
      }

    }
  }
  @throws[SQLException]
  def SaveRow(conn: Connection, dialect: JdbcDialect, tableNameWithSchema: String, rddSchema: StructType, row: Row, options: JDBCOptions): Unit = {
    val insertStmt = JdbcUtils.getInsertStatement(tableNameWithSchema, rddSchema, null, true, dialect)
    val stmt = conn.prepareStatement(insertStmt)
    val setters = rddSchema.fields.map(f => makeSetter(conn, dialect, f.dataType))
    val nullTypes = rddSchema.fields.map(f => getJdbcType(f.dataType, dialect).jdbcNullType)
    val numFields = rddSchema.fields.length
    try {
      var rowCount = 0
      stmt.setQueryTimeout(options.queryTimeout)
      var i = 0
      while (i < numFields) {
        if (row.isNullAt(i)) stmt.setNull(i + 1, nullTypes(i))
        else setters(i).apply(stmt, row, i)
        i = i + 1
      }

    }

    finally stmt.close()
  }


  def createTable(
                   conn: Connection,
                   tableName: String,
                   schema: StructType,
                   caseSensitive: Boolean,
                   options: JdbcOptionsInWrite): Unit = {
    val statement = conn.createStatement
    val dialect = JdbcDialects.get(options.url)
    val strSchema = schemaString(
      schema, caseSensitive, options.url, options.createTableColumnTypes)
    try {
      statement.setQueryTimeout(options.queryTimeout)
      dialect.createTable(statement, tableName, strSchema, options)
      if (options.tableComment.nonEmpty) {
        try {
          val tableCommentQuery = dialect.getTableCommentQuery(tableName, options.tableComment)
          statement.executeUpdate(tableCommentQuery)
        } catch {
          case e: Exception =>
            e.printStackTrace()
        }
      }
    } finally {
      statement.close()
    }
  }

  def schemaString(
                    schema: StructType,
                    caseSensitive: Boolean,
                    url: String,
                    createTableColumnTypes: Option[String] = None): String = {
    val sb = new StringBuilder()
    val dialect = JdbcDialects.get(url)
    val userSpecifiedColTypesMap = createTableColumnTypes
      .map(parseUserSpecifiedCreateTableColumnTypes(schema, caseSensitive, _))
      .getOrElse(Map.empty[String, String])
    schema.fields.foreach { field =>
      val name = dialect.quoteIdentifier(field.name)
      val option = userSpecifiedColTypesMap.get(field.name)
      val typ = option match {
        case Some(value) => getJdbcType(DataType.fromDDL(value), dialect).databaseTypeDefinition
        case None => getJdbcType(field.dataType, dialect).databaseTypeDefinition
      }
      val nullable = if (field.nullable) "" else "NOT NULL"
      sb.append(s", $name $typ $nullable")
    }
    if (sb.length < 2) "" else sb.substring(2)
  }


  private def parseUserSpecifiedCreateTableColumnTypes(
                                                        schema: StructType,
                                                        caseSensitive: Boolean,
                                                        createTableColumnTypes: String): Map[String, String] = {
    val userSchema = CatalystSqlParser.parseTableSchema(createTableColumnTypes)

    // checks duplicate columns in the user specified column types.
    SchemaUtils.checkColumnNameDuplication(userSchema.map(_.name), conf.resolver)

    // checks if user specified column names exist in the DataFrame schema
    userSchema.fieldNames.foreach { col =>
      schema.find(f => conf.resolver(f.name, col)).getOrElse {
        throw QueryCompilationErrors.createTableColumnTypesOptionColumnNotFoundInSchemaError(
          col, schema)
      }
    }

    val userSchemaMap = userSchema.fields.map(f => f.name -> f.dataType.catalogString).toMap
    if (caseSensitive) userSchemaMap else CaseInsensitiveMap(userSchemaMap)
  }

  // A `JDBCValueSetter` is responsible for setting a value from `Row` into a field for
  // `PreparedStatement`. The last argument `Int` means the index for the value to be set
  // in the SQL statement and also used for the value in `Row`.
  private type JDBCValueSetter = (PreparedStatement, Row, Int) => Unit

  private def makeSetter(
                          conn: Connection,
                          dialect: JdbcDialect,
                          dataType: DataType): JDBCValueSetter = dataType match {
    case IntegerType =>
      (stmt: PreparedStatement, row: Row, pos: Int) =>
        stmt.setInt(pos + 1, row.getInt(pos))

    case LongType =>
      (stmt: PreparedStatement, row: Row, pos: Int) =>
        stmt.setLong(pos + 1, row.getLong(pos))

    case DoubleType =>
      (stmt: PreparedStatement, row: Row, pos: Int) =>
        stmt.setDouble(pos + 1, row.getDouble(pos))

    case FloatType =>
      (stmt: PreparedStatement, row: Row, pos: Int) =>
        stmt.setFloat(pos + 1, row.getFloat(pos))

    case ShortType =>
      (stmt: PreparedStatement, row: Row, pos: Int) =>
        stmt.setInt(pos + 1, row.getShort(pos))

    case ByteType =>
      (stmt: PreparedStatement, row: Row, pos: Int) =>
        stmt.setInt(pos + 1, row.getByte(pos))

    case BooleanType =>
      (stmt: PreparedStatement, row: Row, pos: Int) =>
        stmt.setBoolean(pos + 1, row.getBoolean(pos))

    case StringType =>
      (stmt: PreparedStatement, row: Row, pos: Int) =>
        stmt.setString(pos + 1, row.getString(pos))

    case BinaryType =>
      (stmt: PreparedStatement, row: Row, pos: Int) =>
        stmt.setBytes(pos + 1, row.getAs[Array[Byte]](pos))

    case TimestampType =>
      if (conf.datetimeJava8ApiEnabled) {
        (stmt: PreparedStatement, row: Row, pos: Int) =>
          stmt.setTimestamp(pos + 1, toJavaTimestamp(instantToMicros(row.getAs[Instant](pos))))
      } else {
        (stmt: PreparedStatement, row: Row, pos: Int) =>
          stmt.setTimestamp(pos + 1, row.getAs[java.sql.Timestamp](pos))
      }

    case TimestampNTZType =>
      (stmt: PreparedStatement, row: Row, pos: Int) =>
        stmt.setTimestamp(pos + 1,
          dialect.convertTimestampNTZToJavaTimestamp(row.getAs[java.time.LocalDateTime](pos)))

    case DateType =>
      if (conf.datetimeJava8ApiEnabled) {
        (stmt: PreparedStatement, row: Row, pos: Int) =>
          stmt.setDate(pos + 1, toJavaDate(localDateToDays(row.getAs[LocalDate](pos))))
      } else {
        (stmt: PreparedStatement, row: Row, pos: Int) =>
          stmt.setDate(pos + 1, row.getAs[java.sql.Date](pos))
      }

    case t: DecimalType =>
      (stmt: PreparedStatement, row: Row, pos: Int) =>
        stmt.setBigDecimal(pos + 1, row.getDecimal(pos))

    case ArrayType(et, _) =>
      // remove type length parameters from end of type name
      (stmt: PreparedStatement, row: Row, pos: Int) =>
        et match {
          case TimestampNTZType =>
            val array = row.getSeq[java.time.LocalDateTime](pos)
            val arrayType = conn.createArrayOf(
              getJdbcType(et, dialect).databaseTypeDefinition.split("\\(")(0),
              array.map(dialect.convertTimestampNTZToJavaTimestamp).toArray)
            stmt.setArray(pos + 1, arrayType)
          case _ =>
            @tailrec
            def getElementTypeName(dt: DataType): String = dt match {
              case ArrayType(et0, _) => getElementTypeName(et0)
              case a: AtomicType => getJdbcType(a, dialect).databaseTypeDefinition.split("\\(")(0)
              case _ => throw QueryExecutionErrors.nestedArraysUnsupportedError()
            }

            def toArray(seq: scala.collection.Seq[Any], dt: DataType): Array[Any] = dt match {
              case ArrayType(et0, _) =>
                seq.map(i => toArray(i.asInstanceOf[scala.collection.Seq[Any]], et0)).toArray
              case _ => seq.toArray
            }

            val seq = row.getSeq[AnyRef](pos)
            val arrayType = conn.createArrayOf(
              getElementTypeName(et),
              toArray(seq, et).asInstanceOf[Array[AnyRef]])
            stmt.setArray(pos + 1, arrayType)
        }

    case _ =>
      (_: PreparedStatement, _: Row, pos: Int) =>
        throw QueryExecutionErrors.cannotTranslateNonNullValueForFieldError(pos)
  }


  def getJdbcType(dt: DataType, dialect: JdbcDialect): JdbcType = {
    dialect.getJDBCType(dt).orElse(getCommonJDBCType(dt)).getOrElse(
      throw QueryExecutionErrors.cannotGetJdbcTypeError(dt))
  }

  def resultSetToSparkInternalRows(
                                    resultSet: ResultSet,
                                    dialect: JdbcDialect,
                                    schema: StructType,
                                    inputMetrics: InputMetrics): Iterator[InternalRow] = {
    new NextIterator[InternalRow] {
      private[this] val rs = resultSet
      private[this] val getters: Array[JDBCValueGetter] = makeGetters(dialect, schema)
      private[this] val mutableRow = new SpecificInternalRow(schema.fields.map(x => x.dataType))

      override protected def close(): Unit = {
        try {
          rs.close()
        } catch {
          case e: Exception => e.printStackTrace()//logWarning("Exception closing resultset", e)
        }
      }

      override protected def getNext(): InternalRow = {
        if (rs.next()) {
          inputMetrics.incRecordsRead(1)
          var i = 0
          while (i < getters.length) {
            getters(i).apply(rs, mutableRow, i)
            if (rs.wasNull) mutableRow.setNullAt(i)
            i = i + 1
          }
          mutableRow
        } else {
          finished = true
          null.asInstanceOf[InternalRow]
        }
      }
    }
  }

  // A `JDBCValueGetter` is responsible for getting a value from `ResultSet` into a field
  // for `MutableRow`. The last argument `Int` means the index for the value to be set in
  // the row and also used for the value in `ResultSet`.
  private type JDBCValueGetter = (ResultSet, InternalRow, Int) => Unit

  /**
   * Creates `JDBCValueGetter`s according to [[StructType]], which can set
   * each value from `ResultSet` to each field of [[InternalRow]] correctly.
   */
  private def makeGetters(
                           dialect: JdbcDialect,
                           schema: StructType): Array[JDBCValueGetter] = {
    val replaced = CharVarcharUtils.replaceCharVarcharWithStringInSchema(schema)
    replaced.fields.map(sf => makeGetter(sf.dataType, dialect, sf.metadata))
  }

  private def makeGetter(
                          dt: DataType,
                          dialect: JdbcDialect,
                          metadata: Metadata): JDBCValueGetter = dt match {
    case BooleanType =>
      (rs: ResultSet, row: InternalRow, pos: Int) =>
        row.setBoolean(pos, rs.getBoolean(pos + 1))

    case DateType =>
      (rs: ResultSet, row: InternalRow, pos: Int) =>
        // DateTimeUtils.fromJavaDate does not handle null value, so we need to check it.
        val dateVal = rs.getDate(pos + 1)
        if (dateVal != null) {
          row.setInt(pos, fromJavaDate(dialect.convertJavaDateToDate(dateVal)))
        } else {
          row.update(pos, null)
        }

    // When connecting with Oracle DB through JDBC, the precision and scale of BigDecimal
    // object returned by ResultSet.getBigDecimal is not correctly matched to the table
    // schema reported by ResultSetMetaData.getPrecision and ResultSetMetaData.getScale.
    // If inserting values like 19999 into a column with NUMBER(12, 2) type, you get through
    // a BigDecimal object with scale as 0. But the dataframe schema has correct type as
    // DecimalType(12, 2). Thus, after saving the dataframe into parquet file and then
    // retrieve it, you will get wrong result 199.99.
    // So it is needed to set precision and scale for Decimal based on JDBC metadata.
    case DecimalType.Fixed(p, s) =>
      (rs: ResultSet, row: InternalRow, pos: Int) =>
        val decimal =
          nullSafeConvert[JBigDecimal](rs.getBigDecimal(pos + 1), d => Decimal(d, p, s))
        row.update(pos, decimal)

    case DoubleType =>
      (rs: ResultSet, row: InternalRow, pos: Int) =>
        row.setDouble(pos, rs.getDouble(pos + 1))

    case FloatType =>
      (rs: ResultSet, row: InternalRow, pos: Int) =>
        row.setFloat(pos, rs.getFloat(pos + 1))

    case IntegerType =>
      (rs: ResultSet, row: InternalRow, pos: Int) =>
        row.setInt(pos, rs.getInt(pos + 1))

    case LongType if metadata.contains("binarylong") =>
      (rs: ResultSet, row: InternalRow, pos: Int) =>
        val l = nullSafeConvert[Array[Byte]](rs.getBytes(pos + 1), bytes => {
          var ans = 0L
          var j = 0
          while (j < bytes.length) {
            ans = 256 * ans + (255 & bytes(j))
            j = j + 1
          }
          ans
        })
        row.update(pos, l)

    case LongType =>
      (rs: ResultSet, row: InternalRow, pos: Int) =>
        row.setLong(pos, rs.getLong(pos + 1))

    case ShortType =>
      (rs: ResultSet, row: InternalRow, pos: Int) =>
        row.setShort(pos, rs.getShort(pos + 1))

    case ByteType =>
      (rs: ResultSet, row: InternalRow, pos: Int) =>
        row.setByte(pos, rs.getByte(pos + 1))

    case StringType if metadata.contains("rowid") =>
      (rs: ResultSet, row: InternalRow, pos: Int) =>
        val rawRowId = rs.getRowId(pos + 1)
        if (rawRowId == null) {
          row.update(pos, null)
        } else {
          row.update(pos, UTF8String.fromString(rawRowId.toString))
        }

    case StringType =>
      (rs: ResultSet, row: InternalRow, pos: Int) =>
        // TODO(davies): use getBytes for better performance, if the encoding is UTF-8
        row.update(pos, UTF8String.fromString(rs.getString(pos + 1)))

    // SPARK-34357 - sql TIME type represents as zero epoch timestamp.
    // It is mapped as Spark TimestampType but fixed at 1970-01-01 for day,
    // time portion is time of day, with no reference to a particular calendar,
    // time zone or date, with a precision till microseconds.
    // It stores the number of milliseconds after midnight, 00:00:00.000000
    case TimestampType if metadata.contains("logical_time_type") =>
      (rs: ResultSet, row: InternalRow, pos: Int) => {
        row.update(pos, nullSafeConvert[Time](
          rs.getTime(pos + 1), t => Math.multiplyExact(t.getTime, MICROS_PER_MILLIS)))
      }

    case TimestampType =>
      (rs: ResultSet, row: InternalRow, pos: Int) =>
        val t = rs.getTimestamp(pos + 1)
        if (t != null) {
          row.setLong(pos, fromJavaTimestamp(dialect.convertJavaTimestampToTimestamp(t)))
        } else {
          row.update(pos, null)
        }

    case TimestampNTZType if metadata.contains("logical_time_type") =>
      (rs: ResultSet, row: InternalRow, pos: Int) =>
        val micros = nullSafeConvert[Time](rs.getTime(pos + 1), t => {
          val time = dialect.convertJavaTimestampToTimestampNTZ(new Timestamp(t.getTime))
          localDateTimeToMicros(time)
        })
        row.update(pos, micros)

    case TimestampNTZType =>
      (rs: ResultSet, row: InternalRow, pos: Int) =>
        val t = rs.getTimestamp(pos + 1)
        if (t != null) {
          row.setLong(pos, localDateTimeToMicros(dialect.convertJavaTimestampToTimestampNTZ(t)))
        } else {
          row.update(pos, null)
        }

    case BinaryType if metadata.contains("binarylong") =>
      (rs: ResultSet, row: InternalRow, pos: Int) =>
        val bytes = rs.getBytes(pos + 1)
        if (bytes != null) {
          val binary = bytes.flatMap(Integer.toBinaryString(_).getBytes(StandardCharsets.US_ASCII))
          row.update(pos, binary)
        } else {
          row.update(pos, null)
        }

    case BinaryType =>
      (rs: ResultSet, row: InternalRow, pos: Int) =>
        row.update(pos, rs.getBytes(pos + 1))

    case _: YearMonthIntervalType =>
      (rs: ResultSet, row: InternalRow, pos: Int) =>
        row.update(pos,
          nullSafeConvert(rs.getString(pos + 1), dialect.getYearMonthIntervalAsMonths))

    case _: DayTimeIntervalType =>
      (rs: ResultSet, row: InternalRow, pos: Int) =>
        row.update(pos,
          nullSafeConvert(rs.getString(pos + 1), dialect.getDayTimeIntervalAsMicros))

    case _: ArrayType if metadata.contains("pg_bit_array_type") =>
      // SPARK-47628: Handle PostgreSQL bit(n>1) array type ahead. As in the pgjdbc driver,
      // bit(n>1)[] is not distinguishable from bit(1)[], and they are all recognized as boolen[].
      // This is wrong for bit(n>1)[], so we need to handle it first as byte array.
      (rs: ResultSet, row: InternalRow, pos: Int) =>
        val fieldString = rs.getString(pos + 1)
        if (fieldString != null) {
          val strArray = fieldString.substring(1, fieldString.length - 1).split(",")
          // Charset is picked from the pgjdbc driver for consistency.
          val bytesArray = strArray.map(_.getBytes(StandardCharsets.US_ASCII))
          row.update(pos, new GenericArrayData(bytesArray))
        } else {
          row.update(pos, null)
        }

    case ArrayType(et, _) =>
      def elementConversion(et: DataType): AnyRef => Any = et match {
        case TimestampType => arrayConverter[Timestamp] {
          (t: Timestamp) => fromJavaTimestamp(dialect.convertJavaTimestampToTimestamp(t))
        }

        case TimestampNTZType =>
          arrayConverter[Timestamp] {
            (t: Timestamp) => localDateTimeToMicros(dialect.convertJavaTimestampToTimestampNTZ(t))
          }

        case StringType =>
          arrayConverter[Object]((obj: Object) => UTF8String.fromString(obj.toString))

        case DateType => arrayConverter[Date] {
          (d: Date) => fromJavaDate(dialect.convertJavaDateToDate(d))
        }

        case dt: DecimalType =>
          arrayConverter[java.math.BigDecimal](d => Decimal(d, dt.precision, dt.scale))

        case LongType if metadata.contains("binarylong") =>
          throw QueryExecutionErrors.unsupportedArrayElementTypeBasedOnBinaryError(dt)

        case ArrayType(et0, _) =>
          arrayConverter[Array[Any]] {
            arr => new GenericArrayData(elementConversion(et0)(arr))
          }

        case _ => (array: Object) => array.asInstanceOf[Array[Any]]
      }

      (rs: ResultSet, row: InternalRow, pos: Int) =>
        val array = nullSafeConvert[java.sql.Array](
          input = rs.getArray(pos + 1),
          array => new GenericArrayData(elementConversion(et)(array.getArray)))
        row.update(pos, array)

    case NullType =>
      (_: ResultSet, row: InternalRow, pos: Int) => row.update(pos, null)

    case _ => throw QueryExecutionErrors.unsupportedJdbcTypeError(dt.catalogString)
  }

  private def nullSafeConvert[T](input: T, f: T => Any): Any = {
    if (input == null) {
      null
    } else {
      f(input)
    }
  }

  private def arrayConverter[T](elementConvert: T => Any): Any => Any = (array: Any) => {
    array.asInstanceOf[Array[T]].map(e => nullSafeConvert(e, elementConvert))
  }
}
