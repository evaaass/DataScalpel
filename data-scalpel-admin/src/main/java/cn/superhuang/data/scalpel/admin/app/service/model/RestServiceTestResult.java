package cn.superhuang.data.scalpel.admin.app.service.model;

import lombok.Data;

import java.util.Map;

@Data
public class RestServiceTestResult {
    private Boolean success;
    private String msg;
    private String errorDetail;
    private String responseBody;
}
