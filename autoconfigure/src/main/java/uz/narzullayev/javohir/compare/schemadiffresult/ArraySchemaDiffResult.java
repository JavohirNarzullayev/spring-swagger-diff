package uz.narzullayev.javohir.compare.schemadiffresult;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import uz.narzullayev.javohir.compare.OpenApiDiff;
import uz.narzullayev.javohir.model.ChangedSchema;
import uz.narzullayev.javohir.model.DiffContext;
import uz.narzullayev.javohir.model.deferred.DeferredChanged;
import uz.narzullayev.javohir.model.deferred.RecursiveSchemaSet;

public class ArraySchemaDiffResult extends SchemaDiffResult {
  public ArraySchemaDiffResult(OpenApiDiff openApiDiff) {
    super("array", openApiDiff);
  }

  @Override
  public <T extends Schema<X>, X> DeferredChanged<ChangedSchema> diff(
      RecursiveSchemaSet refSet,
      Components leftComponents,
      Components rightComponents,
      T left,
      T right,
      DiffContext context) {
    ArraySchema leftArraySchema = (ArraySchema) left;
    ArraySchema rightArraySchema = (ArraySchema) right;

    DeferredChanged<ChangedSchema> superSchemaDiff =
        super.diff(refSet, leftComponents, rightComponents, left, right, context)
            .flatMap(
                changeSchemaOptional -> {
                  DeferredChanged<ChangedSchema> itemsDiff =
                      openApiDiff
                          .getSchemaDiff()
                          .diff(
                              refSet,
                              leftArraySchema.getItems(),
                              rightArraySchema.getItems(),
                              context.copyWithRequired(true));
                  itemsDiff.ifPresent(changedSchema::setItems);
                  return itemsDiff;
                });

    return superSchemaDiff.mapOptional(schemaOptional -> isApplicable(context));
  }
}
