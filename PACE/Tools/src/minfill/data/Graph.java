package minfill.data;

import org.jetbrains.annotations.Contract;

import java.util.*;

public interface Graph {
    @Contract(pure = true)
    Set<Integer> vertices();

    @Contract(pure = true)
    Set<Integer> neighborhood(Integer n);

    @Contract(pure = true)
    default Set<Integer> neighborhood(Set<Integer> vertices) {
        Set<Integer> neighborhood = Set.empty();

        for (Integer vertex : vertices) {
            neighborhood = neighborhood.union(neighborhood(vertex));
        }

        return neighborhood.minus(vertices);
    }

    @Contract(pure = true)
    default boolean isAdjacent(Integer a, Integer b) {
        assert vertices().contains(a);
        assert vertices().contains(b);

        return neighborhood(a).contains(b);
    }

    @Contract(pure = true)
    default boolean hasPath(Integer a, Integer b) {
        assert vertices().contains(a);
        assert vertices().contains(b);

        Queue<Integer> queue = new ArrayDeque<>();
        java.util.Set<Integer> marked = new HashSet<>();
        queue.add(a);

        while (!queue.isEmpty()) {
            Integer vertex = queue.poll();
            if (Objects.equals(vertex, b)) return true;
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
    default List<Integer> maximumCardinalitySearch() {
        List<Integer> order = new ArrayList<>(vertices().size());
        java.util.Set<Integer> numbered = new HashSet<>(vertices().size());
        Map<Integer, Integer> weightMap = new HashMap<>();
        for (Integer vertex : vertices()) {
            weightMap.put(vertex, 0);
            order.add(vertex);
        }
        for (int i = vertices().size()-1;i >= 0 ; i--) {
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

    @Contract(pure = true)
    default Integer unNumberedMaximumWeightVertex(Map<Integer, Integer> weightMap, java.util.Set<Integer> numbered) {
        Integer key = -1, value = Integer.MIN_VALUE;

        for (Map.Entry<Integer, Integer> entry : weightMap.entrySet()) {
            if (!numbered.contains(entry.getKey()) && entry.getValue().compareTo(value) > 0) {
                key = entry.getKey();
                value = entry.getValue();
            }
        }

        return key;
    }

    @Contract(pure = true) // berry page 5
    default Pair<List<Integer>, Set<Edge>> maximumCardinalitySearchM() {
        List<Integer> order = new ArrayList<>(vertices().size());
        java.util.Set<Integer> numbered = new HashSet<>(vertices().size());
        Map<Integer, Integer> weightMap = new HashMap<>();
        Set<Edge> F = Set.empty();
        for (Integer vertex : vertices()) {
            weightMap.put(vertex,0);
            order.add(vertex);
        }
        for (int i = vertices().size()-1; i >= 0 ; i--) {
            Map<Integer, Integer> weightCopy = new HashMap<>(weightMap);

            Integer z = unNumberedMaximumWeightVertex(weightMap, numbered);
            order.set(i, z);
            numbered.add(z);
            for (Integer y : vertices()) {
                if(!numbered.contains(y)){
                    Integer yWeight = weightCopy.get(y);
                    Set<Integer> possibleGraph = Set.of(z,y);

                    for (Integer Xi : vertices()) {
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
        return Tuple.of(order, F);
    }

    @Contract(pure = true)
    default boolean isChordal() {
        List<Integer> order = maximumCardinalitySearch();

        for (int i = 0; i < order.size(); i++) {
            Set<Integer> mAdj = mAdj(order, i);
            if (!isClique(mAdj))
                return false;
        }
        return true;
    }

    @Contract(pure = true)
    default boolean isClique() {
        return isClique(vertices());
    }

    @Contract(pure = true)
    default boolean isPotentialMaximalClique(Set<Integer> k){
        Graph gk = inducedBy(vertices().minus(k));
        java.util.Set<Set<Integer>> s = new HashSet<>();
        for (Set<Integer> component : gk.components()) {
            Set<Integer> sI = neighborhood(component).intersect(k);
            if(!sI.isProperSubsetOf(k)){
                return false;
            }
            s.add(sI);
        }
        Graph cliqueChecker = this;
        for (Set<Integer> sI : s) {
            Set<Edge> fillEdges = cliqueChecker.cliqueify(sI);
            cliqueChecker = cliqueChecker.addEdges(fillEdges);
        }
        return cliqueChecker.inducedBy(k).isClique();
    }

    @Contract(pure = true)
    default boolean isVitalPotentialMaximalClique(Set<Integer> vertices, int k) {
        if (!vertices.isSubsetOf(vertices())) throw new IllegalArgumentException("Unknown vertex");
        return k >= 0 &&
                inducedBy(vertices).getNumberOfNonEdges() <= k &&
                isPotentialMaximalClique(vertices);
    }

    @Contract(pure = true)
    default Set<Set<Integer>> components() {
        java.util.Set<Integer> marked = new HashSet<>();
        java.util.Set<Set<Integer>> components = new HashSet<>();

        for (Integer i : vertices()) {
            if (!marked.contains(i)) {
                java.util.Set<Integer> component = new HashSet<>();
                Queue<Integer> queue = new ArrayDeque<>();
                queue.add(i);
                component.add(i);
                marked.add(i);


                while (!queue.isEmpty() && marked.size() != vertices().size()) {
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

    @Contract(pure = true)
    default Set<Set<Integer>> fullComponents(Set<Integer> separator) {
        java.util.Set<Set<Integer>> fullComponents = new HashSet<>();
        Graph gMinusS = this.inducedBy(this.vertices().minus(separator));
        for (Set<Integer> component : gMinusS.components()) {
            if (neighborhood(component).equals(separator)) {
                fullComponents.add(component);
            }
        }
        return Set.of(fullComponents);
    }

    @Contract(pure = true)
    default List<Integer> shortestPath(Integer from, Integer to) {
        assert vertices().contains(from);
        assert vertices().contains(to);

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
                    if (Objects.equals(neighbor, to)) break;
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

    @Contract(pure = true)
    Graph addEdge(Edge e);

    @Contract(pure = true)
    Graph addEdges(Set<Edge> edges);

    @Contract(pure = true)
    default Set<Edge> getNonEdges() {
        java.util.Set<Edge> nonEdges = new HashSet<>();

        VertexPairIterable<Integer> vertexPairs = new VertexPairIterable<>(vertices());
        for(Pair<Integer, Integer> pair : vertexPairs){
            if (!isAdjacent(pair.a, pair.b)) {
                nonEdges.add(new Edge(pair.a, pair.b));
            }
        }
        return Set.of(nonEdges);
    }

    @Contract(pure = true)
    default Set<Edge> getEdges() {
        java.util.Set<Edge> edges = new HashSet<>();

        for (Integer v1 : vertices()) {
            for (Integer v2 : vertices()) {
                if (v1 < v2 && isAdjacent(v1, v2)) {
                    edges.add(new Edge(v1, v2));
                }
            }
        }

        return Set.of(edges);
    }

    default int getNumberOfNonEdges() {
        int number = 0;
        VertexPairIterable<Integer> vertexPairs = new VertexPairIterable<>(vertices());
        for(Pair<Integer, Integer> pair : vertexPairs){
            if (!isAdjacent(pair.a, pair.b)) {
                number++;
            }
        }
        return number;
    }

    @Contract(pure = true)
    Graph inducedBy(Set<Integer> vertices);

    @Contract(pure = true)
    ChordalGraph minimalTriangulation();

    @Contract(pure = true)
    default Set<Integer> mAdj(List<Integer> peo, int index) {
        Set<Integer> neighborhood = neighborhood(peo.get(index));
        return neighborhood.intersect(
                Set.of(
                        peo.subList(index + 1, peo.size())
                ));
    }

    @Contract(pure = true)
    default Set<Edge> cliqueify(Set<Integer> vertices) {
        assert vertices.isSubsetOf(vertices());

        java.util.Set<Edge> fill = new HashSet<>();

        VertexPairIterable<Integer> vertexPairs = new VertexPairIterable<>(vertices);
        for(Pair<Integer, Integer> pair : vertexPairs){
            if (!isAdjacent(pair.a, pair.b)) {
                fill.add(new Edge(pair.a, pair.b));
            }
        }

        return Set.of(fill);
    }

    @Contract(pure = true)
    default boolean isClique(Set<Integer> vertices) {
        assert vertices.isSubsetOf(vertices());

        VertexPairIterable<Integer> vertexPairs = new VertexPairIterable<>(vertices);
        for(Pair<Integer, Integer> pair : vertexPairs){
            if (!isAdjacent(pair.a, pair.b)) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Contract(pure = true)
    int hashCode();
}
