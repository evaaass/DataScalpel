package cn.superhuang.data.scalpel.admin.config;

import java.io.Serial;

public class RequestParamsErrorException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 8024355151926456919L;

    public RequestParamsErrorException() {
        super();
    }

    public RequestParamsErrorException(String message) {
        super(message);
    }

    public RequestParamsErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestParamsErrorException(Throwable cause) {
        super(cause);
    }

    protected RequestParamsErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
