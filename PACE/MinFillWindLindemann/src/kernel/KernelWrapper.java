package kernel;

import minfill.MinimumFillKernel;
import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.graphs.adjacencyset.AdjacencySetGraph;
import minfill.sets.Set;
import minfill.tuples.*;
import minfill.tuples.Pair;

import java.util.*;

public class KernelWrapper implements MinimumFillKernel {
    private Kernel<Integer> kernel;

    @Override
    public Triple<Set<Integer>, Set<Integer>, Integer> kernelProcedure1And2(Graph g) {
        kernel = new Kernel<>(toMap(g));

        Holder<Integer> res = kernel.runPhase1n2();

        return Tuple.of(Set.of(res.A), Set.of(res.B), res.cc);
    }

    @Override
    public Optional<Pair<Graph, Integer>> kernelProcedure3(Graph g, Set<Integer> A, Set<Integer> B, int k) {
        Holder<Integer> res = kernel.runPhase3(k);

        if (res.cc > k) return Optional.empty();
        return Optional.of(Tuple.of(toGraph(res.graph), res.cc));
    }

    private Map<Integer, List<Integer>> toMap(Graph g) {
        Map<Integer, List<Integer>> map = new HashMap<>();

        for (Edge edge : g.getEdges()) {
            map.putIfAbsent(edge.from, new ArrayList<>());
            map.putIfAbsent(edge.to, new ArrayList<>());

            map.get(edge.from).add(edge.to);
            map.get(edge.to).add(edge.from);
        }

        return map;
    }

    private Graph toGraph(Map<Integer, List<Integer>> map) {
        java.util.Set<Edge> edges = new HashSet<>();
        java.util.Set<Integer> vertices = new HashSet<>();

        for (Map.Entry<Integer, List<Integer>> edgeEntry : map.entrySet()) {
            Integer from = edgeEntry.getKey();
            vertices.add(from);
            for (Integer to : edgeEntry.getValue()) {
                vertices.add(to);
                edges.add(new Edge(from, to));
            }
        }

        return new AdjacencySetGraph(Set.of(vertices), Set.of(edges));
    }
}
