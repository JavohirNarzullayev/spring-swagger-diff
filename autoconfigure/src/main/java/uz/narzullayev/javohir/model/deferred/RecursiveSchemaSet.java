package uz.narzullayev.javohir.model.deferred;

import uz.narzullayev.javohir.compare.CacheKey;

import java.util.HashSet;

public class RecursiveSchemaSet {
  HashSet<String> leftKeys = new HashSet<>();
  HashSet<String> rightKeys = new HashSet<>();

  public boolean contains(CacheKey key) {
    return leftKeys.contains(key.getLeft()) || rightKeys.contains(key.getRight());
  }

  public void put(CacheKey key) {
    leftKeys.add(key.getLeft());
    leftKeys.add(key.getRight());
  }
}
