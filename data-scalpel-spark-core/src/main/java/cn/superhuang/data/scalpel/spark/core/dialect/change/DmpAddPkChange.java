package cn.superhuang.data.scalpel.spark.core.dialect.change;

import lombok.Data;
import org.apache.spark.sql.connector.catalog.TableChange;

@Data
public class DmpAddPkChange implements TableChange.ColumnChange {

    private final String[] fieldNames;


    public DmpAddPkChange(String[] fieldNames) {
        this.fieldNames = fieldNames;
    }

    @Override
    public String[] fieldNames() {
        return fieldNames;
    }
}
