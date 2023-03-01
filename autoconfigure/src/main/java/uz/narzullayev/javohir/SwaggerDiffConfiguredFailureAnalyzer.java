package uz.narzullayev.javohir;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

public class SwaggerDiffConfiguredFailureAnalyzer extends AbstractFailureAnalyzer<SwaggerDiffException> {
    private String getDescription(SwaggerDiffException ex) {
        return String.format("Swagger dif exception %s", ex.getMessage());
    }

    private String getAction(SwaggerDiffException ex) {
        return String.format("Swagger diff config %s", ex.getMessage());
    }

    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, SwaggerDiffException cause) {
        return new FailureAnalysis(getDescription(cause), getAction(cause), cause);
    }
}
