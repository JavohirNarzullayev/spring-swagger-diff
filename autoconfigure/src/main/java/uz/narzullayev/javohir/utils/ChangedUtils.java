package uz.narzullayev.javohir.utils;

import uz.narzullayev.javohir.model.Changed;

import java.util.Optional;

public class ChangedUtils {

  private ChangedUtils() {}

  public static boolean isUnchanged(Changed changed) {
    return changed == null || changed.isUnchanged();
  }

  public static boolean isCompatible(Changed changed) {
    return changed == null || changed.isCompatible();
  }

  public static <T extends Changed> Optional<T> isChanged(T changed) {
    if (isUnchanged(changed)) {
      return Optional.empty();
    }
    return Optional.of(changed);
  }
}
