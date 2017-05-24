package minfill;

import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.iterators.PairIterable;
import minfill.sets.Set;
import minfill.tuples.Pair;

import java.util.HashSet;
import java.util.Optional;

/**
 * Created by aws on 08-05-2017.
 */
public class MinFillSearchTree {
    private static int reduceCounter, memoizerCounter;
    private static java.util.Set<Graph> nonSolvableGraphs = new HashSet<>();

    public static <T extends Comparable<T>> Optional<Graph<T>> minFillSearchTree(Graph<T> g, int k){
        nonSolvableGraphs = new HashSet<>();
        memoizerCounter = 0;
        reduceCounter = 0;
        Optional<Graph<T>> tGraph = minFillSearchTreeRecoursive(g, k);
        IO.printf("memoizer hits in minFillSearchTree: %d\n", memoizerCounter);
        return tGraph;
    }
    private static <T extends Comparable<T>> Optional<Graph<T>> minFillSearchTreeRecoursive(Graph<T> g, int k){
        if(k==0 ){
            if(g.isChordal()) return Optional.of(g);
            else return Optional.empty();
        }
        if(k>3) {
            if(nonSolvableGraphs.contains(g)){
                memoizerCounter++;
                if(k >= 10) IO.printf("nonSolvableGraphs hit at k: %d\n", k);
                return Optional.empty();
            }
        }

        // the search tree algorithm: find a cycle and breanch on possible chords.
        for (Pair<T, T> pair : new PairIterable<>(Set.of(g.findChordlessCycle().get()))) {
            if(!g.isAdjacent(pair.a, pair.b)){
                Optional<Graph<T>> result = minFillSearchTreeRecoursive(g.addEdge(new Edge<>(pair.a, pair.b)), k-1);
                if(result.isPresent())
                    return result;
            }
        }

        if(k>3)
            nonSolvableGraphs.add(g);
        return Optional.empty();
    }

    private static <T extends Comparable<T>> Optional<Graph<T>> minFillSearchTreeReducer(Graph<T> g, int k) {
        MinFillPolynomialReducer<T> reducer = new MinFillPolynomialReducer<>();
        Set<T> removableVertices = reducer.findRemovableVertices(g);
        Graph<T> gPrime = g.inducedBy(g.getVertices().minus(removableVertices));
        Set<Edge<T>> addEdges = reducer.findSafeEdges(gPrime);
        if (!addEdges.isEmpty()) {
            //IO.printf("reduced instance in search tree by %d\n", addEdges.size());
            if (addEdges.size() > k)
                return Optional.empty();
            return minFillSearchTreeRecoursive(g.addEdges(addEdges), k - addEdges.size());
        }
        Optional<Set<T>> ts = reducer.separatorsThatAreClique(gPrime.addEdges(addEdges));
        if (ts.isPresent()) {
            System.out.println("WHAAAA\nAAAAAA\nAAAAA\nAAAAA\nAAAT");
        }
        return minFillSearchTreeRecoursive(g, k);
    }
}
