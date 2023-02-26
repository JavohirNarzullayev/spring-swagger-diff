package uz.narzullayev.javohir.compare;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.headers.Header;
import uz.narzullayev.javohir.model.Changed;
import uz.narzullayev.javohir.model.ChangedHeader;
import uz.narzullayev.javohir.model.DiffContext;
import uz.narzullayev.javohir.model.deferred.DeferredBuilder;
import uz.narzullayev.javohir.model.deferred.DeferredChanged;
import uz.narzullayev.javohir.utils.RefPointer;
import uz.narzullayev.javohir.utils.RefType;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

public class HeaderDiff extends ReferenceDiffCache<Header, ChangedHeader> {
  private static final RefPointer<Header> refPointer = new RefPointer<>(RefType.HEADERS);
  private final OpenApiDiff openApiDiff;
  private final Components leftComponents;
  private final Components rightComponents;

  public HeaderDiff(OpenApiDiff openApiDiff) {
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

  public DeferredChanged<ChangedHeader> diff(Header left, Header right, DiffContext context) {
    return cachedDiff(new HashSet<>(), left, right, left.get$ref(), right.get$ref(), context);
  }

  @Override
  protected DeferredChanged<ChangedHeader> computeDiff(
      HashSet<String> refSet, Header left, Header right, DiffContext context) {
    left = refPointer.resolveRef(leftComponents, left, left.get$ref());
    right = refPointer.resolveRef(rightComponents, right, right.get$ref());

    DeferredBuilder<Changed> builder = new DeferredBuilder<>();
    ChangedHeader changedHeader =
        new ChangedHeader(left, right, context)
            .setRequired(getBooleanDiff(left.getRequired(), right.getRequired()))
            .setDeprecated(
                !Boolean.TRUE.equals(left.getDeprecated())
                    && Boolean.TRUE.equals(right.getDeprecated()))
            .setStyle(!Objects.equals(left.getStyle(), right.getStyle()))
            .setExplode(getBooleanDiff(left.getExplode(), right.getExplode()));
    builder
        .with(
            openApiDiff
                .getMetadataDiff()
                .diff(left.getDescription(), right.getDescription(), context))
        .ifPresent(changedHeader::setDescription);
    builder
        .with(
            openApiDiff
                .getSchemaDiff()
                .diff(left.getSchema(), right.getSchema(), context.copyWithRequired(true)))
        .ifPresent(changedHeader::setSchema);
    builder
        .with(openApiDiff.getContentDiff().diff(left.getContent(), right.getContent(), context))
        .ifPresent(changedHeader::setContent);
    builder
        .with(
            openApiDiff
                .getExtensionsDiff()
                .diff(left.getExtensions(), right.getExtensions(), context))
        .ifPresent(changedHeader::setExtensions);
    return builder.buildIsChanged(changedHeader);
  }

  private boolean getBooleanDiff(Boolean left, Boolean right) {
    boolean leftRequired = Optional.ofNullable(left).orElse(Boolean.FALSE);
    boolean rightRequired = Optional.ofNullable(right).orElse(Boolean.FALSE);
    return leftRequired != rightRequired;
  }
}
