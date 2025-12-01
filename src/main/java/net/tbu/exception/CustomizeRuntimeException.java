package net.tbu.exception;


/**
 * 自定义异常
 */
public class CustomizeRuntimeException extends RuntimeException {
    private final String message;

    public CustomizeRuntimeException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
