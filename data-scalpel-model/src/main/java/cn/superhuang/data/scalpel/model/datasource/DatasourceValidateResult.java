package cn.superhuang.data.scalpel.model.datasource;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DatasourceValidateResult {
    private Boolean valid;
    private String errorMsg;
    private String errorDetail;
}
