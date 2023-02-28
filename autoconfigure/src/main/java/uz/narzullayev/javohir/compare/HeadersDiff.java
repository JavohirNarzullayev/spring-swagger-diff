package uz.narzullayev.javohir.compare;

import io.swagger.v3.oas.models.headers.Header;
import uz.narzullayev.javohir.model.ChangedHeader;
import uz.narzullayev.javohir.model.ChangedHeaders;
import uz.narzullayev.javohir.model.DiffContext;
import uz.narzullayev.javohir.model.deferred.DeferredBuilder;
import uz.narzullayev.javohir.model.deferred.DeferredChanged;
import uz.narzullayev.javohir.utils.ChangedUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static uz.narzullayev.javohir.utils.ChangedUtils.isChanged;

public class HeadersDiff {
  private final OpenApiDiff openApiDiff;

  public HeadersDiff(OpenApiDiff openApiDiff) {
    this.openApiDiff = openApiDiff;
  }

  public DeferredChanged<ChangedHeaders> diff(
      Map<String, Header> left, Map<String, Header> right, DiffContext context) {
    MapKeyDiff<String, Header> headerMapDiff = MapKeyDiff.diff(left, right);
    List<String> sharedHeaderKeys = headerMapDiff.getSharedKey();

    Map<String, ChangedHeader> changed = new LinkedHashMap<>();
    DeferredBuilder<ChangedHeader> builder = new DeferredBuilder<>();
    for (String headerKey : sharedHeaderKeys) {
      Header oldHeader = left.get(headerKey);
      Header newHeader = right.get(headerKey);
      builder
          .with(openApiDiff.getHeaderDiff().diff(oldHeader, newHeader, context))
          .ifPresent(changedHeader -> changed.put(headerKey, changedHeader));
    }
    return builder
        .build()
        .mapOptional(
            value ->
                ChangedUtils.isChanged(
                    new ChangedHeaders(left, right, context)
                        .setIncreased(headerMapDiff.getIncreased())
                        .setMissing(headerMapDiff.getMissing())
                        .setChanged(changed)));
  }
}
