package minfill;

import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.tuples.Pair;
import minfill.sets.Set;
import minfill.tuples.Triple;
import minfill.tuples.Tuple;
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
            Optional<List<Integer>> cycle = g.inducedBy(B).findChordlessCycle();
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

            for (Integer u : A) {
                for (Integer x : g.neighborhood(u).toSet().intersect(B)) {
                    Graph gPrime = g.inducedBy(g.vertices().remove(x));
                    Set<Integer> R = (g.neighborhood(x).toSet().minus(g.neighborhood(u).toSet())).intersect(B);

                    for (Integer v : R) {
                        if (gPrime.hasPath(u, v)) {
                            cycleFound = true;
                            List<Integer> path = gPrime.shortestPath(u, v);
                            path.add(x);

                            List<Set<Integer>> subPaths = new ArrayList<>();

                            boolean prevInB = false;
                            java.util.Set<Integer> subPath = new HashSet<>();
                            for (Integer vertex : path) {
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
                                if (subPaths.get(0).size() == path.size() - 1) {
                                    kMin += subPaths.get(0).size() - 1;
                                } else {
                                    kMin += subPaths.get(0).size() - 2;
                                }
                            } else {
                                kMin += Math.max(subPaths.stream().mapToInt(Set::size).sum() / 2, subPaths.get(0).size());
                            }

                            continue p2;
                        }
                    }
                }
            }
        } while (cycleFound);

        return Tuple.of(A, B, kMin);
    }

    @Contract(pure = true)
    public Optional<Pair<Graph, Integer>> kernelProcedure3(Graph g, Set<Integer> A, Set<Integer> B, int k) {
        int kPrime = k;

        // P3
        for (Edge nonEdge : g.inducedBy(A).getNonEdges()) {
            int x = nonEdge.from, y = nonEdge.to;

            Set<Integer> bNeighbors = g.neighborhood(x).toSet().intersect(g.neighborhood(y).toSet()).intersect(B);
            java.util.Set<Integer> Axy = new HashSet<>();

            for (Integer b : bNeighbors) {
                Graph gPrime = g.inducedBy(g.vertices().remove(b));

                if (gPrime.hasPath(x, y)) {
                    Axy.add(b);
                }
            }

            if (Axy.size() > 2*k) {
                g = g.addEdge(nonEdge);
                kPrime--;

                if (kPrime < 0) return Optional.empty();
            } else {
                Set<Integer> set = Set.of(Axy);
                A = A.union(set);
                B = B.minus(set);
            }
        }

        return Optional.of(Tuple.of(g.inducedBy(A), kPrime));
    }
}
