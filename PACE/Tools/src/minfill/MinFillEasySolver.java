package minfill;

import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.sets.Set;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aws on 25-04-2017.
 */
public class MinFillEasySolver {

    public Set<Integer> findRemovableVertices(Graph g){
        Set<Integer> result = Set.empty();
        for (Integer integer : g.vertices()) {
            if(g.isClique(g.neighborhood(integer).toSet()))
            {
                result = result.add(integer);
            }
        }
        return result;
    }

    public Set<Edge> findEasyEdges(Graph g){
        Set<Edge> step1 = findEasyEdgesStep1(g);
        Set<Edge> step2 = findEasyEdgesStep2(g.addEdges(step1));
        return step1.union(step2);
    }

    @Contract(pure=true)
    private Set<Edge> findEasyEdgesStep1(Graph g){
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
                            g = g.inducedBy(g.vertices().remove(u));
                            hasChanged=true;
                            continue outer;
                        }
                    }
                }
            }
        }
        return result;
    }

    private Set<Edge> findEasyEdgesStep2(Graph g) {
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
}
