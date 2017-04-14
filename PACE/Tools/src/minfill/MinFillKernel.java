package minfill;

import minfill.data.Graph;
import minfill.data.Pair;
import org.jetbrains.annotations.Contract;

public class MinFillKernel {
    @Contract(pure = true)
    public Pair<Graph, Integer> kernelize(Graph g, int k) {
        //throw new RuntimeException("Not implemented");

        Graph gPrime = g;

        boolean hasChanges = true;
        while (hasChanges) {
            hasChanges = false;

            for (Integer vertex : gPrime.vertices()) {
                if (gPrime.isClique(gPrime.neighborhood(vertex))) {
                    gPrime = gPrime.inducedBy(gPrime.vertices().remove(vertex));
                    hasChanges = true;
                }
            }
        }

        return new Pair<>(gPrime, k);
    }
}
