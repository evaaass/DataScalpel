package cn.superhuang.data.scalpel.admin.web.resource.errors;

import java.io.Serial;
import java.io.Serializable;

public class FieldErrorVM implements Serializable {

    @Serial
    private static final long serialVersionUID = 3525922474573820975L;
    private final String objectName;

    private final String field;

    private final String message;

    public FieldErrorVM(String dto, String field, String message) {
        this.objectName = dto;
        this.field = field;
        this.message = message;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }
}
