package minfill.graphs.adjacencymatrix;

import minfill.sets.Set;

import java.util.Map;

class FilteredMap<T> {
    final Set<T> allowedKeys;
    final Set<T> allowedValues;
    final Map<T, T> inner;


    public FilteredMap(Set<T> allowedKeys, Set<T> allowedValues, Map<T, T> inner) {
        this.allowedKeys = allowedKeys;
        this.allowedValues = allowedValues;
        this.inner = inner;
    }

    public boolean contains(T key) {
        return allowedKeys.contains(key) && allowedValues.contains(inner.get(key));
    }

    public Set<T> keySet() {
        return allowedKeys;
    }

    public T get(T key) {
        if (contains(key)) return inner.get(key);
        throw new IllegalArgumentException("key");
    }
}
