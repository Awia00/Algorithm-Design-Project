package minfill.graphs.adjacencymatrix;

import minfill.graphs.ChordalGraph;
import minfill.graphs.Edge;
import minfill.sets.Set;

public class AdjacencyMatrixChordalGraph extends AdjacencyMatrixGraph implements ChordalGraph {
    public AdjacencyMatrixChordalGraph(Set<Integer> vertices, Set<Edge> edges) {
        super(vertices, edges);
    }

    protected AdjacencyMatrixChordalGraph(boolean[][] neighborhoods, FilteredMap<Integer> mapToInternal, Integer[] mapToExternal) {
        super(neighborhoods, mapToInternal, mapToExternal);
    }
}
