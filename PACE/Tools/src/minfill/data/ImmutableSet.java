package minfill.data;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ImmutableSet<T> implements Set<T> {
    private final java.util.Set<T> inner;

    public ImmutableSet(T element) {
        inner = new HashSet<>(1);
        inner.add(element);
    }

    public ImmutableSet(Collection<T> elements) {
        this(new HashSet<>(elements));
    }

    private ImmutableSet(java.util.Set<T> elements) {
        if (elements.isEmpty()) throw new IllegalArgumentException("Empty iterable");
        inner = elements;
    }

    private Set<T> newSet(java.util.Set<T> elements) {
        if (elements.isEmpty()) return Set.empty();
        return new ImmutableSet<>(elements);
    }

    @Override
    public boolean isEmpty() {
        return inner.isEmpty();
    }

    @Override
    public boolean isProperSubsetOf(Set<T> other) {
        return size() < other.size() && isSubsetOf(other);
    }

    @Override
    public boolean isSubsetOf(Set<T> other) {
        if (size() > other.size()) return false;
        for (T element : inner) {
            if (!other.contains(element)) return false;
        }
        return true;
    }

    @Override
    public boolean contains(T element) {
        return inner.contains(element);
    }

    @Override
    public int size() {
        return inner.size();
    }

    @Override
    public Set<T> add(T element) {
        if (inner.contains(element)) return this;

        java.util.Set<T> copy = new HashSet<>(inner);
        copy.add(element);
        return newSet(copy);
    }

    @Override
    public Set<T> union(Set<T> other) {
        if (other.isEmpty()) return this;

        java.util.Set<T> copy = new HashSet<>(inner);

        for (T element : other) {
            copy.add(element);
        }
        return newSet(copy);
    }

    @Override
    public Set<T> intersect(Set<T> other) {
        if (other.isEmpty()) return other;

        java.util.Set<T> intersection = new HashSet<>();

        for (T element : other) {
            if (inner.contains(element)) {
                intersection.add(element);
            }
        }
        return new ImmutableSet<>(intersection);
    }

    @Override
    public Set<T> minus(Set<T> other) {
        if (other.isEmpty()) return this;

        java.util.Set<T> copy = new HashSet<>(inner);

        for (T element : other) {
            copy.remove(element);
        }

        return new ImmutableSet<>(copy);
    }

    @Override
    public Set<Set<T>> subsetsOfSizeAtMost(int size) {
        Pair<java.util.Set<Set<T>>, java.util.Set<Set<T>>> results = subsetHelper(size);

        return new ImmutableSet<>(results.o1);
    }

    private Pair<java.util.Set<Set<T>>, java.util.Set<Set<T>>> subsetHelper(int size) {
        if (size == 0) {
            java.util.Set<Set<T>> result = new HashSet<>();
            result.add(Set.empty());

            return new Pair<>(result, new HashSet<>(result));
        }

        Pair<java.util.Set<Set<T>>, java.util.Set<Set<T>>> previousResults = subsetHelper(size - 1);

        java.util.Set<Set<T>> myResults = new HashSet<>();

        for (T element : inner) {
            for (Set<T> prevSet : previousResults.o2) {
                if (!prevSet.contains(element)) myResults.add(prevSet.add(element));
            }
        }

        previousResults.o1.addAll(myResults);

        return new Pair<>(previousResults.o1, myResults);
    }

    @Override
    public Iterator<T> iterator() {
        return Collections.unmodifiableSet(inner).iterator();
    }

    @Override
    public String toString() {
        return inner.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImmutableSet<?> that = (ImmutableSet<?>) o;

        return inner.equals(that.inner);
    }

    @Override
    public int hashCode() {
        return inner.hashCode();
    }

    public static void main(String[] args) {
        for (int i = 1; i < 15; i++) {
            Set<Integer> elements = new ImmutableSet<>(IntStream.range(0, i).boxed().collect(Collectors.toSet()));

            Set<Set<Integer>> subsets = elements.subsetsOfSizeAtMost(i);

            assert (((int) Math.pow(2, i)) == subsets.size());
        }


        assert (
                new ImmutableSet<>(
                        IntStream.range(0, 3).boxed().collect(Collectors.toSet()))
                        .subsetsOfSizeAtMost(3)
                        .contains(
                                new ImmutableSet<>(Arrays.asList(0, 1, 2))));
    }
}
