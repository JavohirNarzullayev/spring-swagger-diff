package uz.narzullayev.javohir.model.schema;

import uz.narzullayev.javohir.model.ChangedList;
import uz.narzullayev.javohir.model.DiffContext;
import uz.narzullayev.javohir.model.DiffResult;

import java.util.List;

public class ChangedRequired extends ChangedList<String> {

  public ChangedRequired(List<String> oldValue, List<String> newValue, DiffContext context) {
    super(oldValue, newValue, context);
  }

  @Override
  public DiffResult isItemsChanged() {
    if (context.isRequest() && getIncreased().isEmpty()
        || context.isResponse() && getMissing().isEmpty()) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }
}
