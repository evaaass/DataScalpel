package cn.superhuang.data.scalpel.model.datamasking;

import cn.hutool.crypto.digest.MD5;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class DataMaskingRuleMD5 extends DataMaskingRule implements Serializable {
    @Serial
    private static final long serialVersionUID = 2906257806403732280L;

    @Override
    public String mask(String data) {
        return MD5.create().digestHex(data);
    }

    public static void main(String[] args) {
        System.out.println(MD5.create().digestHex("111111"));
    }
}
