package cn.superhuang.data.scalpel.model.datamasking;

import lombok.Data;

@Data
public abstract class DataMaskingRule {

    private String type;


    public abstract String mask(String data);
}
