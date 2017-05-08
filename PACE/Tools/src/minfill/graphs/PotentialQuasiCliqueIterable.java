package minfill.graphs;

import minfill.sets.Set;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * Created by aws on 19-04-2017.
 */
public class PotentialQuasiCliqueIterable implements Iterable<Set<Integer>> {
    private final Graph g;
    private final int k;

    public PotentialQuasiCliqueIterable(Graph g, int k) {
        this.g = g;
        this.k = k;
    }

    @NotNull
    @Override
    public Iterator<Set<Integer>> iterator() {
        return new PotentialQuasiCliqueIterator(g, k);
    }
}
