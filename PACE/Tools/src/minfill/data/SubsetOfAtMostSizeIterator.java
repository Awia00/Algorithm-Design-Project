package minfill.data;

import java.util.*;

public class SubsetOfAtMostSizeIterator<T extends Comparable<T>> implements Iterator<Set<T>> {
    private final List<T> elements;
    private final Iterator<Set<T>> inner;
    private final int maxSize;

    private int index;
    private Set<T> sub;
    private boolean nextTaken = true;
    private Set<T> next;

    @SuppressWarnings("unchecked")
    public SubsetOfAtMostSizeIterator(Set<T> elements, int maxSize) {
        this.elements = new ArrayList<>(elements.size());
        for (T element : elements) {
            this.elements.add(element);
        }
        Collections.sort(this.elements);

        if (maxSize == 2) {
            inner = new SetIterator<>(this.elements);
        } else if (maxSize > 2) {
            inner = new SubsetOfAtMostSizeIterator<>(this.elements, maxSize - 1);
        } else {
            throw new IllegalArgumentException("maxSize");
        }

        index = elements.size();
        this.maxSize = maxSize;
    }

    private SubsetOfAtMostSizeIterator(List<T> elements, int maxSize) {
        this.elements = elements;
        if (maxSize == 2) {
            inner = new SetIterator<>(this.elements);
        } else if (maxSize > 2) {
            inner = new SubsetOfAtMostSizeIterator<>(this.elements, maxSize - 1);
        } else {
            throw new IllegalArgumentException("maxSize");
        }

        index = elements.size();
        this.maxSize = maxSize;
    }

    @Override
    public boolean hasNext() {
        if (!nextTaken) return next != null;
        nextTaken = false;

        do {
            // Take new sub-element
            if ((sub == null || sub.size() < maxSize - 1) && inner.hasNext()) {
                sub = inner.next();

                // Find largest element already contained.
                for (int i = elements.size() - 1; i >= 0; i--) {
                    T element = elements.get(i);
                    if (sub.contains(element)) {
                        index = i+1;
                        break;
                    }
                }
                index--; // HACK: fix index.

                // First return the sub element.
                next = sub;

                nextTaken = false;
                return true;
            }
            if (index >= elements.size() - 1) {
                sub = null;
            } else {
                while (index < elements.size() - 1) {
                    T element = elements.get(++index);
                    if (!sub.contains(element)) {
                        next = sub.add(element);
                        nextTaken = false;
                        return true;
                    }
                }
            }
        } while (index < elements.size() - 1 || inner.hasNext());


        return false;
    }

    @Override
    public Set<T> next() {
        if (hasNext()) {
            nextTaken = true;
            Set<T> next = this.next;
            this.next = null;
            return next;
        }
        throw new IllegalStateException("No more elements");
    }

    private static class SetIterator<T> implements Iterator<Set<T>> {
        private final Iterator<T> inner;

        private SetIterator(List<T> elements) {
            inner = elements.iterator();
        }

        @Override
        public boolean hasNext() {
            return inner.hasNext();
        }

        @Override
        public Set<T> next() {
            return Set.of(inner.next());
        }
    }

    public static void main(String[] args) {
        Iterator<Set<Integer>> ite = new SubsetOfAtMostSizeIterator<>(Set.of(1, 2, 3, 4, 5), 2);

        // Check for duplicate elements:
        int count = 0;
        java.util.Set<Set<Integer>> retrieved = new HashSet<>();
        while (ite.hasNext()) {
            count++;
            retrieved.add(ite.next());
        }

        assert count == retrieved.size();


        // Check for known elements in subsets.
        ite = new SubsetOfAtMostSizeIterator<>(Set.of(1, 2, 3), 2);

        java.util.Set<Set<Integer>> expected = new HashSet<>();
        expected.add(Set.of(1));
        expected.add(Set.of(2));
        expected.add(Set.of(3));
        expected.add(Set.of(1, 2));
        expected.add(Set.of(1, 3));
        expected.add(Set.of(2, 3));

        while (ite.hasNext()) {
            assert expected.remove(ite.next());
        }
        assert expected.isEmpty();
    }
}
