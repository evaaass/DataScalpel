package cn.superhuang.data.scalpel.model.datasource.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties({"params"})
public class S3Config extends DatasourceConfig{
    public static final String S3_ACCESS_ID="type";
    public static final String S3_SECRET_KEY="db";
    public static final String S3_ENDPOINT="endpoint";
    public static final String S3_BUCKET="bucket";

    public String getAccessId(){
        return getParams().get(S3_ACCESS_ID);
    }
    public String getSecretKey(){
        return getParams().get(S3_SECRET_KEY);
    }
    public String getEndpoint(){
        return getParams().get(S3_ENDPOINT);
    }
    public String getBucket(){
        return getParams().get(S3_BUCKET);
    }

    public void setAccessId(String accessId){
        getParams().put(S3_ACCESS_ID, accessId);
    }
    public void setSecretKey(String secretKey){
        getParams().put(S3_SECRET_KEY, secretKey);
    }
    public void setEndpoint(String endpoint){
        getParams().put(S3_ENDPOINT, endpoint);
    }
    public void setBucket(String bucket){
        getParams().put(S3_BUCKET, bucket);
    }

}
