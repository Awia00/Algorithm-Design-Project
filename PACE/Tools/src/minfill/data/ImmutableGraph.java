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

    @Contract(pure = true)
    public Integer unNumberedMaximumWeightVertex(Map<Integer, Integer> weightMap, java.util.Set<Integer> numbered){
        int key = -1, value = Integer.MIN_VALUE;

        for (Map.Entry<Integer, Integer> entry : weightMap.entrySet()) {
            if (!numbered.contains(entry.getKey()) && entry.getValue() > value) {
                key = entry.getKey();
                value = entry.getValue();
            }
        }

        return key;
    }

    @Override
    @Contract(pure = true)
    public List<Integer> maximumCardinalitySearch() { // todo handle components
        List<Integer> order = new ArrayList<>(vertices.size());
        java.util.Set<Integer> numbered = new HashSet<>(vertices.size());
        Map<Integer, Integer> weightMap = new HashMap<>();
        for (Integer vertex : vertices) {
            weightMap.put(vertex, 0);
            order.add(vertex);
        }
        for (int i = vertices.size()-1;i >= 0 ; i--) {
            Integer z = unNumberedMaximumWeightVertex(weightMap, numbered);
            order.set(i, z);
            numbered.add(z);
            for (Integer neighbour : neighborhood(z)) {
                if (!numbered.contains(neighbour)) {
                    weightMap.put(neighbour, weightMap.get(neighbour) + 1);
                }
            }
        }
        return order;
    }
    @Override
    @Contract(pure = true) // berry page 5
    public Pair<List<Integer>, Set<Edge>> maximumCardinalitySearchM() { // todo handle components
        List<Integer> order = new ArrayList<>(vertices.size());
        java.util.Set<Integer> numbered = new HashSet<>(vertices.size());
        Map<Integer, Integer> weightMap = new HashMap<>();
        Set<Edge> F = Set.empty();
        for (Integer vertex : vertices) {
            weightMap.put(vertex,0);
            order.add(vertex);
        }
        for (int i = vertices.size()-1; i >= 0 ; i--) {
            Map<Integer, Integer> weightCopy = new HashMap<>(weightMap);

            Integer z = unNumberedMaximumWeightVertex(weightMap, numbered);
            order.set(i, z);
            numbered.add(z);
            for (Integer y : vertices) {
                if(!numbered.contains(y)){
                    Integer yWeight = weightCopy.get(y);
                    Set<Integer> possibleGraph = Set.of(z,y);

                    for (Integer Xi : vertices) {
                        if(!numbered.contains(Xi) && weightCopy.get(Xi) < yWeight){ // w{z-}(xi) < w_{z-}(y) so maybe wrong maybe we need a path of increasing weight or something
                            possibleGraph = possibleGraph.add(Xi);
                        }
                    }

                    if(inducedBy(possibleGraph).hasPath(y,z)){
                        weightMap.put(y, weightMap.get(y)+1);
                        F = F.add(new Edge(z,y));
                    }
                }
            }
        }
        return new Pair<>(order, F);
    }
    @Override
    @Contract(pure = true)
    public boolean isChordal() { // todo handle components
        for (Set<Integer> component : components()) {
            List<Integer> order = this.inducedBy(component).maximumCardinalitySearch();

            for (int i = 0; i < order.size(); i++) {
                Set<Integer> madj = mAdj(order, i);
                if (!isClique(madj))
                    return false;
            }
        }
        return true; // might not be how to check that it has an ordering.
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
        return inducedBy(vertices).getNonEdges().size() <= k && isPotentialMaximalClique(vertices);
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
                component.add(i);
                marked.add(i);


                while (!queue.isEmpty() && marked.size() != vertices.size()) {
                    int vertex = queue.poll();

                    for (Integer neighbor : neighborhood(vertex)) {
                        if (!marked.contains(neighbor)) {
                            marked.add(neighbor);
                            component.add(neighbor);
                            queue.add(neighbor);
                        }
                    }
                }
                components.add(Set.of(component));
            }
        }

        return Set.of(components);
    }

    @Override
    @Contract(pure = true)
    public Set<Set<Integer>> fullComponents(Set<Integer> separator) {
        Set<Set<Integer>> fullComponents = Set.empty();
        Graph gMinusS = this.inducedBy(this.vertices().minus(separator));
        for (Set<Integer> component : gMinusS.components()) {
            if (neighborhood(component).equals(separator)) {
                fullComponents = fullComponents.add(component);
            }
        }
        return fullComponents;
    }

    @Override
    @Contract(pure = true) // Kumar, Madhavan page 10(164)
    public Set<Set<Integer>> minimalSeparatorsOfChordalGraph() {
        if (!isChordal())
            throw new UnsupportedOperationException("minimalSeparatorsOfChordalGraph can only be used on chordal graphs");
        List<Integer> peo = maximumCardinalitySearch();
        Set<Set<Integer>> separators = Set.empty();
        for (int i = 0; i < peo.size()-1; i++) {
            Integer v1 = peo.get(i);
            Integer v2 = peo.get(i+1);
            if(neighborhood(v1).size() <= neighborhood(v2).size()){
                separators = separators.add(neighborhood(v1));
            }
        }
        return separators;
        // new UnsupportedOperationException("Not implemented"); // TODO
    }

    @Override
    @Contract(pure = true) // blair page 20
    public Set<Set<Integer>> maximalCliquesOfChordalGraph() { // todo handle components
        if (!isChordal())
            throw new UnsupportedOperationException("maximalCliquesOfChordalGraph can only be used on chordal graphs");
        List<Integer> peo = maximumCardinalitySearch();
        Set<Set<Integer>> cliques = Set.empty();
        for (int i = 0; i < peo.size()-1; i++) {
            Integer v1 = peo.get(i);
            Integer v2 = peo.get(i+1);
            if(i == 0) cliques = cliques.add(neighborhood(v1).add(v1));
            if(mAdj(peo, i).size() <= mAdj(peo, i+1).size()) { // Li = vertices with labels greater than i but we already know how many we have left since we go in order
                cliques = cliques.add(neighborhood(v2).add(v2));
            }
        }
        return cliques;
    }

    private Set<Integer> mAdj(List<Integer> peo, int index) {
        Set<Integer> neighborhood = neighborhood(peo.get(index));
        return neighborhood.intersect(
                Set.of(
                        peo.subList(index + 1, peo.size())
                ));
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
        marked.add(from);

        while (!queue.isEmpty()) {
            int vertex = queue.poll();
            for (Integer neighbor : neighborhood(vertex)) {
                if (!marked.contains(neighbor)) {
                    marked.add(neighbor);
                    edgeFrom.put(neighbor, vertex);
                    if (neighbor == to) break;
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
    public Graph addEdges(Set<Edge> edges) {
        return new ImmutableGraph(vertices, getEdges().union(edges));
    }

    @Override
    public Graph removeEdge(Edge e) {
        if (!vertices.contains(e.from)) throw new IllegalArgumentException("Unknown vertex");
        if (!vertices.contains(e.to)) throw new IllegalArgumentException("Unknown vertex");

        if (!neighborhood(e.from).contains(e.to)) return this;

        Map<Integer, Set<Integer>> copy = new HashMap<>(neighborhoods);

        copy.put(e.from, copy.get(e.from).remove(e.to));
        copy.put(e.to, copy.get(e.to).remove(e.from));

        return new ImmutableGraph(vertices, copy);
    }

    @Contract(pure = true)
    public Set<Edge> getEdges(){
        Set<Edge> edges = Set.empty();

        for (Integer v1 : vertices) {
            for (Integer v2 : vertices) {
                if (v1 < v2 && neighborhood(v1).contains(v2)) {
                    edges = edges.add(new Edge(v1, v2));
                }
            }
        }

        return edges;
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
        if (!vertices.isSubsetOf(this.vertices))
            throw new IllegalArgumentException("Unknown vertex");

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
        return addEdges(maximumCardinalitySearchM().o2);
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

        return new ImmutableGraph(this.vertices, copy);
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
