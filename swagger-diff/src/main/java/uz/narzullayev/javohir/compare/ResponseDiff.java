package uz.narzullayev.javohir.compare;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.responses.ApiResponse;

import uz.narzullayev.javohir.model.Changed;
import uz.narzullayev.javohir.model.ChangedResponse;
import uz.narzullayev.javohir.model.DiffContext;
import uz.narzullayev.javohir.model.deferred.DeferredBuilder;
import uz.narzullayev.javohir.model.deferred.DeferredChanged;
import uz.narzullayev.javohir.model.deferred.RealizedChanged;
import uz.narzullayev.javohir.utils.RefPointer;
import uz.narzullayev.javohir.utils.RefType;

import java.util.HashSet;
import java.util.Optional;

public class ResponseDiff extends ReferenceDiffCache<ApiResponse, ChangedResponse> {
  private static final RefPointer<ApiResponse> refPointer = new RefPointer<>(RefType.RESPONSES);
  private final OpenApiDiff openApiDiff;
  private final Components leftComponents;
  private final Components rightComponents;

  public ResponseDiff(OpenApiDiff openApiDiff) {
    this.openApiDiff = openApiDiff;
    this.leftComponents =
        openApiDiff.getOldSpecOpenApi() != null
            ? openApiDiff.getOldSpecOpenApi().getComponents()
            : null;
    this.rightComponents =
        openApiDiff.getNewSpecOpenApi() != null
            ? openApiDiff.getNewSpecOpenApi().getComponents()
            : null;
  }

  public DeferredChanged<ChangedResponse> diff(
      ApiResponse left, ApiResponse right, DiffContext context) {
    if (left == null && right == null) {
      return new RealizedChanged<>(Optional.empty());
    }
    if ((left == null && right != null) || (left != null && right == null)) {
      return new RealizedChanged<>(Optional.of(new ChangedResponse(left, right, context)));
    }
    return cachedDiff(new HashSet<>(), left, right, left.get$ref(), right.get$ref(), context);
  }

  @Override
  protected DeferredChanged<ChangedResponse> computeDiff(
      HashSet<String> refSet, ApiResponse left, ApiResponse right,
      DiffContext context) {
    left = refPointer.resolveRef(leftComponents, left, left.get$ref());
    right = refPointer.resolveRef(rightComponents, right, right.get$ref());

    DeferredBuilder<Changed> builder = new DeferredBuilder<>();
    ChangedResponse changedResponse = new ChangedResponse(left, right, context);
    builder
        .with(
            openApiDiff
                .getMetadataDiff()
                .diff(left.getDescription(), right.getDescription(), context))
        .ifPresent(changedResponse::setDescription);
    builder
        .with(openApiDiff.getContentDiff().diff(left.getContent(), right.getContent(), context))
        .ifPresent(changedResponse::setContent);
    builder
        .with(openApiDiff.getHeadersDiff().diff(left.getHeaders(), right.getHeaders(), context))
        .ifPresent(changedResponse::setHeaders);
    builder
        .with(
            openApiDiff
                .getExtensionsDiff()
                .diff(left.getExtensions(), right.getExtensions(), context))
        .ifPresent(changedResponse::setExtensions);

    return builder.buildIsChanged(changedResponse);
  }
}
