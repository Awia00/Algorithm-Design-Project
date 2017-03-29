package minfill.data;

import java.util.Iterator;

public class EmptySet<T> implements Set<T> {
    private final static EmptySetIterator iterator = new EmptySetIterator();
    private static final EmptySet instance = new EmptySet();

    @SuppressWarnings("unchecked")
    public static <T> Set<T> instance() {
        return instance;
    }

    private EmptySet() {}

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean isProperSubsetOf(Set<T> other) {
        return !other.isEmpty();
    }

    @Override
    public boolean isSubsetOf(Set<T> other) {
        return true;
    }

    @Override
    public boolean contains(T element) {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Set<T> add(T element) {
        return new ImmutableSet<>(element);
    }

    @Override
    public Set<T> union(Set<T> other) {
        return other;
    }

    @Override
    public Set<T> intersect(Set<T> other) {
        return this;
    }

    @Override
    public Set<T> minus(Set<T> other) {
        return this;
    }

    @Override
    public Set<Set<T>> subsetsOfSizeAtMost(int size) {
        return new EmptySet<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<T> iterator() {
        return iterator;
    }

    @Override
    public String toString() {
        return "[]";
    }

    static class EmptySetIterator<T> implements Iterator<T> {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public T next() {
            throw new IllegalStateException("Empty iterator");
        }
    }
}
