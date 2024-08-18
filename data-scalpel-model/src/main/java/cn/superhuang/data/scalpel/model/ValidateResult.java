package cn.superhuang.data.scalpel.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ValidateResult {
    private Boolean valid;
    private String nodeId;
    private List<String> messages;
    private List<ValidateErrorDetail> errors = new ArrayList<>();


    public void addError(ValidateErrorDetail nodeConfigError) {
        errors.add(nodeConfigError);
    }

    public void addError(String target, String content) {
        ValidateErrorDetail validateErrorDetail=new ValidateErrorDetail();
        validateErrorDetail.setTarget(target);
        validateErrorDetail.setContent(content);
        errors.add(validateErrorDetail);
    }
}
