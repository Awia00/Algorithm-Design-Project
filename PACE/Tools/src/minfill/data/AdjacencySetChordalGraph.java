package minfill.data;

import java.util.Map;

public class AdjacencySetChordalGraph extends AdjacencySetGraph implements ChordalGraph {
    protected AdjacencySetChordalGraph(Set<Integer> vertices, Map<Integer, Set<Integer>> neighborhoods) {
        super(vertices, neighborhoods);
        assert isChordal();
    }

    @Override
    public ChordalGraph minimalTriangulation() {
        return this;
    } // Since we are already a chordal graph.
}
