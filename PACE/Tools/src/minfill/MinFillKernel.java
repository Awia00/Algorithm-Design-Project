package minfill;

import minfill.data.*;
import minfill.data.Set;
import org.jetbrains.annotations.Contract;

import java.util.*;

public class MinFillKernel {
    @Contract(pure = true)
    public Triple<Set<Integer>, Set<Integer>, Integer> kernelProcedure1And2(Graph g) {
        Set<Integer> A = Set.empty(), B = g.vertices();
        int kMin = 0;

        // P1
        boolean cycleFound;
        do {
            cycleFound = false;
            Optional<List<Integer>> cycle = findChordlessCycle(g.inducedBy(B));
            if (cycle.isPresent()) {
                cycleFound = true;
                Set<Integer> cycleSet = Set.of(cycle.get());
                assert cycleSet.size() >= 4;

                kMin += cycleSet.size() - 3;

                A = A.union(cycleSet);
                B = B.minus(cycleSet);
            }
        } while (cycleFound);

        // P2
        p2:
        do {
            cycleFound = false;

            for (int v : B) {
                Set<Integer> neighborhoodV = g.neighborhood(v);
                for (int w : neighborhoodV.intersect(B)) {
                    Graph gPrime = g
                            .inducedBy(g.vertices().minus(neighborhoodV.intersect(g.neighborhood(w))))
                            .removeEdge(new Edge(v, w));

                    if (gPrime.hasPath(v, w)) {
                        cycleFound = true;
                        List<Integer> path = gPrime.shortestPath(v, w);

                        List<Set<Integer>> subPaths = new ArrayList<>();

                        int startIndex = -1;
                        for (int i = 0; i < path.size(); i++) {
                            if (A.contains(path.get(i))) {
                                startIndex = i;
                                break;
                            }
                        }

                        assert startIndex != -1;

                        boolean prevInB = false;
                        java.util.Set<Integer> subPath = new HashSet<>();
                        for (int i = startIndex; i < path.size() + startIndex; i++) {
                            Integer vertex = path.get(i % path.size());
                            if (prevInB) {
                                if (B.contains(vertex)) {
                                    subPath.add(vertex);
                                } else {
                                    subPaths.add(Set.of(subPath));
                                    subPath = new HashSet<>();
                                    prevInB = false;
                                }
                            } else {
                                if (B.contains(vertex)) {
                                    subPath.add(vertex);
                                    prevInB = true;
                                }
                            }
                        }
                        if(!subPath.isEmpty()) subPaths.add(Set.of(subPath));

                        Set<Integer> vertices = Set.of(path);
                        A = A.union(vertices);
                        B = B.minus(vertices);

                        subPaths.sort(Comparator.comparing(sub -> -sub.size()));

                        if (subPaths.size() == 1) {
                            if (subPaths.get(0).size() == path.size() - 2) {
                                kMin += subPaths.get(0).size() - 1;
                            } else {
                                kMin += subPaths.get(0).size();
                            }
                        } else {
                            kMin += Math.max(subPaths.stream().mapToInt(Set::size).sum() / 2, subPaths.get(0).size());
                        }

                        continue p2;
                    }
                }
            }
        } while (cycleFound);

        return new Triple<>(A, B, kMin);
    }

    @Contract(pure = true)
    public Optional<Pair<Graph, Integer>> kernelProcedure3(Graph g, Set<Integer> A, Set<Integer> B, int k) {
        int kPrime = k;

        // P3
        for (Edge nonEdge : g.inducedBy(A).getNonEdges()) {
            int x = nonEdge.from, y = nonEdge.to;

            Set<Integer> bNeighbors = g.neighborhood(x).intersect(g.neighborhood(y)).intersect(B);
            java.util.Set<Integer> Axy = new HashSet<>();

            for (Integer b : bNeighbors) {
                Graph gPrime = g.inducedBy(g.vertices().remove(b));

                if (gPrime.hasPath(x, y)) {
                    Axy.add(b);
                }
            }

            if (Axy.size() > 2*k) {
                g = g.addEdge(nonEdge);
                kPrime--; // TODO: Should we reduce k instead (for later non-edges)?.

                if (kPrime < 0) return Optional.empty();
            } else {
                Set<Integer> set = Set.of(Axy);
                A = A.union(set);
                B = B.minus(set);
            }
        }

        return Optional.of(new Pair<>(g.inducedBy(A), kPrime));
    }


    private Optional<List<Integer>> findChordlessCycle(Graph g) {
        for (Set<Integer> component : g.components()) {
            List<Integer> order = g.inducedBy(component).maximumCardinalitySearch();

            for (int i = 0; i < order.size(); i++) {
                Set<Integer> madj = mAdj(g, order, i);
                List<Integer> madjList = new ArrayList<>();
                for (Integer vertex : madj) {
                    madjList.add(vertex);
                }
                if (!g.isClique(madj)) {
                    // Cycle identified
                    Graph gPrime = g.inducedBy(g.vertices().remove(order.get(i)));

                    for (int j = 0; j < madjList.size()-1; j++) {
                        Integer v = madjList.get(j);
                        for (int k = j+1; k < madjList.size(); k++) {
                            Integer w = madjList.get(k);
                            if (!gPrime.isAdjacent(v, w) && gPrime.hasPath(v, w)) {
                                List<Integer> path = gPrime.shortestPath(v, w);
                                path.add(order.get(i));

                                assert path.size() >= 4;

                                return Optional.of(path);
                            }
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    private Set<Integer> mAdj(Graph g, List<Integer> peo, int index) {
        Set<Integer> neighborhood = g.neighborhood(peo.get(index));
        return neighborhood.intersect(
                Set.of(
                        peo.subList(index + 1, peo.size())
                ));
    }
}
