package minfill;

import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.sets.Set;

import java.util.Optional;

/**
 * Created by aws on 08-05-2017.
 */
public class MinFillExhaustive {
    public static Optional<Graph> exhaustiveNonEdgeSearch(Graph g, int k){
        for (Set<Edge> edges : Set.subsetsOfSize(g.getNonEdges(), k)) {
            Graph gWithSubsetEdges = g.addEdges(edges);
            if(gWithSubsetEdges.isChordal()){
                return Optional.of(gWithSubsetEdges);
            }
        }
        return Optional.empty();
    }
}
