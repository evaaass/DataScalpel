package cn.superhuang.data.scalpel.admin.app.datafile.model;

import cn.superhuang.data.scalpel.model.DataTable;
import lombok.Data;

import java.util.List;

@Data
public class DataFileMetadata {
    public List<DataTable> dataTables;
}