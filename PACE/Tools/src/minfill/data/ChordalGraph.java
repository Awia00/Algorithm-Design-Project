package minfill.data;

import org.jetbrains.annotations.Contract;

public interface ChordalGraph extends Graph {
    @Contract(pure = true)
    Set<Set<Integer>> minimalSeparators();

    @Contract(pure = true)
    Set<Set<Integer>> maximalCliques();
}
