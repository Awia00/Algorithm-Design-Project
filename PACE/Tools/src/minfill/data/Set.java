package minfill.data;

import org.jetbrains.annotations.Contract;

public interface Set<T> extends Iterable<T>  {
    @Contract(pure = true)
    boolean isEmpty();

    @Contract(pure = true)
    boolean isProperSubsetOf(Set<T> other);

    @Contract(pure = true)
    boolean isSubsetOf(Set<T> other);

    @Contract(pure = true)
    boolean contains(T element);

    @Contract(pure = true)
    int size();

    @Contract(pure = true)
    Set<T> add(T element);

    @Contract(pure = true)
    Set<T> remove(T element);

    @Contract(pure = true)
    Set<T> union(Set<T> other);

    @Contract(pure = true)
    Set<T> intersect(Set<T> other);

    @Contract(pure = true)
    Set<T> minus(Set<T> other);

    @Contract(pure = true)
    Set<Set<T>> subsetsOfSizeAtMost(int size);

    @Contract(pure = true)
    static <T> Set<T> empty() {
        return EmptySet.instance();
    }
}