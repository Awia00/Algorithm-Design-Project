package minfill;

import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.sets.Set;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by aws on 25-04-2017.
 */
public class MinFillEasySolver {
    public static Set<Edge> addEasyEdges(Graph g){
        boolean hasChanged = true;
        Set<Edge> result = Set.empty();
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
}
