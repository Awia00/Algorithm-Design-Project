package minfill;

import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.sets.Set;
import minfill.tuples.Pair;
import minfill.tuples.Triple;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Optional;

public class Program {
    private static MinFillKernel kernel;
    private static MinFillEasySolver easySolver;
    private static MinFill mfi;
    private static IOManager io;

    public static void main(String[] args) throws FileNotFoundException {
        System.err.close();
        if (args.length != 0 && !args[0].startsWith("-")) { // Hack to read from file
            System.setIn(new FileInputStream(new File(args[0])));
        }

        io = new IOManager();
        kernel = new MinFillKernel();
        easySolver = new MinFillEasySolver();
        mfi = new MinFill();

        Graph entireGraph = io.parse();
        System.err.printf("Graph of size (|V|, |E|) = (%d, %d)\n", entireGraph.vertices().size(), entireGraph.getEdges().size());

        Set<Edge> componentResult = Set.empty();
        for (Set<Integer> component : entireGraph.components()) {
            componentResult = componentResult.union(perComponent(entireGraph.inducedBy(component)));
        }
        io.print(componentResult);
        assert entireGraph.addEdges(componentResult).isChordal();
    }

    private static Set<Edge> perComponent(Graph g) {
        System.err.printf("Component of size (|V|, |E|) = (%d, %d)\n", g.vertices().size(), g.getEdges().size());

        Triple<Set<Integer>, Set<Integer>, Integer> abk = kernel.kernelProcedure1And2(g);

        System.err.printf("Kernel procedure 1 and 2 done. k=%d\n", abk.c);

        int k = abk.c;

        while (true) {
            Optional<Pair<Graph, Integer>> tmp = kernel.kernelProcedure3(g, abk.a, abk.b, k);
            System.err.printf("Kernel procedure 3 for k=%d done\n", k);
            if (tmp.isPresent()) {
                Graph gPrime = tmp.get().a;
                int kPrime = tmp.get().b;

                Set<Edge> kernelAddedEdges = gPrime.getEdges().minus(g.getEdges());
                Set<Edge> easyEdges = easySolver.findEasyEdges(gPrime);
                gPrime = gPrime.addEdges(easyEdges);
                kPrime -= easyEdges.size();

                if(!easyEdges.isEmpty())
                    return perComponent(gPrime).union(kernelAddedEdges).union(easyEdges);

                Set<Set<Integer>> components = gPrime.components();

                if (components.size() > 1) {
                    Set<Edge> componentResult = Set.empty();
                    for (Set<Integer> component : components) {
                        componentResult = componentResult.union(perComponent(gPrime.inducedBy(component)));
                    }
                    return componentResult.union(kernelAddedEdges).union(easyEdges);
                }

                System.err.printf("k'=%d\n", kPrime);

                Optional<Graph> result = mfi.stepB1(gPrime, kPrime);

                if (result.isPresent()) {
                    Set<Edge> minimumFill = result.get().getEdges().minus(g.getEdges());

                    System.err.println("Memoizer hits: " + MinFill.memoizerHits.longValue());

                    assert result.get().isChordal();
                    assert gPrime.addEdges(minimumFill).isChordal();

                    return minimumFill.union(kernelAddedEdges).union(easyEdges);
                }
            }
            k++;
        }
    }
}
