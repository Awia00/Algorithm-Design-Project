package minfill;

import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.iterators.FilterIterable;
import minfill.iterators.SomeMinimalSeparatorIterable;
import minfill.sets.Set;
import org.jetbrains.annotations.Contract;

import java.util.*;

/**
 * Created by aws on 25-04-2017.
 */
public class MinFillPolynomialReducer {

    public Set<Integer> findRemovableVertices(Graph g){
        java.util.Set<Integer> result = new HashSet<>();
        Graph gPrime = g;

        boolean hasChanged = true;
        while(hasChanged){
            hasChanged = false;
            for (Integer integer : gPrime.getVertices()) {
                Set<Integer> neighborhood = gPrime.neighborhood(integer).toSet();
                if (gPrime.neighborhood(integer).toSet().size() == gPrime.getVertices().size() - 1) { // check if universal
                    result.add(integer);
                    gPrime = gPrime.inducedBy(gPrime.getVertices().remove(integer));

                    hasChanged = true;
                    break;
                } else if (gPrime.isClique(neighborhood)) // check is simplicial
                {
                    // Simplicial getVertices
                    result.add(integer);
                    gPrime = gPrime.inducedBy(gPrime.getVertices().remove(integer));

                    // Cliques
                    for (Set<Integer> component : gPrime.inducedBy(gPrime.getVertices().minus(neighborhood)).components()) {
                        Set<Integer> fringe = gPrime.neighborhood(component);
                        neighborhood = neighborhood.minus(fringe).minus(component);
                    }
                    for (Integer vertex : neighborhood) {
                        result.add(vertex);
                        gPrime = gPrime.inducedBy(gPrime.getVertices().remove(integer));
                    }
                    hasChanged = true;
                    break;
                }
            }
        }
        //for (Set<Integer> subset : new SubsetOfAtMostSizeIterable<>(g.getVertices(), 4)) {
        //    if(g.isClique(subset)){
        //        for (Set<Integer> component : g.inducedBy(g.getVertices().minus(subset)).components()) {
        //            Set<Integer> fringe = g.neighborhood(component);
        //            subset = subset.minus(fringe).minus(component);
        //        }
        //        for (Integer vertex : subset) {
        //            result.add(vertex);
        //        }
        //    }
        //}
        return Set.of(result);
    }

    public Set<Edge> findSafeEdges(Graph g){
        Set<Edge> step1 = independentSimpleCycleReduction(g);
        Set<Edge> step2 = nonIndependentSimpleCycleReducer(g.addEdges(step1));
        Set<Edge> step3 = firstLevelMinimalSeparatorsAlmostCliquesReducer(g.addEdges(step2.union(step1)));

        IO.println("MinSep: " + step3.size());
        return step1.union(step2).union(step3);
    }

    @Contract(pure=true)
    private Set<Edge> independentSimpleCycleReduction(Graph g){
        boolean hasChanged = true;
        Set<Edge> result = Set.empty();
        // step 1
        outer:
        while(hasChanged)
        {
            hasChanged = false;
            for (List<Integer> cycle : g.findChordlessCycles()) {
                for (Integer u : cycle) {
                    Set<Integer> neighbourhood = g.neighborhood(u).toSet();
                    if(neighbourhood.size() == 2){
                        ArrayList<Integer> neighbourhoodList = new ArrayList<>();
                        for (Integer vertex : neighbourhood) {
                            neighbourhoodList.add(vertex);
                        }

                        if(g.neighborhood(neighbourhoodList.get(0)).toSet().size() == 2 || g.neighborhood(neighbourhoodList.get(1)).toSet().size() == 2){
                            Edge edge = new Edge(neighbourhoodList.get(0), neighbourhoodList.get(1));
                            result = result.add(edge);
                            g = g.addEdge(edge);
                            g = g.inducedBy(g.getVertices().remove(u));
                            hasChanged=true;
                            continue outer;
                        }
                    }
                }
            }
        }
        return result;
    }

    private Set<Edge> nonIndependentSimpleCycleReducer(Graph g) {
        // step 1
        boolean hasChanged = true;
        Set<Edge> result = Set.empty();

        outer:
        while(hasChanged)
        {
            hasChanged = false;
            for (List<Integer> cycle : g.findChordlessCycles()) {
                for (int i = 0; i < cycle.size(); i++) {
                    Integer u = cycle.get(i);
                    Integer w = cycle.get((i+1)%cycle.size());
                    Integer v = cycle.get((i+2)%cycle.size());

                    Graph gPrime = g.removeEdges(Set.of(new Edge(u,w), new Edge(w,v)));
                    if(!gPrime.hasPath(w,u)){
                        result = result.add(new Edge(u,v));
                        g = g.addEdge(new Edge(u,v));
                        hasChanged = true;
                        continue outer;
                    }
                }
            }
        }
        return result;
    }

    private Set<Edge> firstLevelMinimalSeparatorsAlmostCliquesReducer(Graph g) {
        Set<Edge> result = Set.empty();

        boolean hasChanged;
        outer:
        do {
            hasChanged = false;
            for (Set<Integer> separator : new SomeMinimalSeparatorIterable(g)) {
                Set<Edge> nonEdges = g.inducedBy(separator).getNonEdges();
                if (nonEdges.size() == 1) {
                    g = g.addEdges(nonEdges);
                    result = result.union(nonEdges);
                    hasChanged = true;
                    continue outer; // To start from the new graph.
                }
            }
        } while (hasChanged);

        return result;
    }

    public Optional<Set<Integer>> separatorsThatAreClique(Graph g) {
        for (Set<Integer> separator : new SomeMinimalSeparatorIterable(g)) {
            if (g.isClique(separator)) {
                return Optional.of(separator);
            }
        }

        return Optional.empty();
    }
}

