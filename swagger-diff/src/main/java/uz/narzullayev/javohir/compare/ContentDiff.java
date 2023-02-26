package uz.narzullayev.javohir.compare;

import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import uz.narzullayev.javohir.model.Changed;
import uz.narzullayev.javohir.model.ChangedContent;
import uz.narzullayev.javohir.model.ChangedMediaType;
import uz.narzullayev.javohir.model.DiffContext;
import uz.narzullayev.javohir.model.deferred.DeferredBuilder;
import uz.narzullayev.javohir.model.deferred.DeferredChanged;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static uz.narzullayev.javohir.utils.ChangedUtils.isChanged;
import static uz.narzullayev.javohir.utils.ChangedUtils.isUnchanged;

public class ContentDiff {

  private final OpenApiDiff openApiDiff;

  public ContentDiff(OpenApiDiff openApiDiff) {
    this.openApiDiff = openApiDiff;
  }

  public DeferredChanged<ChangedContent> diff(Content left, Content right, DiffContext context) {
    DeferredBuilder<Changed> builder = new DeferredBuilder<>();

    MapKeyDiff<String, MediaType> mediaTypeDiff = MapKeyDiff.diff(left, right);
    List<String> sharedMediaTypes = mediaTypeDiff.getSharedKey();
    Map<String, ChangedMediaType> changedMediaTypes = new LinkedHashMap<>();

    sharedMediaTypes.stream()
        .forEach(
            mediaTypeKey -> {
              MediaType oldMediaType = left.get(mediaTypeKey);
              MediaType newMediaType = right.get(mediaTypeKey);

              ChangedMediaType changedMediaType =
                  new ChangedMediaType(oldMediaType.getSchema(), newMediaType.getSchema(), context);

              builder
                  .with(
                      openApiDiff
                          .getSchemaDiff()
                          .diff(
                              oldMediaType.getSchema(),
                              newMediaType.getSchema(),
                              context.copyWithRequired(true)))
                  .ifPresent(
                      value -> {
                        changedMediaType.setSchema(value);
                        if (!isUnchanged(changedMediaType)) {
                          changedMediaTypes.put(mediaTypeKey, changedMediaType);
                        }
                      });
            });

    return builder
        .build()
        .mapOptional(
            value ->
                isChanged(
                    new ChangedContent(left, right, context)
                        .setIncreased(mediaTypeDiff.getIncreased())
                        .setMissing(mediaTypeDiff.getMissing())
                        .setChanged(changedMediaTypes)));
  }
}
