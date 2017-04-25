package minfill.graphs.adjacencyset;

import minfill.graphs.ChordalGraph;
import minfill.sets.Set;

import java.util.Map;

public class AdjacencySetChordalGraph extends AdjacencySetGraph implements ChordalGraph {
    protected AdjacencySetChordalGraph(Set<Integer> vertices, Map<Integer, Set<Integer>> neighborhoods) {
        super(vertices, neighborhoods);
        assert isChordal();
    }
}
