package minfill.data;

import org.jetbrains.annotations.Contract;

import java.util.*;

public class ImmutableGraph implements Graph {
    private final Set<Integer> vertices;
    private final Map<Integer, Set<Integer>> neighborhoods;

    public ImmutableGraph(Set<Integer> vertices) {
        this.vertices = vertices;
        neighborhoods = new HashMap<>();

        for (Integer vertex : vertices) {
            neighborhoods.put(vertex, Set.empty());
        }
    }

    public ImmutableGraph(Set<Integer> vertices, Set<Edge> edges) {
        this(vertices);

        for (Edge edge : edges) {
            neighborhoods.put(edge.from, neighborhoods.get(edge.from).add(edge.to));
            neighborhoods.put(edge.to, neighborhoods.get(edge.to).add(edge.from));
        }
    }

    private ImmutableGraph(Set<Integer> vertices, Map<Integer, Set<Integer>> neighborhoods) {
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
    public Map<Integer, Set<Integer>> neighborhoods() {
        return Collections.unmodifiableMap(neighborhoods);
    }

    @Override
    @Contract(pure = true)
    public Set<Integer> neighborhood(int n) {
        if (!vertices.contains(n)) throw new IllegalArgumentException("Unknown vertex");
        return neighborhoods.get(n);
    }

    @Override
    @Contract(pure = true)
    public Set<Integer> neighborhood(Set<Integer> vertices) {
        Set<Integer> neighborhood = Set.empty();

        for (Integer vertex : vertices) {
            neighborhood = neighborhood.union(neighborhood(vertex));
        }

        return neighborhood.minus(vertices);
    }

    @Override
    @Contract(pure = true)
    public boolean isAdjacent(int a, int b) {
        if (!vertices.contains(a)) throw new IllegalArgumentException("Unknown vertex");
        if (!vertices.contains(b)) throw new IllegalArgumentException("Unknown vertex");

        return neighborhood(a).contains(b);
    }

    @Override
    @Contract(pure = true)
    public boolean hasPath(int a, int b) {
        if (!vertices.contains(a)) throw new IllegalArgumentException("Unknown vertex");
        if (!vertices.contains(b)) throw new IllegalArgumentException("Unknown vertex");

        Queue<Integer> queue = new ArrayDeque<>();
        java.util.Set<Integer> marked = new HashSet<>();
        queue.add(a);

        while (!queue.isEmpty()) {
            int vertex = queue.poll();
            if (vertex == b) return true;
            if (!marked.contains(vertex)) {
                marked.add(vertex);
                for (Integer neighbor : neighborhood(vertex)) {
                    queue.add(neighbor);
                }
            }
        }

        return false;
    }

    @Override
    @Contract(pure = true)
    public boolean isChordal() {
        throw new UnsupportedOperationException("Not implemented"); // TODO
    }

    @Override
    @Contract(pure = true)
    public boolean isClique() {
        return isClique(vertices);
    }

    @Override
    @Contract(pure = true)
    public boolean isVitalPotentialMaximalClique(Set<Integer> vertices, int k) {
        if (!vertices.isSubsetOf(this.vertices)) throw new IllegalArgumentException("Unknown vertex");
        if (k < 0) return false;
        throw new UnsupportedOperationException("Not implemented"); // TODO
    }

    @Override
    @Contract(pure = true)
    public Set<Set<Integer>> components() {
        java.util.Set<Integer> marked = new HashSet<>();
        java.util.Set<Set<Integer>> components = new HashSet<>();

        for (Integer i : vertices) {
            if (!marked.contains(i)) {
                java.util.Set<Integer> component = new HashSet<>();
                Queue<Integer> queue = new ArrayDeque<>();
                queue.add(i);

                while (!queue.isEmpty()) {
                    int vertex = queue.poll();
                    marked.add(vertex);
                    component.add(vertex);

                    for (Integer neighbor : neighborhood(vertex)) {
                        if (!marked.contains(neighbor)) {
                            queue.add(neighbor);
                        }
                    }
                }
                components.add(new ImmutableSet<>(component));
            }
        }

        return new ImmutableSet<>(components);
    }

    @Override
    @Contract(pure = true)
    public Set<Set<Integer>> fullComponents(Set<Integer> separator) {
        throw new UnsupportedOperationException("Not implemented"); // TODO
    }

    @Override
    @Contract(pure = true)
    public Set<Set<Integer>> minimalSeparators() {
        throw new UnsupportedOperationException("Not implemented"); // TODO
    }

    @Override
    @Contract(pure = true)
    public Set<Set<Integer>> maximalCliquesOfChordalGraph() {
        if (!isChordal())
            throw new UnsupportedOperationException("maximalCliquesOfChordalGraph can only be used on chordal graphs");
        throw new UnsupportedOperationException("Not implemented"); // TODO
    }

    @Override
    @Contract(pure = true)
    public List<Integer> shortestPath(int from, int to) {
        if (!vertices.contains(from)) throw new IllegalArgumentException("Unknown vertex");
        if (!vertices.contains(to)) throw new IllegalArgumentException("Unknown vertex");

        java.util.Set<Integer> marked = new HashSet<>();
        Map<Integer, Integer> edgeFrom = new HashMap<>();
        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(from);

        while (!queue.isEmpty()) {
            int vertex = queue.poll();
            if (vertex == to) break;
            if (!marked.contains(vertex)) {
                marked.add(vertex);
                for (Integer neighbor : neighborhood(vertex)) {
                    edgeFrom.put(neighbor, vertex);
                    queue.add(neighbor);
                }
            }
        }

        List<Integer> path = new ArrayList<>();
        Integer pathVertex = to;

        while (pathVertex != null) {
            path.add(pathVertex);
            pathVertex = edgeFrom.get(pathVertex);
        }

        Collections.reverse(path);

        return path;
    }

    @Override
    @Contract(pure = true)
    public Graph addEdge(Edge e) {
        if (!vertices.contains(e.from)) throw new IllegalArgumentException("Unknown vertex");
        if (!vertices.contains(e.to)) throw new IllegalArgumentException("Unknown vertex");

        if (neighborhood(e.from).contains(e.to)) return this;

        Map<Integer, Set<Integer>> copy = new HashMap<>(neighborhoods);

        copy.put(e.from, copy.get(e.from).add(e.to));
        copy.put(e.to, copy.get(e.to).add(e.from));

        return new ImmutableGraph(vertices, copy);
    }

    @Override
    @Contract(pure = true)
    public Set<Edge> getNonEdges() {
        Set<Edge> nonEdges = Set.empty();

        for (Integer v1 : vertices) {
            for (Integer v2 : vertices) {
                if (v1 < v2 && !neighborhood(v1).contains(v2)) {
                    nonEdges = nonEdges.add(new Edge(v1, v2));
                }
            }
        }

        return nonEdges;
    }

    @Override
    @Contract(pure = true)
    public Graph inducedBy(Set<Integer> vertices) {
        if (!vertices.isSubsetOf(this.vertices)) throw new IllegalArgumentException("Unknown vertex");

        if (vertices.isProperSubsetOf(this.vertices)) {
            Map<Integer, Set<Integer>> copy = new HashMap<>();

            for (Integer vertex : vertices) {
                copy.put(vertex, neighborhoods.get(vertex).intersect(vertices));
            }

            return new ImmutableGraph(vertices, copy);
        }
        // If vertices is a subset of V(this), but not a proper subset, then it must be the entire graph.
        return this;
    }

    @Override
    @Contract(pure = true)
    public Graph minimalTriangulation() {
        throw new UnsupportedOperationException("Not implemented"); // TODO
    }

    @Override
    @Contract(pure = true)
    public Graph cliqueify(Set<Integer> vertices) {
        if (!vertices.isSubsetOf(this.vertices)) throw new IllegalArgumentException("Unknown vertex");

        Map<Integer, Set<Integer>> copy = new HashMap<>(neighborhoods);

        for (Integer v1 : vertices) {
            for (Integer v2 : vertices) {
                if (!Objects.equals(v1, v2)) {
                    copy.put(v1, copy.get(v1).add(v2));
                    copy.put(v2, copy.get(v2).add(v1));
                }
            }
        }

        return new ImmutableGraph(vertices, copy);
    }

    @Override
    @Contract(pure = true)
    public boolean isClique(Set<Integer> vertices) {
        if (!vertices.isSubsetOf(this.vertices)) throw new IllegalArgumentException("Unknown vertex");
        for (Integer v1 : vertices) {
            for (Integer v2 : vertices) {
                if (!Objects.equals(v1, v2) && !neighborhood(v1).contains(v2)) return false;
            }
        }
        return true;
    }
}
