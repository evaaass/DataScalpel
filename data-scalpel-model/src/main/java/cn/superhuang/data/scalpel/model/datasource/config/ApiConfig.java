package cn.superhuang.data.scalpel.model.datasource.config;


import cn.superhuang.data.scalpel.model.enumeration.DatasourceType;

public class ApiConfig extends DatasourceConfig {
    public static final String API_URL = "url";
    public static final String API_METHOD = "method";
    public static final String API_DATA_STRUCTURE = "dataStructure";
    public static final String API_JSON_PATH = "jsonPath";
    public static final String API_RES_TYPE = "responseType";
    public static final String API_BODY_TYPE = "bodyType";
    public static final String API_HEADER = "headers";
    public static final String API_BODY = "body";
    public static final String API_SCRIPT = "script";

    public ApiConfig(){
        setType(DatasourceType.API);
    }

    public String getUrl(){
        return getParams().get(API_URL);
    }
    public String getMethod(){
        return getParams().get(API_METHOD);
    }
    public String getDataStructure(){
        return getParams().get(API_DATA_STRUCTURE);
    }
    public String getJsonPath(){
        return getParams().get(API_JSON_PATH);
    }
    public String getResponseType(){
        return getParams().get(API_RES_TYPE);
    }
    public String getBodyType(){
        return getParams().get(API_BODY_TYPE);
    }
    public String getHeaders(){
        return getParams().get(API_HEADER);
    }
    public String getBody() {
        return getParams().get(API_BODY);
    }
    public String getScript() {
        return getParams().get(API_SCRIPT);
    }

    public String setUrl(String url){
        return getParams().put(API_URL, url);
    }
    public String setMethod(String method){
        return getParams().put(API_METHOD, method);
    }
    public String setDataStructure(String dataStructure){
        return getParams().put(API_DATA_STRUCTURE, dataStructure);
    }
    public String setJsonPath(String jsonPath){
        return getParams().put(API_JSON_PATH, jsonPath);
    }
    public String setResponseType(String responseType){
        return getParams().put(API_RES_TYPE, responseType);
    }
    public String setBodyType(String bodyType){
        return getParams().put(API_BODY_TYPE, bodyType);
    }
    public String setHeaders(String headers){
        return getParams().put(API_HEADER, headers);
    }
    public String setBody(String body){
        return getParams().put(API_BODY, body);
    }
    public String setScript(String script){
        return getParams().put(API_SCRIPT, script);
    }
}
