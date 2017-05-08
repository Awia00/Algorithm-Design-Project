package minfill.graphs.adjacencymatrix;

import minfill.graphs.ChordalGraph;
import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.graphs.Neighborhood;
import minfill.sets.Set;

import java.util.*;

public class AdjacencyMatrixGraph implements Graph {
    private boolean[][] neighborhoods;
    private FilteredMap<Integer> mapToInternal;
    private Integer[] mapToExternal;

    public AdjacencyMatrixGraph(Set<Integer> vertices, Set<Edge> edges) {
        mapToExternal = new Integer[vertices.size()];
        neighborhoods = new boolean[vertices.size()][vertices.size()];

        Map<Integer, Integer> toInternal = new HashMap<>(vertices.size());
        java.util.Set<Integer> allowedKeys = new HashSet<>(),
            allowedValues = new HashSet<>();

        int index = 0;
        for (Integer vertex : vertices) {
            mapToExternal[index] = vertex;
            toInternal.put(vertex, index);
            allowedKeys.add(vertex);
            allowedValues.add(index);
            ++index;
        }

        mapToInternal = new FilteredMap<>(Set.of(allowedKeys), Set.of(allowedValues), toInternal);

        for (Edge edge : edges) {
            int from = mapToInternal.get(edge.from),
                to = mapToInternal.get(edge.to);
            neighborhoods[from][to] = neighborhoods[to][from] = true;
        }
    }

    protected AdjacencyMatrixGraph(boolean[][] neighborhoods, FilteredMap<Integer> mapToInternal, Integer[] mapToExternal) {
        this.neighborhoods = neighborhoods;
        this.mapToInternal = mapToInternal;
        this.mapToExternal = mapToExternal;
    }

    @Override
    public Set<Integer> getVertices() {
        return mapToInternal.keySet();
    }

    @Override
    public Neighborhood neighborhood(Integer n) {
        return new AdjacencyMatrixNeighborhood(mapToExternal, mapToInternal, neighborhoods[mapToInternal.get(n)]);
    }

    @Override
    public Graph removeEdges(Set<Edge> edges) {
        boolean[][] copy = deepCopy();

        boolean change = false;

        for (Edge e : edges) {
            int from = mapToInternal.get(e.from),
                    to = mapToInternal.get(e.to);

            if (neighborhoods[from][to]) {
                change = true;
                copy[from][to] = copy[to][from] = false;
            }
        }

        if (change) return new AdjacencyMatrixGraph(copy, mapToInternal, mapToExternal);
        return this;
    }


    public static int adjacentCount = 0;
    @Override
    public boolean isAdjacent(Integer a, Integer b) {
        adjacentCount++;
        int from = mapToInternal.get(a);
        int to = mapToInternal.get(b);

        return neighborhoods[from][to];
    }

    @Override
    public Graph addEdge(Edge e) {
        int from = mapToInternal.get(e.from),
            to = mapToInternal.get(e.to);

        if (neighborhoods[from][to]) return this;

        boolean[][] copy = deepCopy();
        copy[from][to] = copy[to][from] = true;

        return new AdjacencyMatrixGraph(copy, mapToInternal, mapToExternal);
    }

    @Override
    public Graph addEdges(Set<Edge> edges) {
        boolean[][] copy = deepCopy();

        boolean change = false;

        for (Edge e : edges) {
            int from = mapToInternal.get(e.from),
                to = mapToInternal.get(e.to);

            if (!neighborhoods[from][to]) {
                change = true;
                copy[from][to] = copy[to][from] = true;
            }
        }

        if (change) return new AdjacencyMatrixGraph(copy, mapToInternal, mapToExternal);
        return this;
    }

    @Override
    public Graph inducedBy(Set<Integer> vertices) {
        assert vertices.isSubsetOf(getVertices());

        java.util.Set<Integer> allowedValues = new HashSet<>();

        for (Integer vertex : vertices) {
            allowedValues.add(mapToInternal.get(vertex));
        }

        return new AdjacencyMatrixGraph(neighborhoods, new FilteredMap<>(vertices.intersect(mapToInternal.allowedKeys), Set.of(allowedValues).intersect(mapToInternal.allowedValues), mapToInternal.inner), mapToExternal);
    }

    @Override
    public ChordalGraph minimalTriangulation() {
        if (isChordal()) return new AdjacencyMatrixChordalGraph(neighborhoods, mapToInternal, mapToExternal);
        boolean[][] copy = deepCopy();

        boolean change = false;
        for (Edge e : maximumCardinalitySearchM().b) {
            int from = mapToInternal.get(e.from),
                    to = mapToInternal.get(e.to);

            if (!neighborhoods[from][to]) {
                change = true;
                copy[from][to] = copy[to][from] = true;
            }
        }

        if (change) return new AdjacencyMatrixChordalGraph(copy, mapToInternal, mapToExternal);

        throw new IllegalStateException("This should not be possible :-(");
    }

    private boolean[][] deepCopy() {
        boolean[][] copy = new boolean[neighborhoods.length][];

        for (int i = 0; i < copy.length; i++) {
            copy[i] = Arrays.copyOf(neighborhoods[i], neighborhoods[i].length);
        }

        return copy;
    }
}

