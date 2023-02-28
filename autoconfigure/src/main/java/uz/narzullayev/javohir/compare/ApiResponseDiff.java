package uz.narzullayev.javohir.compare;

import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import uz.narzullayev.javohir.model.Changed;
import uz.narzullayev.javohir.model.ChangedApiResponse;
import uz.narzullayev.javohir.model.ChangedResponse;
import uz.narzullayev.javohir.model.DiffContext;
import uz.narzullayev.javohir.model.deferred.DeferredBuilder;
import uz.narzullayev.javohir.model.deferred.DeferredChanged;


import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ApiResponseDiff {
  private final OpenApiDiff openApiDiff;

  public ApiResponseDiff(OpenApiDiff openApiDiff) {
    this.openApiDiff = openApiDiff;
  }

  public DeferredChanged<ChangedApiResponse> diff(
      @Nullable ApiResponses left, @Nullable ApiResponses right, DiffContext context) {
    MapKeyDiff<String, ApiResponse> responseMapKeyDiff = MapKeyDiff.diff(left, right);
    List<String> sharedResponseCodes = responseMapKeyDiff.getSharedKey();
    Map<String, ChangedResponse> resps = new LinkedHashMap<>();
    DeferredBuilder<Changed> builder = new DeferredBuilder<>();

    for (String responseCode : sharedResponseCodes) {
      builder
          .with(
              openApiDiff
                  .getResponseDiff()
                  .diff(
                      left != null ? left.get(responseCode) : null,
                      right != null ? right.get(responseCode) : null,
                      context))
          .ifPresent(changedResponse -> resps.put(responseCode, changedResponse));
    }
    ChangedApiResponse changedApiResponse =
        new ChangedApiResponse(left, right, context)
            .setIncreased(responseMapKeyDiff.getIncreased())
            .setMissing(responseMapKeyDiff.getMissing())
            .setChanged(resps);
    builder
        .with(
            openApiDiff
                .getExtensionsDiff()
                .diff(
                    left != null ? left.getExtensions() : null,
                    right != null ? right.getExtensions() : null,
                    context))
        .ifPresent(changedApiResponse::setExtensions);
    return builder.buildIsChanged(changedApiResponse);
  }
}
