package minfill;

import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.iterators.PairIterable;
import minfill.sets.Set;
import minfill.tuples.Pair;

import java.util.Optional;

/**
 * Created by aws on 08-05-2017.
 */
public class MinFillSearchTree {
    public static <T extends Comparable<T>> Optional<Graph<T>> minFillSearchTree(Graph<T> g, int k){
        if(k==0 ){
            if(g.isChordal()) return Optional.of(g);
            else return Optional.empty();
        }

        for (Pair<T, T> pair : new PairIterable<>(Set.of(g.findChordlessCycle().get()))) {
            if(!g.isAdjacent(pair.a, pair.b)){
                Optional<Graph<T>> result = minFillSearchTree(g.addEdge(new Edge<>(pair.a, pair.b)), k-1);
                if(result.isPresent())
                    return result;
            }
        }
        return Optional.empty();
    }
}
