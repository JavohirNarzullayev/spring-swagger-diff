package uz.narzullayev.javohir.model.schema;

import uz.narzullayev.javohir.model.ChangedList;
import uz.narzullayev.javohir.model.DiffContext;
import uz.narzullayev.javohir.model.DiffResult;

import java.util.List;

public class ChangedEnum<T> extends ChangedList<T> {

  public ChangedEnum(List<T> oldValue, List<T> newValue, DiffContext context) {
    super(oldValue, newValue, context);
  }

  @Override
  public DiffResult isItemsChanged() {
    if (context.isRequest() && getMissing().isEmpty()
        || context.isResponse() && getIncreased().isEmpty()) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }
}
