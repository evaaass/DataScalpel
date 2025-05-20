package cn.superhuang.data.scalpel.model.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TestResult {
    private Boolean valid;
    private String msg;
    private String detail;
}
