package uz.narzullayev.javohir;

public class SwaggerDiffException extends RuntimeException {
    public SwaggerDiffException() {
        super();
    }

    public SwaggerDiffException(String message) {
        super(message);
    }

    public SwaggerDiffException(String message, Throwable cause) {
        super(message, cause);
    }

    public SwaggerDiffException(Throwable cause) {
        super(cause);
    }
}
