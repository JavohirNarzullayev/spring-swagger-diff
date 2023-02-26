package uz.narzullayev.javohir.model;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

public interface ComposedChanged extends Changed {

  @NotNull
  List<Changed> getChangedElements();

  @NotNull
  DiffResult isCoreChanged();

  @Override
  default DiffResult isChanged() {
    DiffResult elementsResult = DiffResult.fromWeight(
            getChangedElements().stream()
                .filter(Objects::nonNull)
                .map(Changed::isChanged)
                .mapToInt(DiffResult::getWeight)
                .max()
                .orElse(0));
    if (isCoreChanged().getWeight() > elementsResult.getWeight()) {
      return isCoreChanged();
    } else {
      return elementsResult;
    }
  }
}
