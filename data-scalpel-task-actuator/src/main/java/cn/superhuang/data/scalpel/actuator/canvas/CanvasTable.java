package cn.superhuang.data.scalpel.actuator.canvas;

import cn.superhuang.data.scalpel.model.DataTable;
import lombok.Data;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;

@Data
public class CanvasTable extends DataTable implements Serializable {
    @Serial
    private static final long serialVersionUID = -1627539247144862586L;
    private Dataset<Row> dataset;
    private String previewData;


    @Override
    public CanvasTable clone() {
        CanvasTable table = new CanvasTable();
        table.setDataset(dataset);
        table.setName(getName());
        table.setCnName(getCnName());
        table.setColumns(getColumns());
        table.setMetadata(new HashMap<>(getMetadata()));
        return table;
    }
}
