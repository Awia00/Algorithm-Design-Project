package minfill.data;

public interface Set<T> extends Iterable<T> {
    boolean isEmpty();
    boolean isProperSubsetOf(Set<T> other);
    boolean isSubsetOf(Set<T> other);
    boolean contains(T element);
    int size();
    Set<T> add(T element);
    Set<T> union(Set<T> other);
    Set<T> intersect(Set<T> other);
    Set<T> minus(Set<T> other);
    Set<Set<T>> subsetsOfSizeAtMost(int size);

    static <T> Set<T> empty() {
        return EmptySet.instance();
    }
}
