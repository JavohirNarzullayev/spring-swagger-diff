package uz.narzullayev.javohir.compare;


import uz.narzullayev.javohir.model.ChangedMetadata;
import uz.narzullayev.javohir.model.DiffContext;

import java.util.Optional;

import static uz.narzullayev.javohir.utils.ChangedUtils.isChanged;


public class MetadataDiff {
  public MetadataDiff(OpenApiDiff openApiDiff) {}

  public Optional<ChangedMetadata> diff(String left, String right, DiffContext context) {
    return isChanged(new ChangedMetadata().setLeft(left).setRight(right));
  }
}
