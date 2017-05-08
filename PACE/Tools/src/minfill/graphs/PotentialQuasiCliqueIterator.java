package minfill.graphs;

import minfill.graphs.ChordalGraph;
import minfill.graphs.Graph;
import minfill.iterators.FilterIterator;
import minfill.sets.Set;

import java.util.*;

/**
 * Created by aws on 19-04-2017.
 */
public class PotentialQuasiCliqueIterator implements Iterator<Set<Integer>> {
    private final Graph g;
    private final Iterator<Set<Integer>> vertexSubsets;
    private Set<Integer> z;
    private FilterIterator<Set<Integer>> minimalSeparators;
    private Iterator<Set<Integer>> maximalCliques;
    private Iterator<Integer> zIterator;
    private Graph gMinusKUnionZ;
    private int stage;

    public PotentialQuasiCliqueIterator(Graph g, int k) {
        this.g = g;
        vertexSubsets = Set.subsetsOfSizeAtMost(g.vertices(), (int)(5*Math.sqrt(k))).iterator();
    }

    @Override
    public boolean hasNext() {
        if(stage == 0){
            return vertexSubsets.hasNext();
        }
        else if(stage == 1){
            boolean value = minimalSeparators.hasNext();
            if(!value) {
                stage = 2;
                return hasNext();
            }
            return true;
        }
        else if(stage == 2){
            boolean value = maximalCliques.hasNext();
            if(!value){
                stage = 0;
                return hasNext();
            }
            return true;
        }
        else if(stage == 3){
            boolean value = zIterator.hasNext();
            if(!value){
                stage = 2;
                return hasNext();
            }
            return true;
        }
        throw new RuntimeException("hasNext got away");
    }

    @Override
    public Set<Integer> next() {
        if(stage == 0){
            z = vertexSubsets.next();
            Set<Integer> gMinusZ = g.vertices().minus(z);
            ChordalGraph h = g.inducedBy(gMinusZ).minimalTriangulation();
            minimalSeparators = new FilterIterator<>(h.minimalSeparators(), g::isClique);
            maximalCliques = h.maximalCliques().iterator();

            if(minimalSeparators.hasNext())
                stage = 1;
            else
                stage = 2;
        }
        if(stage == 1){
            Set<Integer> s = minimalSeparators.next();
            return s.union(z);
        }
        if(stage == 2){
            Set<Integer> maximalClique = maximalCliques.next();
            gMinusKUnionZ = g.inducedBy(g.vertices().minus(maximalClique.union(z)));
            zIterator = z.iterator();
            stage = 3;
            if(g.isClique(maximalClique)){
                return maximalClique.union(z);
            }
        }
        if(stage == 3){
            Integer y = zIterator.next();
            Set<Integer> Y = Set.of(y);
            for (Set<Integer> bi : gMinusKUnionZ.components()) {
                if (g.neighborhood(bi).contains(y)) {
                    Y = Y.union(bi);
                }
            }
            return g.neighborhood(Y).add(y);
        }
        throw new RuntimeException("next got away");
    }
}
