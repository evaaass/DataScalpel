package cn.superhuang.data.scalpel.model.service;

import lombok.Data;

import java.util.List;


@Data
public class ServiceTestResult {
    private Boolean success;
    private String msg;
    private String errorDetail;

    private String requestBody;
    private String responseBody;
    private String requestBodyDefinition;
    private String responseBodyDefinition;
}
