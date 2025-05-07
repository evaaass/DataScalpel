package util

import org.apache.spark.executor.InputMetrics
import org.apache.spark.sql.Row
import org.apache.spark.sql.catalyst.expressions.SpecificInternalRow
import org.apache.spark.sql.catalyst.{InternalRow, SQLConfHelper}
import org.apache.spark.sql.catalyst.parser.CatalystSqlParser
import org.apache.spark.sql.catalyst.util.{CaseInsensitiveMap, CharVarcharUtils, DateTimeUtils, GenericArrayData}
import org.apache.spark.sql.catalyst.util.DateTimeUtils.{instantToMicros, localDateToDays, toJavaDate, toJavaTimestamp}
import org.apache.spark.sql.errors.{QueryCompilationErrors, QueryExecutionErrors}
import org.apache.spark.sql.execution.datasources.jdbc.{JDBCOptions, JdbcOptionsInWrite, JdbcUtils}
import org.apache.spark.sql.execution.datasources.jdbc.JdbcUtils.{JDBCValueSetter, conf, getCommonJDBCType}
import org.apache.spark.sql.jdbc.{JdbcDialect, JdbcDialects, JdbcType}
import org.apache.spark.sql.types.{ArrayType, BinaryType, BooleanType, ByteType, DataType, DataTypes, DateType, Decimal, DecimalType, DoubleType, FloatType, IntegerType, LongType, Metadata, ShortType, StringType, StructType, TimestampNTZType, TimestampType}
import org.apache.spark.unsafe.types.UTF8String
import org.apache.spark.util.NextIterator

import java.sql.{Connection, PreparedStatement, ResultSet, SQLException}
import java.time.{Instant, LocalDate}
import java.util.Locale
import java.util.concurrent.TimeUnit

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

  type JDBCValueSetter = (PreparedStatement, Row, Int) => Unit

  def makeSetter(
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
      val typeName = getJdbcType(et, dialect).databaseTypeDefinition
        .toLowerCase(Locale.ROOT).split("\\(")(0)
      (stmt: PreparedStatement, row: Row, pos: Int) =>
        val array = conn.createArrayOf(
          typeName,
          row.getSeq[AnyRef](pos).toArray)
        stmt.setArray(pos + 1, array)

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
          row.setInt(pos, DateTimeUtils.fromJavaDate(dateVal))
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
          nullSafeConvert[java.math.BigDecimal](rs.getBigDecimal(pos + 1), d => Decimal(d, p, s))
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
        val bytes = rs.getBytes(pos + 1)
        var ans = 0L
        var j = 0
        while (j < bytes.length) {
          ans = 256 * ans + (255 & bytes(j))
          j = j + 1
        }
        row.setLong(pos, ans)

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
        val rawTime = rs.getTime(pos + 1)
        if (rawTime != null) {
          val localTimeMicro = TimeUnit.NANOSECONDS.toMicros(
            rawTime.toLocalTime().toNanoOfDay())
          val utcTimeMicro = DateTimeUtils.toUTCTime(
            localTimeMicro, conf.sessionLocalTimeZone)
          row.setLong(pos, utcTimeMicro)
        } else {
          row.update(pos, null)
        }
      }

    case TimestampType =>
      (rs: ResultSet, row: InternalRow, pos: Int) =>
        val t = rs.getTimestamp(pos + 1)
        if (t != null) {
          row.setLong(pos, DateTimeUtils.
            fromJavaTimestamp(dialect.convertJavaTimestampToTimestamp(t)))
        } else {
          row.update(pos, null)
        }

    case TimestampNTZType =>
      (rs: ResultSet, row: InternalRow, pos: Int) =>
        val t = rs.getTimestamp(pos + 1)
        if (t != null) {
          row.setLong(pos,
            DateTimeUtils.localDateTimeToMicros(dialect.convertJavaTimestampToTimestampNTZ(t)))
        } else {
          row.update(pos, null)
        }

    case BinaryType =>
      (rs: ResultSet, row: InternalRow, pos: Int) =>
        row.update(pos, rs.getBytes(pos + 1))

    case ArrayType(et, _) =>
      val elementConversion = et match {
        case TimestampType =>
          (array: Object) =>
            array.asInstanceOf[Array[java.sql.Timestamp]].map { timestamp =>
              nullSafeConvert(timestamp, DateTimeUtils.fromJavaTimestamp)
            }

        case StringType =>
          (array: Object) =>
            // some underling types are not String such as uuid, inet, cidr, etc.
            array.asInstanceOf[Array[java.lang.Object]]
              .map(obj => if (obj == null) null else UTF8String.fromString(obj.toString))

        case DateType =>
          (array: Object) =>
            array.asInstanceOf[Array[java.sql.Date]].map { date =>
              nullSafeConvert(date, DateTimeUtils.fromJavaDate)
            }

        case dt: DecimalType =>
          (array: Object) =>
            array.asInstanceOf[Array[java.math.BigDecimal]].map { decimal =>
              nullSafeConvert[java.math.BigDecimal](
                decimal, d => Decimal(d, dt.precision, dt.scale))
            }

        case LongType if metadata.contains("binarylong") =>
          throw QueryExecutionErrors.unsupportedArrayElementTypeBasedOnBinaryError(dt)

        case ArrayType(_, _) =>
          throw QueryExecutionErrors.nestedArraysUnsupportedError()

        case _ => (array: Object) => array.asInstanceOf[Array[Any]]
      }

      (rs: ResultSet, row: InternalRow, pos: Int) =>
        val array = nullSafeConvert[java.sql.Array](
          input = rs.getArray(pos + 1),
          array => new GenericArrayData(elementConversion.apply(array.getArray)))
        row.update(pos, array)

    case _ => throw QueryExecutionErrors.unsupportedJdbcTypeError(dt.catalogString)
  }


  private def nullSafeConvert[T](input: T, f: T => Any): Any = {
    if (input == null) {
      null
    } else {
      f(input)
    }
  }
}
