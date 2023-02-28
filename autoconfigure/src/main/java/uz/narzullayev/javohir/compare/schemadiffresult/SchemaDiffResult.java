package uz.narzullayev.javohir.compare.schemadiffresult;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Schema;
import uz.narzullayev.javohir.compare.ListDiff;
import uz.narzullayev.javohir.compare.MapKeyDiff;
import uz.narzullayev.javohir.compare.OpenApiDiff;
import uz.narzullayev.javohir.model.Change;
import uz.narzullayev.javohir.model.Changed;
import uz.narzullayev.javohir.model.ChangedSchema;
import uz.narzullayev.javohir.model.DiffContext;
import uz.narzullayev.javohir.model.deferred.DeferredBuilder;
import uz.narzullayev.javohir.model.deferred.DeferredChanged;
import uz.narzullayev.javohir.model.deferred.RecursiveSchemaSet;
import uz.narzullayev.javohir.model.schema.*;
import uz.narzullayev.javohir.utils.ChangedUtils;

import java.util.*;

import static java.util.Optional.ofNullable;
import static uz.narzullayev.javohir.utils.ChangedUtils.isChanged;

public class SchemaDiffResult {
  protected ChangedSchema changedSchema;
  protected OpenApiDiff openApiDiff;

  public SchemaDiffResult(OpenApiDiff openApiDiff) {
    this.openApiDiff = openApiDiff;
    this.changedSchema = new ChangedSchema();
  }

  public SchemaDiffResult(String type, OpenApiDiff openApiDiff) {
    this(openApiDiff);
    this.changedSchema.setType(type);
  }

  public <V extends Schema<X>, X> DeferredChanged<ChangedSchema> diff(
      RecursiveSchemaSet refSet,
      Components leftComponents,
      Components rightComponents,
      V left,
      V right,
      DiffContext context) {

    DeferredBuilder<Changed> builder = new DeferredBuilder<>();

    ChangedEnum<X> changedEnum =
        ListDiff.diff(new ChangedEnum<>(left.getEnum(), right.getEnum(), context));
    changedSchema
        .setContext(context)
        .setOldSchema(left)
        .setNewSchema(right)
        .setChangeDeprecated(
            !Boolean.TRUE.equals(left.getDeprecated())
                && Boolean.TRUE.equals(right.getDeprecated()))
        .setChangeTitle(!Objects.equals(left.getTitle(), right.getTitle()))
        .setRequired(
            ListDiff.diff(new ChangedRequired(left.getRequired(), right.getRequired(), context)))
        .setChangeDefault(!Objects.equals(left.getDefault(), right.getDefault()))
        .setEnumeration(changedEnum)
        .setChangeFormat(!Objects.equals(left.getFormat(), right.getFormat()))
        .setReadOnly(new ChangedReadOnly(left.getReadOnly(), right.getReadOnly(), context))
        .setWriteOnly(new ChangedWriteOnly(left.getWriteOnly(), right.getWriteOnly(), context))
        .setMaxLength(new ChangedMaxLength(left.getMaxLength(), right.getMaxLength(), context));
    builder
        .with(
            openApiDiff
                .getExtensionsDiff()
                .diff(left.getExtensions(), right.getExtensions(), context))
        .ifPresent(changedSchema::setExtensions);
    builder
        .with(
            openApiDiff
                .getMetadataDiff()
                .diff(left.getDescription(), right.getDescription(), context))
        .ifPresent(changedSchema::setDescription);
    Map<String, Schema> leftProperties = left.getProperties();
    Map<String, Schema> rightProperties = right.getProperties();
    MapKeyDiff<String, Schema> propertyDiff = MapKeyDiff.diff(leftProperties, rightProperties);
    for (String key : propertyDiff.getSharedKey()) {
      builder
          .with(
              openApiDiff
                  .getSchemaDiff()
                  .diff(
                      refSet,
                      leftProperties.get(key),
                      rightProperties.get(key),
                      required(context, key, right.getRequired())))
          .ifPresent(
              changedSchema1 -> changedSchema.getChangedProperties().put(key, changedSchema1));
    }
    compareAdditionalProperties(refSet, left, right, context, builder);
    changedSchema
        .getIncreasedProperties()
        .putAll(filterProperties(Change.Type.ADDED, propertyDiff.getIncreased(), context));
    changedSchema
        .getMissingProperties()
        .putAll(filterProperties(Change.Type.REMOVED, propertyDiff.getMissing(), context));
    return builder.build().mapOptional(values -> isApplicable(context));
  }

