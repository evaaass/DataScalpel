package cn.superhuang.data.scalpel.actuator;

public class ActuatorException extends RuntimeException{
    public ActuatorException() {
        super();
    }

    public ActuatorException(String message) {
        super(message);
    }

    public ActuatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActuatorException(Throwable cause) {
        super(cause);
    }

    protected ActuatorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
