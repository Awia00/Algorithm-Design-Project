package minfill.data;

import java.util.Map;

public interface Graph {
    Set<Integer> vertices();
    Map<Integer, Set<Integer>> neighborhoods();
    Set<Integer> neighborhood(int n);
    Set<Integer> neighborhood(Set<Integer> vertices);
    boolean isAdjacent(int a, int b);
    boolean hasPath(int a, int b);
    boolean isChordal();
    boolean isClique();
    default boolean isPotentialMaximalClique(Set<Integer> k){
        Graph gk = inducedBy(vertices().minus(k));
        Set<Set<Integer>> s = Set.empty();
        for (Set<Integer> component : gk.components()) {
            Set<Integer> sI = neighborhood(component.intersect(k));
            s.add(sI);
            if(!sI.isProperSubsetOf(k)){
                return false;
            }
        }
        Graph cliqueChecker = this;
        for (Set<Integer> sI : s) {
            cliqueChecker = cliqueChecker.cliqueify(sI);
        }
        return cliqueChecker.inducedBy(k).isClique();
    }

    // TODO: isPotentialMaximalClique could probably be implemented in terms of this.
    boolean isVitalPotentialMaximalClique(Set<Integer> vertices, int k);

    Set<Set<Integer>> components();
    Set<Set<Integer>> fullComponents();
    Set<Set<Integer>> minimalSeparators();
    Set<Set<Integer>> maximalCliquesOfChordalGraph(); // TODO: We have an algorithm for this. [3]
    Set<Integer> shortestPath(int from, int to);
    Graph addEdge(Edge e);
    Set<Edge> getNonEdges();
    Graph inducedBy(Set<Integer> vertices);
    Graph minimalTriangulation();
    Graph cliqueify(Set<Integer> vertices);

    boolean isClique(Set<Integer> vertices);

    @Override
    int hashCode();
}
