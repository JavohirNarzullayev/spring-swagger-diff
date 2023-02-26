package uz.narzullayev.javohir;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

public class NoTriggerConfiguredFailureAnalyzer extends AbstractFailureAnalyzer<NoTriggerConfiguredException> {
    private static final String DESCRIPTION = "There is no configured in application.";
    private static final String ACTION = "- Disable scheduling auto configuration by setting scheduling.enabled=false\n"
            + "- Create a @Bean for " +  ", "
            + "\n"
            + "- Create " +
            " annotated with @";

    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, NoTriggerConfiguredException cause) {
        return new FailureAnalysis(DESCRIPTION, ACTION, cause);
    }
}
