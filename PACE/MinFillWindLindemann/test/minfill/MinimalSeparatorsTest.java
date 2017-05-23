package minfill;

import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.graphs.adjacencyset.AdjacencySetGraph;
import minfill.sets.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
/**
 * Created by aws on 21-05-2017.
 */
public class MinimalSeparatorsTest {
    @Test
    public void minimal_A_B_Separators(){
        Set<Integer> vertices = Set.of(1, 2, 3, 4);
        Set<Edge<Integer>> edges = Set.of(new Edge<>(1, 2), new Edge<>(2, 3), new Edge<>(3, 4));
        Graph<Integer> graph = new AdjacencySetGraph<>(vertices, edges);

        Set<Set<Integer>> separators = graph.minimalSeparators(1, 4);
        assertEquals(Set.of(Set.of(2), Set.of(3)), separators);
    }

    @Test
    public void minimalSeparators(){
        Set<Integer> vertices = Set.of(1, 2, 3, 4);
        Set<Edge<Integer>> edges = Set.of(new Edge<>(1, 2), new Edge<>(2, 3), new Edge<>(3, 4));
        Graph<Integer> graph = new AdjacencySetGraph<>(vertices, edges);

        Set<Set<Integer>> separators = graph.minimalSeparators();
        assertEquals(Set.of(Set.of(2), Set.of(3)), separators);
    }
}
