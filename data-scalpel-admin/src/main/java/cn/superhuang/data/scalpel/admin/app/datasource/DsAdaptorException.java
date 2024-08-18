package cn.superhuang.data.scalpel.admin.app.datasource;

public class DsAdaptorException extends RuntimeException{
    public DsAdaptorException() {
    }

    public DsAdaptorException(String message) {
        super(message);
    }

    public DsAdaptorException(String message, Throwable cause) {
        super(message, cause);
    }

    public DsAdaptorException(Throwable cause) {
        super(cause);
    }

    public DsAdaptorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}