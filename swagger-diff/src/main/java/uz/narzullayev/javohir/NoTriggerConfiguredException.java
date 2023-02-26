package uz.narzullayev.javohir;

public class NoTriggerConfiguredException extends RuntimeException {
    public NoTriggerConfiguredException() {
        super();
    }

    public NoTriggerConfiguredException(String message) {
        super(message);
    }

    public NoTriggerConfiguredException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoTriggerConfiguredException(Throwable cause) {
        super(cause);
    }
}
