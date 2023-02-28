package uz.narzullayev.javohir.compare;


import uz.narzullayev.javohir.model.Change;
import uz.narzullayev.javohir.model.Changed;
import uz.narzullayev.javohir.model.DiffContext;

public interface ExtensionDiff {

  ExtensionDiff setOpenApiDiff(OpenApiDiff openApiDiff);

  String getName();

  Changed diff(Change<?> extension, DiffContext context);

  default boolean isParentApplicable(
      Change.Type type, Object object, Object extension, DiffContext context) {
    return true;
  }
}
