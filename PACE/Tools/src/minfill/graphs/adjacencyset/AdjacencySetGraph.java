package minfill.graphs.adjacencyset;

import minfill.graphs.ChordalGraph;
import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.graphs.Neighborhood;
import minfill.sets.Set;
import org.jetbrains.annotations.Contract;

import java.util.HashMap;
import java.util.Map;

public class AdjacencySetGraph implements Graph {
    private final Set<Integer> vertices;
    private final Map<Integer, Set<Integer>> neighborhoods;

    public AdjacencySetGraph(Set<Integer> vertices) {
        this.vertices = vertices;
        neighborhoods = new HashMap<>();

        for (Integer vertex : vertices) {
            neighborhoods.put(vertex, Set.empty());
        }
    }

    public AdjacencySetGraph(Set<Integer> vertices, Set<Edge> edges) {
        this(vertices);

        for (Edge edge : edges) {
            neighborhoods.put(edge.from, neighborhoods.get(edge.from).add(edge.to));
            neighborhoods.put(edge.to, neighborhoods.get(edge.to).add(edge.from));
        }
    }

    protected AdjacencySetGraph(Set<Integer> vertices, Map<Integer, Set<Integer>> neighborhoods) {
        this.vertices = vertices;
        this.neighborhoods = neighborhoods;
    }

    @Override
    @Contract(pure = true)
    public Set<Integer> vertices() {
        return vertices;
    }

    @Override
    @Contract(pure = true)
    public Neighborhood neighborhood(Integer n) {
        assert vertices.contains(n);
        return new AdjacencySetNeighborhood(neighborhoods.get(n));
    }

    @Override
    public Graph removeEdges(Set<Edge> edges) {
        boolean change = false;

        Map<Integer, Set<Integer>> copy = new HashMap<>(neighborhoods);

        for (Edge e : edges) {
            assert vertices.contains(e.from);
            assert vertices.contains(e.to);

            if (isAdjacent(e.from, e.to)) {
                change = true;
                copy.put(e.from, copy.get(e.from).remove(e.to));
                copy.put(e.to, copy.get(e.to).remove(e.from));
            }
        }

        return change ? new AdjacencySetGraph(vertices, copy) : this;
    }

    @Override
    @Contract(pure = true)
    public Graph addEdge(Edge e) {
        assert vertices.contains(e.from);
        assert vertices.contains(e.to);

        if (isAdjacent(e.from, e.to)) return this;

        Map<Integer, Set<Integer>> copy = new HashMap<>(neighborhoods);

        copy.put(e.from, copy.get(e.from).add(e.to));
        copy.put(e.to, copy.get(e.to).add(e.from));

        return new AdjacencySetGraph(vertices, copy);
    }


    @Override
    @Contract(pure = true)
    public Graph addEdges(Set<Edge> edges) {
        boolean change = false;

        Map<Integer, Set<Integer>> copy = new HashMap<>(neighborhoods);

        for (Edge e : edges) {
            assert vertices.contains(e.from);
            assert vertices.contains(e.to);

            if (!isAdjacent(e.from, e.to)) {
                change = true;
                copy.put(e.from, copy.get(e.from).add(e.to));
                copy.put(e.to, copy.get(e.to).add(e.from));
            }
        }

        return change ? new AdjacencySetGraph(vertices, copy) : this;
    }

    @Override
    @Contract(pure = true)
    public Graph inducedBy(Set<Integer> vertices) {
        assert vertices.isSubsetOf(vertices());

        if (vertices.isProperSubsetOf(vertices())) {
            Map<Integer, Set<Integer>> copy = new HashMap<>();

            for (Integer vertex : vertices) {
                copy.put(vertex, neighborhood(vertex).toSet().intersect(vertices));
            }

            return new AdjacencySetGraph(vertices, copy);
        }
        // If vertices is a subset of V(this), but not a proper subset, then it must be the entire graph.
        return this;
    }

    @Override
    @Contract(pure = true)
    public ChordalGraph minimalTriangulation() {
        if (isChordal()) return new AdjacencySetChordalGraph(vertices, neighborhoods);
        Map<Integer, Set<Integer>> copy = new HashMap<>(neighborhoods);

        for (Edge edge : maximumCardinalitySearchM().b) {
            copy.put(edge.from, copy.get(edge.from).add(edge.to));
            copy.put(edge.to, copy.get(edge.to).add(edge.from));
        }

        return new AdjacencySetChordalGraph(vertices, copy);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdjacencySetGraph that = (AdjacencySetGraph) o;

        return neighborhoods.equals(that.neighborhoods);
    }

    @Override
    public int hashCode() {
        return neighborhoods.hashCode();
    }
}
