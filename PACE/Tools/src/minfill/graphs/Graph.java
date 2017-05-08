package minfill.graphs;

import minfill.iterators.PairIterable;
import minfill.tuples.Pair;
import minfill.tuples.Tuple;
import org.jetbrains.annotations.Contract;

import java.util.*;

public interface Graph {
    @Contract(pure = true)
    minfill.sets.Set<Integer> getVertices();

    @Contract(pure = true)
    Neighborhood neighborhood(Integer n);

    @Contract(pure = true)
    Graph removeEdges(minfill.sets.Set<Edge> edges);

    @Contract(pure = true)
    default minfill.sets.Set<Integer> neighborhood(minfill.sets.Set<Integer> vertices) {
        minfill.sets.Set<Integer> neighborhood = minfill.sets.Set.empty();

        for (Integer vertex : vertices) {
            neighborhood = neighborhood.union(neighborhood(vertex).toSet());
        }

        return neighborhood.minus(vertices);
    }

    @Contract(pure = true)
    default boolean isAdjacent(Integer a, Integer b) {
        assert getVertices().contains(a);
        assert getVertices().contains(b);

        return neighborhood(a).contains(b);
    }

    @Contract(pure = true)
    default boolean hasPath(Integer a, Integer b) {
        assert getVertices().contains(a);
        assert getVertices().contains(b);

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
        List<Integer> order = new ArrayList<>(getVertices().size());
        java.util.Set<Integer> numbered = new HashSet<>(getVertices().size());
        Map<Integer, Integer> weightMap = new HashMap<>();
        for (Integer vertex : getVertices()) {
            weightMap.put(vertex, 0);
            order.add(vertex);
        }
        for (int i = getVertices().size()-1; i >= 0 ; i--) {
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
    default Pair<List<Integer>, minfill.sets.Set<Edge>> maximumCardinalitySearchM() {
        List<Integer> order = new ArrayList<>(getVertices().size());
        java.util.Set<Integer> numbered = new HashSet<>(getVertices().size());
        Map<Integer, Integer> weightMap = new HashMap<>();
        minfill.sets.Set<Edge> F = minfill.sets.Set.empty();
        for (Integer vertex : getVertices()) {
            weightMap.put(vertex,0);
            order.add(vertex);
        }
        for (int i = getVertices().size()-1; i >= 0 ; i--) {
            Map<Integer, Integer> weightCopy = new HashMap<>(weightMap);

            Integer z = unNumberedMaximumWeightVertex(weightMap, numbered);
            order.set(i, z);
            numbered.add(z);
            for (Integer y : getVertices()) {
                if(!numbered.contains(y)){
                    Integer yWeight = weightCopy.get(y);
                    minfill.sets.Set<Integer> possibleGraph = minfill.sets.Set.of(z,y);

                    for (Integer Xi : getVertices()) {
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
            minfill.sets.Set<Integer> mAdj = mAdj(order, i);
            if (!isClique(mAdj))
                return false;
        }
        return true;
    }

    @Contract(pure = true)
    default boolean isClique() {
        return isClique(getVertices());
    }

    @Contract(pure = true)
    default boolean isPotentialMaximalClique(minfill.sets.Set<Integer> k){
        Graph gk = inducedBy(getVertices().minus(k));
        java.util.Set<minfill.sets.Set<Integer>> s = new HashSet<>();
        for (minfill.sets.Set<Integer> component : gk.components()) {
            minfill.sets.Set<Integer> sI = neighborhood(component).intersect(k);
            if(!sI.isProperSubsetOf(k)){
                return false;
            }
            s.add(sI);
        }
        Graph cliqueChecker = this;
        for (minfill.sets.Set<Integer> sI : s) {
            minfill.sets.Set<Edge> fillEdges = cliqueChecker.cliqueify(sI);
            cliqueChecker = cliqueChecker.addEdges(fillEdges);
        }
        return cliqueChecker.inducedBy(k).isClique();
    }

    @Contract(pure = true)
    default boolean isVitalPotentialMaximalClique(minfill.sets.Set<Integer> vertices, int k) {
        if (!vertices.isSubsetOf(getVertices())) throw new IllegalArgumentException("Unknown vertex");
        return k >= 0 &&
                inducedBy(vertices).getNumberOfNonEdges() <= k &&
                isPotentialMaximalClique(vertices);
    }

    @Contract(pure = true)
    default minfill.sets.Set<minfill.sets.Set<Integer>> components() {
        java.util.Set<Integer> marked = new HashSet<>();
        java.util.Set<minfill.sets.Set<Integer>> components = new HashSet<>();

        for (Integer i : getVertices()) {
            if (!marked.contains(i)) {
                java.util.Set<Integer> component = new HashSet<>();
                Queue<Integer> queue = new ArrayDeque<>();
                queue.add(i);
                component.add(i);
                marked.add(i);


                while (!queue.isEmpty() && marked.size() != getVertices().size()) {
                    int vertex = queue.poll();

                    for (Integer neighbor : neighborhood(vertex)) {
                        if (!marked.contains(neighbor)) {
                            marked.add(neighbor);
                            component.add(neighbor);
                            queue.add(neighbor);
                        }
                    }
                }
                components.add(minfill.sets.Set.of(component));
            }
        }

        return minfill.sets.Set.of(components);
    }

    @Contract(pure = true)
    default minfill.sets.Set<minfill.sets.Set<Integer>> fullComponents(minfill.sets.Set<Integer> separator) {
        java.util.Set<minfill.sets.Set<Integer>> fullComponents = new HashSet<>();
        Graph gMinusS = this.inducedBy(this.getVertices().minus(separator));
        for (minfill.sets.Set<Integer> component : gMinusS.components()) {
            if (neighborhood(component).equals(separator)) {
                fullComponents.add(component);
            }
        }
        return minfill.sets.Set.of(fullComponents);
    }

    @Contract(pure = true)
    default List<Integer> shortestPath(Integer from, Integer to) {
        assert getVertices().contains(from);
        assert getVertices().contains(to);

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
    Graph addEdges(minfill.sets.Set<Edge> edges);

    @Contract(pure = true)
    default minfill.sets.Set<Edge> getNonEdges() {
        java.util.Set<Edge> nonEdges = new HashSet<>();

        PairIterable<Integer> vertexPairs = new PairIterable<>(getVertices());
        for(Pair<Integer, Integer> pair : vertexPairs){
            if (!isAdjacent(pair.a, pair.b)) {
                nonEdges.add(new Edge(pair.a, pair.b));
            }
        }
        return minfill.sets.Set.of(nonEdges);
    }

    @Contract(pure = true)
    default minfill.sets.Set<Edge> getEdges() {
        java.util.Set<Edge> edges = new HashSet<>();

        for (Integer v1 : getVertices()) {
            for (Integer v2 : getVertices()) {
                if (v1 < v2 && isAdjacent(v1, v2)) {
                    edges.add(new Edge(v1, v2));
                }
            }
        }

        return minfill.sets.Set.of(edges);
    }

    default int getNumberOfNonEdges() {
        int number = 0;
        PairIterable<Integer> vertexPairs = new PairIterable<>(getVertices());
        for(Pair<Integer, Integer> pair : vertexPairs){
            if (!isAdjacent(pair.a, pair.b)) {
                number++;
            }
        }
        return number;
    }

    @Contract(pure = true)
    Graph inducedBy(minfill.sets.Set<Integer> vertices);

    @Contract(pure = true)
    ChordalGraph minimalTriangulation();

    @Contract(pure = true)
    default minfill.sets.Set<Integer> mAdj(List<Integer> peo, int index) {
        minfill.sets.Set<Integer> neighborhood = neighborhood(peo.get(index)).toSet();
        return neighborhood.intersect(
                minfill.sets.Set.of(
                        peo.subList(index + 1, peo.size())
                ));
    }

    @Contract(pure = true)
    default minfill.sets.Set<Edge> cliqueify(minfill.sets.Set<Integer> vertices) {
        assert vertices.isSubsetOf(getVertices());

        java.util.Set<Edge> fill = new HashSet<>();

        PairIterable<Integer> vertexPairs = new PairIterable<>(vertices);
        for(Pair<Integer, Integer> pair : vertexPairs){
            if (!isAdjacent(pair.a, pair.b)) {
                fill.add(new Edge(pair.a, pair.b));
            }
        }

        return minfill.sets.Set.of(fill);
    }

    @Contract(pure = true)
    default boolean isClique(minfill.sets.Set<Integer> vertices) {
        assert vertices.isSubsetOf(getVertices());

        PairIterable<Integer> vertexPairs = new PairIterable<>(vertices);
        for(Pair<Integer, Integer> pair : vertexPairs){
            if (!isAdjacent(pair.a, pair.b)) {
                return false;
            }
        }
        return true;
    }

    default Optional<List<Integer>> findChordlessCycle() {
        for (minfill.sets.Set<Integer> component : components()) {
            List<Integer> order = inducedBy(component).maximumCardinalitySearch();

            for (int i = 0; i < order.size(); i++) {
                minfill.sets.Set<Integer> madj = mAdj(order, i);
                List<Integer> madjList = new ArrayList<>();
                for (Integer vertex : madj) {
                    madjList.add(vertex);
                }
                if (!isClique(madj)) {
                    // Cycle identified
                    for (int j = 0; j < madjList.size()-1; j++) {
                        Integer v = madjList.get(j);
                        for (int k = j+1; k < madjList.size(); k++) {
                            Integer w = madjList.get(k);

                            minfill.sets.Set<Integer> toRemove = madj.minus(minfill.sets.Set.of(v, w)).add(order.get(i));

                            Graph gPrime = inducedBy(getVertices().minus(toRemove));

                            if (!gPrime.isAdjacent(v, w) && gPrime.hasPath(v, w)) {
                                List<Integer> path = gPrime.shortestPath(v, w);
                                path.add(order.get(i));

                                assert path.size() >= 4;
                                assert !inducedBy(minfill.sets.Set.of(path)).isChordal();

                                return Optional.of(path);
                            }
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    default minfill.sets.Set<List<Integer>> findChordlessCycles() {
        minfill.sets.Set<List<Integer>> cycles = minfill.sets.Set.empty();
        for (minfill.sets.Set<Integer> component : components()) {
            List<Integer> order = inducedBy(component).maximumCardinalitySearch();

            for (int i = 0; i < order.size(); i++) {
                minfill.sets.Set<Integer> madj = mAdj(order, i);
                List<Integer> madjList = new ArrayList<>();
                for (Integer vertex : madj) {
                    madjList.add(vertex);
                }
                if (!isClique(madj)) {
                    // Cycle identified
                    Graph gPrime = inducedBy(getVertices().remove(order.get(i)));

                    for (int j = 0; j < madjList.size()-1; j++) {
                        Integer v = madjList.get(j);
                        for (int k = j+1; k < madjList.size(); k++) {
                            Integer w = madjList.get(k);
                            if (!gPrime.isAdjacent(v, w) && gPrime.hasPath(v, w)) {
                                List<Integer> path = gPrime.shortestPath(v, w);
                                path.add(order.get(i));

                                assert path.size() >= 4;

                                // todo check if path already in.
                                cycles = cycles.add(path);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return cycles;
    }

    @Override
    @Contract(pure = true)
    int hashCode();
}
