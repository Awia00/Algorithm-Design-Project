package utils;


import minfill.IO;
import minfill.MinFillKernel;
import minfill.graphs.Graph;
import minfill.tuples.Pair;
import minfill.sets.Set;
import minfill.tuples.Triple;

import java.io.IOException;
import java.util.Optional;

public class Kernelizer {
    private static MinFillKernel kernel = new MinFillKernel();

    public static void main(String[] args) throws IOException {
        try (IO io = new IO(Util.getInput(args))) {
            io.print(kernelize(io.parse()).getEdges());
        }
    }

    public static Graph kernelize(Graph g) {
        return kernelizeWithK(g).a;
    }

    public static Pair<Graph, Integer> kernelizeWithK(Graph g) {
        Triple<Set<Integer>, Set<Integer>, Integer> abk = kernel.kernelProcedure1And2(g);

        int k = abk.c - 1;
        Optional<Pair<Graph, Integer>> gk;
        do {
            gk = kernel.kernelProcedure3(g, abk.a, abk.b, ++k);
        } while(!gk.isPresent());

        return gk.get();
    }
}
