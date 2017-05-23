package minfill.iterators;

import minfill.graphs.Graph;
import minfill.sets.Set;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class SomeMinimalSeparatorIterable implements Iterable<Set<Integer>> {
    private final Graph g;

    public SomeMinimalSeparatorIterable(Graph g) {
        this.g = g;
    }

    @NotNull
    @Override
    public Iterator<Set<Integer>> iterator() {
        return new SomeMinimalSeparatorIterator(g);
    }
}
