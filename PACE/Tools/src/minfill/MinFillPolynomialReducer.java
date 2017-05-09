package minfill;

import minfill.graphs.ChordalGraph;
import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.sets.Set;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by aws on 25-04-2017.
 */
public class MinFillPolynomialReducer {

    public Set<Integer> findRemovableVertices(Graph g){
        java.util.Set<Integer> result = new HashSet<>();

        for (Integer integer : g.getVertices()) {
            Set<Integer> neighborhood = g.neighborhood(integer).toSet();
            if (g.neighborhood(integer).toSet().size() == g.getVertices().size() - 1) { // check if universal
                result.add(integer);
            } else if (g.isClique(neighborhood)) // check is simplicial
            {
                // Simplicial getVertices
                result.add(integer);

                // Cliques
                for (Set<Integer> component : g.inducedBy(g.getVertices().minus(neighborhood)).components()) {
                    if (component.size() > 2) {
                        Set<Integer> fringe = g.neighborhood(component);
                        neighborhood = neighborhood.minus(fringe).minus(component);
                    }
                }
                for (Integer vertex : neighborhood) {
                    result.add(vertex);
                }
            }
        }
        //for (Set<Integer> subset : new SubsetOfAtMostSizeIterable<>(g.getVertices(), 4)) {
        //    if(g.isClique(subset)){
        //        for (Set<Integer> component : g.inducedBy(g.getVertices().minus(subset)).components()) {
        //            if (component.size() > 2) {
        //                Set<Integer> fringe = g.neighborhood(component);
        //                subset = subset.minus(fringe).minus(component);
        //            }
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
        Set<Edge> step3 = minimalSeparatorsAlmostCliquesReducer(g.addEdges(step2));
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

    // Does not work ATM
    private Set<Edge> minimalSeparatorsAlmostCliquesReducer(Graph g) {
        // step 1
        boolean hasChanged = true;
        Set<Edge> result = Set.empty();

        while(hasChanged)
        {
            hasChanged = false;
            ChordalGraph h = g.minimalTriangulation(); // this cannot be done to get the minimalSeparators
            for (Set<Integer> separator : h.minimalSeparators()) {
                Set<Edge> nonEdges = g.inducedBy(separator).getNonEdges();
                if(nonEdges.size() == 1){
                    result = result.union(nonEdges);
                    g = g.addEdges(nonEdges);
                    hasChanged = true;
                    break;
                }
            }
        }
        return Set.empty();
    }
}