  protected Optional<ChangedSchema> isApplicable(DiffContext context) {
    if (changedSchema.getReadOnly().isUnchanged()
        && changedSchema.getWriteOnly().isUnchanged()
        && !isPropertyApplicable(changedSchema.getNewSchema(), context)) {
      return Optional.empty();
    }
    return ChangedUtils.isChanged(changedSchema);
  }

  private Map<String, Schema<?>> filterProperties(
      Change.Type type, Map<String, Schema> properties, DiffContext context) {
    Map<String, Schema<?>> result = new LinkedHashMap<>();
    for (Map.Entry<String, Schema> entry : properties.entrySet()) {
      if (isPropertyApplicable(entry.getValue(), context)
          && openApiDiff
              .getExtensionsDiff()
              .isParentApplicable(
                  type,
                  entry.getValue(),
                  ofNullable(entry.getValue().getExtensions()).orElse(new LinkedHashMap<>()),
                  context)) {
        result.put(entry.getKey(), entry.getValue());
      } else {
        // Child property is not applicable, so required cannot be applied
        changedSchema.getRequired().getIncreased().remove(entry.getKey());
      }
    }
    return result;
  }

  private boolean isPropertyApplicable(Schema<?> schema, DiffContext context) {
    return !(context.isResponse() && Boolean.TRUE.equals(schema.getWriteOnly()))
        && !(context.isRequest() && Boolean.TRUE.equals(schema.getReadOnly()));
  }

  private DiffContext required(DiffContext context, String key, List<String> required) {
    return context.copyWithRequired(required != null && required.contains(key));
  }

  private void compareAdditionalProperties(
      RecursiveSchemaSet refSet,
      Schema<?> leftSchema,
      Schema<?> rightSchema,
      DiffContext context,
      DeferredBuilder<Changed> builder) {
    Object left = leftSchema.getAdditionalProperties();
    Object right = rightSchema.getAdditionalProperties();
    if (left instanceof Schema || right instanceof Schema) {
      Schema<?> leftAdditionalSchema = left instanceof Schema ? (Schema<?>) left : null;
      Schema<?> rightAdditionalSchema = right instanceof Schema ? (Schema<?>) right : null;
      ChangedSchema apChangedSchema =
          new ChangedSchema()
              .setContext(context)
              .setOldSchema(leftAdditionalSchema)
              .setNewSchema(rightAdditionalSchema);
      if (left != null && right != null) {
        DeferredChanged<ChangedSchema> addPropChangedSchemaOP =
            openApiDiff
                .getSchemaDiff()
                .diff(
                    refSet,
                    leftAdditionalSchema,
                    rightAdditionalSchema,
                    context.copyWithRequired(false));
        builder
            .with(addPropChangedSchemaOP)
            .whenSet(
                optional -> {
                  ChangedSchema apc = optional.orElse(apChangedSchema);
                  ChangedUtils.isChanged(apc).ifPresent(changedSchema::setAddProp);
                });
      } else {
        ChangedUtils.isChanged(apChangedSchema).ifPresent(changedSchema::setAddProp);
      }
    }
  }

  public ChangedSchema getChangedSchema() {
    return this.changedSchema;
  }

  public OpenApiDiff getOpenApiDiff() {
    return this.openApiDiff;
  }
}
