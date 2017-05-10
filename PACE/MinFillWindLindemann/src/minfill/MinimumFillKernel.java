package minfill;

import minfill.graphs.Graph;
import minfill.sets.Set;
import minfill.tuples.Pair;
import minfill.tuples.Triple;
import org.jetbrains.annotations.Contract;

import java.util.Optional;

public interface MinimumFillKernel {
    @Contract(pure = true)
    Triple<Set<Integer>, Set<Integer>, Integer> kernelProcedure1And2(Graph g);

    @Contract(pure = true)
    Optional<Pair<Graph, Integer>> kernelProcedure3(Graph g, Set<Integer> A, Set<Integer> B, int k);
}
