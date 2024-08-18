package cn.superhuang.data.scalpel.admin.app.datasource.dto;

import lombok.Data;

import java.util.List;

@Data
public class DsItemPreviewResult {
    private DsItemPreviewDataType type;
    private Long count;
    private List content;
}
