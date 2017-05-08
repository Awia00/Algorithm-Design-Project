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
    public static Optional<Graph> minFillSearchTree(Graph g, int k){
        if(k==0 && g.isChordal()){
            IO.println("button reached:");
            return Optional.of(g);
        }
        if(k==0){
            return Optional.empty();
        }

        for (Pair<Integer, Integer> pair : new PairIterable<>(Set.of(g.findChordlessCycle().get()))) {
            if(!g.isAdjacent(pair.a, pair.b)){
                Optional<Graph> result = minFillSearchTree(g.addEdge(new Edge(pair.a, pair.b)), k-1);
                if(result.isPresent())
                    return result;
            }
        }
        return Optional.empty();
    }
}
