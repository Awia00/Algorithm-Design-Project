package minfill.data;

import org.jetbrains.annotations.Contract;

import java.util.HashSet;
import java.util.List;

public interface Graph {
    @Contract(pure = true)
    Set<Integer> vertices();

    @Contract(pure = true)
    Set<Integer> neighborhood(Integer n);

    @Contract(pure = true)
    Set<Integer> neighborhood(Set<Integer> vertices);

    @Contract(pure = true)
    boolean isAdjacent(Integer a, Integer b);

    @Contract(pure = true)
    boolean hasPath(Integer a, Integer b);

    @Contract(pure = true)
    List<Integer> maximumCardinalitySearch();

    @Contract(pure = true) // berry page 5
    Pair<List<Integer>, Set<Edge>> maximumCardinalitySearchM();

    @Contract(pure = true)
    boolean isChordal();

    @Contract(pure = true)
    boolean isClique();

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

    // TODO: isPotentialMaximalClique could probably be implemented in terms of this.
    @Contract(pure = true)
    boolean isVitalPotentialMaximalClique(Set<Integer> vertices, int k);

    @Contract(pure = true)
    Set<Set<Integer>> components();

    @Contract(pure = true)
    Set<Set<Integer>> fullComponents(Set<Integer> separator);

    @Contract(pure = true)
    Set<Set<Integer>> minimalSeparatorsOfChordalGraph();

    @Contract(pure = true)
    Set<Set<Integer>> maximalCliquesOfChordalGraph(); // TODO: We have an algorithm for this. [3]

    @Contract(pure = true)
    List<Integer> shortestPath(Integer from, Integer to);

    @Contract(pure = true)
    Graph addEdge(Edge e);

    @Contract(pure = true)
    Graph addEdges(Set<Edge> edges);

    @Contract(pure = true)
    Graph removeEdge(Edge edge);

    @Contract(pure = true)
    Set<Edge> getNonEdges();

    @Contract(pure = true)
    Set<Edge> getEdges();

    int getNumberOfNonEdges();

    @Contract(pure = true)
    Graph inducedBy(Set<Integer> vertices);

    @Contract(pure = true)
    Graph minimalTriangulation();

    @Contract(pure = true)
    Set<Edge> cliqueify(Set<Integer> vertices);

    @Contract(pure = true)
    boolean isClique(Set<Integer> vertices);

    @Override
    @Contract(pure = true)
    int hashCode();
}
