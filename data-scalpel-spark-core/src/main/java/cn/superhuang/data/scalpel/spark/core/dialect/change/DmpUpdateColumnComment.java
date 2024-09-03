package cn.superhuang.data.scalpel.spark.core.dialect.change;

import lombok.Data;
import org.apache.spark.sql.connector.catalog.TableChange;
import org.apache.spark.sql.types.DataType;

@Data
public class DmpUpdateColumnComment implements TableChange.ColumnChange {

    private final String[] fieldNames;
    private final String newComment;
    private final DataType type;
    private final Boolean nullable;

    public DmpUpdateColumnComment(String[] fieldNames, String newComment, DataType type, Boolean nullable) {
        this.fieldNames = fieldNames;
        this.newComment = newComment;
        this.type = type;
        this.nullable = nullable;
    }

    @Override
    public String[] fieldNames() {
        return fieldNames;
    }
}
