import minfill.IOManager;
import minfill.MinFillKernel;
import minfill.data.Graph;
import minfill.data.Pair;
import minfill.data.Set;
import minfill.data.Triple;

import java.io.IOException;
import java.util.Optional;

public class Kernelizer {

    public static void main(String[] args) throws IOException {
        try (IOManager io = new IOManager(Util.getInput(args))) {
            io.print(kernelize(io.parse()).getEdges());
        }
    }

    public static Graph kernelize(Graph g) {
        MinFillKernel kernel = new MinFillKernel();
        Triple<Set<Integer>, Set<Integer>, Integer> abk = kernel.kernelProcedure1And2(g);

        int k = abk.c - 1;
        Optional<Pair<Graph, Integer>> gk;
        do {
            gk = kernel.kernelProcedure3(g, abk.a, abk.b, ++k);
        } while(!gk.isPresent());

        return gk.get().a;
    }
}
