package minfill;

import minfill.data.Graph;
import minfill.data.Pair;
import org.jetbrains.annotations.Contract;

public class MinFillKernel {
    @Contract(pure = true)
    public Pair<Graph, Integer> kernelize(Graph g, int k) {
        //throw new RuntimeException("Not implemented");
        return new Pair<>(g, k);
    }
}
