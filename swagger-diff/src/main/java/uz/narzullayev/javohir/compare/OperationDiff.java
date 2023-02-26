package uz.narzullayev.javohir.compare;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.narzullayev.javohir.model.*;
import uz.narzullayev.javohir.model.deferred.DeferredBuilder;
import uz.narzullayev.javohir.model.deferred.DeferredChanged;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static uz.narzullayev.javohir.utils.ChangedUtils.isChanged;


public class OperationDiff {
  private static final Logger log = LoggerFactory.getLogger(OperationDiff.class);

  private final OpenApiDiff openApiDiff;

  public OperationDiff(OpenApiDiff openApiDiff) {
    this.openApiDiff = openApiDiff;
  }

  public DeferredChanged<ChangedOperation> diff(
      Operation oldOperation, Operation newOperation, DiffContext context) {

    DeferredBuilder<Changed> builder = new DeferredBuilder<>();
    ChangedOperation changedOperation =
        new ChangedOperation(context.getUrl(), context.getMethod(), oldOperation, newOperation);

    log.debug(
        "Diff operation {} {}", changedOperation.getPathUrl(), changedOperation.getHttpMethod());

    builder
        .with(
            openApiDiff
                .getMetadataDiff()
                .diff(oldOperation.getSummary(), newOperation.getSummary(), context))
        .ifPresent(changedOperation::setSummary);
    builder
        .with(
            openApiDiff
                .getMetadataDiff()
                .diff(oldOperation.getDescription(), newOperation.getDescription(), context))
        .ifPresent(changedOperation::setDescription);
    builder
        .with(
            openApiDiff
                .getMetadataDiff()
                .diff(oldOperation.getOperationId(), newOperation.getOperationId(), context))
        .ifPresent(changedOperation::setOperationId);
    changedOperation.setDeprecated(
        !Boolean.TRUE.equals(oldOperation.getDeprecated())
            && Boolean.TRUE.equals(newOperation.getDeprecated()));

    if (oldOperation.getRequestBody() != null || newOperation.getRequestBody() != null) {
      builder
          .with(
              openApiDiff
                  .getRequestBodyDiff()
                  .diff(
                      oldOperation.getRequestBody(),
                      newOperation.getRequestBody(),
                      context.copyAsRequest()))
          .ifPresent(changedOperation::setRequestBody);
    }

    builder
        .with(
            openApiDiff
                .getParametersDiff()
                .diff(oldOperation.getParameters(), newOperation.getParameters(), context))
        .ifPresent(
            params -> {
              removePathParameters(context.getParameters(), params);
              changedOperation.setParameters(params);
            });

    if (oldOperation.getResponses() != null || newOperation.getResponses() != null) {
      builder
          .with(
              openApiDiff
                  .getApiResponseDiff()
                  .diff(
                      oldOperation.getResponses(),
                      newOperation.getResponses(),
                      context.copyAsResponse()))
          .ifPresent(
              responses -> {
                log.debug(
                    "operation "
                        + changedOperation.getPathUrl()
                        + " "
                        + changedOperation.getHttpMethod()
                        + " setting api responses "
                        + responses.getChangedElements().stream()
                            .filter(Objects::nonNull)
                            .map(Changed::isChanged)
                            .filter(Objects::nonNull)
                            .map(Enum::toString)
                            .collect(Collectors.joining(",")));
                changedOperation.setApiResponses(responses);
              });
    }

    if (oldOperation.getSecurity() != null || newOperation.getSecurity() != null) {
      builder
          .with(
              openApiDiff
                  .getSecurityRequirementsDiff()
                  .diff(oldOperation.getSecurity(), newOperation.getSecurity(), context))
          .ifPresent(changedOperation::setSecurityRequirements);
    }

    builder
        .with(
            openApiDiff
                .getExtensionsDiff()
                .diff(oldOperation.getExtensions(), newOperation.getExtensions(), context))
        .ifPresent(changedOperation::setExtensions);

    return builder
        .build()
        .mapOptional(
            value -> {
              Optional<ChangedOperation> changed = isChanged(changedOperation);
              log.debug(
                  "Is changed operation "
                      + changedOperation.getPathUrl()
                      + " "
                      + changedOperation.getHttpMethod()
                      + " changed: "
                      + changed.map(ComposedChanged::isChanged).orElse(null));
              return changed;
            });
  }

  public void removePathParameters(Map<String, String> pathParameters, ChangedParameters params) {
    pathParameters.forEach(
        (oldParam, newParam) -> {
          removePathParameter(oldParam, params.getMissing());
          removePathParameter(newParam, params.getIncreased());
        });
  }

  public void removePathParameter(String name, List<Parameter> parameters) {
    parameters.stream()
        .filter(p -> "path".equals(p.getIn()) && name.equals(p.getName()))
        .findFirst()
        .ifPresent(parameters::remove);
  }
}
