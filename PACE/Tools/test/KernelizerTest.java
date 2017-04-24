import minfill.data.Edge;
import minfill.data.Graph;
import minfill.data.AdjacencySetGraph;
import minfill.data.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KernelizerTest {
    @Test
    void kernelize() {
        Set<Integer> vertices = Set.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        Set<Edge> edges = Set.of(
                new Edge(0, 1),
                new Edge(1, 2),
                new Edge(2, 3),
                new Edge(3, 4),
                new Edge(0, 4),
                new Edge(5, 6),
                new Edge(6, 7),
                new Edge(7, 8),
                new Edge(5, 8),
                new Edge(8, 9));

        Graph g = new AdjacencySetGraph(vertices, edges);

        Graph kernelized = Kernelizer.kernelize(g);

        int numVertices = kernelized.vertices().size();
        int numEdges = kernelized.getEdges().size();

        for (Set<Integer> component : g.components()) {
            Graph kernelizedComponent = Kernelizer.kernelize(g.inducedBy(component));

            numVertices -= kernelizedComponent.vertices().size();
            numEdges -= kernelizedComponent.getEdges().size();
        }

        assertEquals(0, numVertices);
        assertEquals(0, numEdges);
    }

}