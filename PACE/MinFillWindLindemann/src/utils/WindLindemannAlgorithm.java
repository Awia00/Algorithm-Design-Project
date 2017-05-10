package utils;

import minfill.IO;
import minfill.kernel.MinFillKernel;
import minfill.kernel.MinimumFillKernel;
import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.sets.Set;
import minfill.tuples.Pair;
import minfill.tuples.Triple;
import minfill.tuples.Tuple;

import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.PriorityQueue;

public class WindLindemannAlgorithm {
    private static MinimumFillKernel kernel = new MinFillKernel();

    public static void main(String[] args) throws FileNotFoundException {
        IO io = new IO(Util.getInput(args));

        Graph g = io.parse();

        java.util.Set<Edge> minFill = new HashSet<>();
        for (Set<Integer> component : g.components()) {
            for (Edge edge : perComponent(g.inducedBy(component))) {
                minFill.add(edge);
            }
        }

        Graph filled = g.addEdges(Set.of(minFill));
        assert filled.isChordal();

        Set<Edge> F = filled.getEdges().minus(g.getEdges());

        IO.println("|F| = " + F.size());
        io.print(F);
    }

    public static Set<Edge> perComponent(Graph g) {
        if (g.isChordal()) return Set.empty();

        Triple<Set<Integer>, Set<Integer>, Integer> abk = kernel.kernelProcedure1And2(g);

        int k = abk.c;
        while (true) {
            IO.printf("k=%d\n", k);
            Optional<Pair<Graph, Integer>> option = kernel.kernelProcedure3(g, abk.a, abk.b, k);

            if (option.isPresent()) {
                Graph gPrime = option.get().a;
                int kPrime = option.get().b;

                Set<Edge> edgesAddedByKernel = gPrime.getEdges();

                Set<Set<Integer>> components = gPrime.components();
                if (components.size() != 1) {
                    boolean hasResult = true;
                    java.util.Set<Edge> minFill = new HashSet<>();
                    for (Set<Integer> component : components) {
                        Optional<Set<Edge>> maybeEdges = perComponent(gPrime.inducedBy(component), kPrime);

                        if (maybeEdges.isPresent()) {
                            for (Edge edge : maybeEdges.get()) {
                                minFill.add(edge);
                            }
                        } else {
                            hasResult = false;
                            break;
                        }
                    }

                    if (hasResult) {
                        IO.println("Found components in graph.");
                        return Set.of(minFill).union(edgesAddedByKernel);
                    }
                } else {
                    Graph best = null;
                    int bestK = kPrime;
                    Set<Edge> bestNonEdgesAdded = Set.empty();

                    IO.println("Now trying all non-edges.");
                    for (Edge nonEdge : gPrime.getNonEdges()) {
                        Graph gNonEdge = gPrime.addEdge(nonEdge);

                        Pair<Graph, Integer> kernelized = Kernelizer.kernelizeWithK(gNonEdge);

                        if (kernelized.b < bestK) {
                            best = kernelized.a;
                            bestK = kernelized.b;
                            bestNonEdgesAdded = gNonEdge.getEdges().union(kernelized.a.getEdges()).minus(gPrime.getEdges());
                        }
                    }

                    if (best == null) {
                        IO.println("No good edge found.");
                    } else {
                        if (best.isChordal()) {
                            IO.println("Found result!");
                            return best.getEdges().union(bestNonEdgesAdded).union(edgesAddedByKernel);
                        }
                        IO.println("Search deeper!");
                        Optional<Set<Edge>> maybeEdges = perComponent(best, kPrime - bestNonEdgesAdded.size());

                        if (maybeEdges.isPresent()) {
                            return maybeEdges.get().union(bestNonEdgesAdded).union(edgesAddedByKernel);
                        }
                    }
                }
            }
            k++;
        }
    }

    public static Optional<Set<Edge>> perComponent(Graph gPrime, int kPrime) {
        if (gPrime.isChordal()) return Optional.of(Set.empty());
        if (kPrime == 0) return Optional.empty();

        Set<Set<Integer>> components = gPrime.components();
        if (components.size() != 1) {
            boolean hasResult = true;
            java.util.Set<Edge> minFill = new HashSet<>();
            for (Set<Integer> component : components) {
                Optional<Set<Edge>> maybeEdges = perComponent(gPrime.inducedBy(component), kPrime);

                if (maybeEdges.isPresent()) {
                    for (Edge edge : maybeEdges.get()) {
                        minFill.add(edge);
                    }
                } else {
                    hasResult = false;
                    break;
                }
            }

            if (hasResult) {
                IO.println("Found components in graph.");
                return Optional.of(Set.of(minFill));
            }
        } else {
            IO.println("Now trying all non-edges.");
            PriorityQueue<Triple<Graph, Integer, Set<Edge>>> pq = new PriorityQueue<>(Comparator.comparing(a -> a.b));

            for (Edge nonEdge : gPrime.getNonEdges()) {
                Graph gNonEdge = gPrime.addEdge(nonEdge);

                Pair<Graph, Integer> kernelized = Kernelizer.kernelizeWithK(gNonEdge);

                if (kernelized.b < kPrime) {
                    pq.add(Tuple.of(kernelized.a, kernelized.b, gNonEdge.getEdges().union(kernelized.a.getEdges()).minus(gPrime.getEdges())));
                }
            }

            while (!pq.isEmpty()) {
                Triple<Graph, Integer, Set<Edge>> good = pq.poll();

                if (good.a.isChordal()) {
                    IO.println("Found result!");
                    return Optional.of(good.a.getEdges().union(good.c));
                }
                IO.println("Search deeper!");
                Optional<Set<Edge>> maybeEdges = perComponent(good.a, kPrime - good.c.size());

                if (maybeEdges.isPresent()) {
                    Set<Edge> fill = maybeEdges.get().union(good.c);

                    if (fill.size() < kPrime) {
                        return Optional.of(maybeEdges.get().union(good.c));
                    }
                }
            }
        }

        return Optional.empty();
    }
}