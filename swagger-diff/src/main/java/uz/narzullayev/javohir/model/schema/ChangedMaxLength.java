package uz.narzullayev.javohir.model.schema;

import uz.narzullayev.javohir.model.Changed;
import uz.narzullayev.javohir.model.DiffContext;
import uz.narzullayev.javohir.model.DiffResult;

import java.util.Objects;

public final class ChangedMaxLength implements Changed {
  private final Integer oldValue;
  private final Integer newValue;
  private final DiffContext context;

  @Override
  public DiffResult isChanged() {
    if (Objects.equals(oldValue, newValue)) {
      return DiffResult.NO_CHANGES;
    }
    if (context.isRequest() && (newValue == null || oldValue != null && oldValue <= newValue)
        || context.isResponse() && (oldValue == null || newValue != null && newValue <= oldValue)) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }

  public ChangedMaxLength(
      final Integer oldValue, final Integer newValue, final DiffContext context) {
    this.oldValue = oldValue;
    this.newValue = newValue;
    this.context = context;
  }

  public Integer getOldValue() {
    return this.oldValue;
  }

  public Integer getNewValue() {
    return this.newValue;
  }

  public DiffContext getContext() {
    return this.context;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangedMaxLength that = (ChangedMaxLength) o;
    return Objects.equals(oldValue, that.oldValue)
        && Objects.equals(newValue, that.newValue)
        && Objects.equals(context, that.context);
  }

  @Override
  public int hashCode() {
    return Objects.hash(oldValue, newValue, context);
  }

  @Override
  public String toString() {
    return "ChangedMaxLength(oldValue="
        + this.getOldValue()
        + ", newValue="
        + this.getNewValue()
        + ", context="
        + this.getContext()
        + ")";
  }
}
