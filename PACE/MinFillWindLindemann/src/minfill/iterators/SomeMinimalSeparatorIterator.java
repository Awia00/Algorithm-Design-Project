package minfill.iterators;

import minfill.graphs.Graph;
import minfill.sets.Set;

import java.util.Iterator;
import java.util.Optional;

public class SomeMinimalSeparatorIterator implements Iterator<Set<Integer>> {
    private Iterator<Integer> aVertices, bVertices;
    private Integer a;
    private Set<Integer> next;
    private final Graph g;

    public SomeMinimalSeparatorIterator(Graph g) {
        this.g = g;
        aVertices = g.getVertices().iterator();
    }

    @Override
    public boolean hasNext() {
        if (next != null) return true;

        while (aVertices.hasNext() || (bVertices != null && bVertices.hasNext())) {
            if (bVertices == null || !bVertices.hasNext()) {
                a = aVertices.next();
                bVertices = g.getVertices().remove(a).iterator();
            }

            while (bVertices.hasNext()) {
                Integer b = bVertices.next();
                if(!g.isAdjacent(a,b)){
                    Set<Integer> nA = g.neighborhood(a).toSet();
                    Optional<Set<Integer>> cB = g.inducedBy(g.getVertices().minus(nA)).componentWithB(b);
                    assert cB.isPresent();
                    Set<Integer> s = nA.minus(g.isolatedSet(cB.get(), nA));
                    if (!s.isEmpty()) {
                        next = s;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Set<Integer> next() {
        if (next == null) throw new IllegalStateException();
        Set<Integer> result = next;
        next = null;
        return result;
    }
}
