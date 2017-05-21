package minfill.graphs.adjacencymatrix;

import minfill.graphs.Edge;
import minfill.sets.ImmutableSet;
import minfill.sets.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
/**
 * Created by aws on 21-05-2017.
 */
public class MinimalSeparatorsTest {
    @Test
    public void minimal_A_B_Separators(){
        Set<Integer> vertices = Set.of(1, 2, 3, 4);
        Set<Edge> edges = Set.of(new Edge(1, 2), new Edge(2, 3), new Edge(3, 4));
        AdjacencyMatrixGraph graph = new AdjacencyMatrixGraph(vertices, edges);

        Set<Set<Integer>> separators = graph.minimalSeparators(1, 4);
        assertEquals(Set.of(Set.of(2), Set.of(3)), separators);
    }

    @Test
    public void minimalSeparators(){
        Set<Integer> vertices = Set.of(1, 2, 3, 4);
        Set<Edge> edges = Set.of(new Edge(1, 2), new Edge(2, 3), new Edge(3, 4));
        AdjacencyMatrixGraph graph = new AdjacencyMatrixGraph(vertices, edges);

        Set<Set<Integer>> separators = graph.minimalSeparators();
        assertEquals(Set.of(Set.of(2), Set.of(3)), separators);
    }
}
