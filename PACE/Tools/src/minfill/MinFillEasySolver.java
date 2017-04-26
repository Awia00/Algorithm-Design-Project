package minfill;

import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.sets.Set;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by aws on 25-04-2017.
 */
public class MinFillEasySolver {

    public Set<Edge> findEasyEdges(Graph g){
        return findEasyEdgesStep1(g).union(findEasyEdgesStep2(g));
    }

    @Contract(pure=true)
    private Set<Edge> findEasyEdgesStep1(Graph g){
        boolean hasChanged = true;
        Set<Edge> result = Set.empty();
        // step 1
        while(hasChanged)
        {
            hasChanged = false;
            Optional<List<Integer>> chordlessCycle = g.findChordlessCycle();
            if(chordlessCycle.isPresent()){
                for (Integer u : chordlessCycle.get()) {
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
                            break;
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

        while(hasChanged)
        {
            hasChanged = false;
            Optional<List<Integer>> chordlessCycle = g.findChordlessCycle();
            if(chordlessCycle.isPresent()){
                List<Integer> cycle = chordlessCycle.get();
                for (int i = 0; i < cycle.size(); i++) {
                    Integer u = cycle.get(i);
                    Integer w = cycle.get((i+1)%cycle.size());
                    Integer v = cycle.get((i+2)%cycle.size());

                    g = g.removeEdges(Set.of(new Edge(u,w), new Edge(w,v)));
                    if(!g.hasPath(w,u)){
                        result = result.add(new Edge(u,v));
                    }
                }
            }
        }
        return Set.empty();
    }
}
