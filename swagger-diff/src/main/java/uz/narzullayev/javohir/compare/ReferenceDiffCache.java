package uz.narzullayev.javohir.compare;

import uz.narzullayev.javohir.model.Changed;
import uz.narzullayev.javohir.model.DiffContext;
import uz.narzullayev.javohir.model.deferred.DeferredChanged;
import uz.narzullayev.javohir.model.deferred.RealizedChanged;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public abstract class ReferenceDiffCache<C, D extends Changed> {
  private final Map<CacheKey, DeferredChanged<D>> refDiffMap;

  public ReferenceDiffCache() {
    this.refDiffMap = new HashMap<>();
  }

  private DeferredChanged<D> getFromCache(CacheKey cacheKey) {
    return refDiffMap.get(cacheKey);
  }

  private void addToCache(CacheKey cacheKey, DeferredChanged<D> changed) {
    refDiffMap.put(cacheKey, changed);
  }

  public DeferredChanged<D> cachedDiff(
      HashSet<String> refSet,
      C left,
      C right,
      String leftRef,
      String rightRef,
      DiffContext context) {
    boolean areBothRefParameters = leftRef != null && rightRef != null;
    if (areBothRefParameters) {
      CacheKey key = new CacheKey(leftRef, rightRef, context);
      DeferredChanged<D> changedFromRef = getFromCache(key);
      if (changedFromRef != null) {
        return changedFromRef;
      } else {
        String refKey = getRefKey(leftRef, rightRef);
        if (refSet.contains(refKey)) {
          return RealizedChanged.empty();
        } else {
          refSet.add(refKey);
          DeferredChanged<D> changed = computeDiff(refSet, left, right, context);
          addToCache(key, changed);
          refSet.remove(refKey);
          return changed;
        }
      }
    } else {
      return computeDiff(refSet, left, right, context);
    }
  }

  protected String getRefKey(String leftRef, String rightRef) {
    return leftRef + ":" + rightRef;
  }

  protected abstract DeferredChanged<D> computeDiff(
      HashSet<String> refSet, C left, C right, DiffContext context);
}
