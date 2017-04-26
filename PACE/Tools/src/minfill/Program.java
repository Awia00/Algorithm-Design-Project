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
    static final boolean printDebug = false;
    private static MinFillKernel kernel = new MinFillKernel();
    private static MinFillEasySolver easySolver = new MinFillEasySolver();
    private static MinFill mfi = new MinFill();
    private static IO io;

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length != 0 && !args[0].startsWith("-")) { // Hack to read from file
            System.setIn(new FileInputStream(new File(args[0])));
        }

        io = new IO();

        Graph entireGraph = io.parse();
        IO.printf("Graph of size (|V|, |E|) = (%d, %d)\n", entireGraph.vertices().size(), entireGraph.getEdges().size());

        io.print(minFill(entireGraph));
    }

    public static Set<Edge> minFill(Graph entireGraph){
        Set<Edge> componentResult = Set.empty();
        for (Set<Integer> component : entireGraph.components()) {
            componentResult = componentResult.union(perComponent(entireGraph.inducedBy(component)));
        }

        System.err.println(componentResult.size());
        assert entireGraph.addEdges(componentResult).isChordal();
        return componentResult;
    }

    private static Set<Edge> perComponent(Graph g) {
        IO.printf("Component of size (|V|, |E|) = (%d, %d)\n", g.vertices().size(), g.getEdges().size());

        Triple<Set<Integer>, Set<Integer>, Integer> abk = kernel.kernelProcedure1And2(g);

        IO.printf("Kernel procedure 1 and 2 done. k=%d\n", abk.c);

        int k = abk.c;

        while (true) {
            Optional<Pair<Graph, Integer>> tmp = kernel.kernelProcedure3(g, abk.a, abk.b, k);
            IO.printf("Kernel procedure 3 for k=%d done\n", k);
            if (tmp.isPresent()) {
                Graph gPrime = tmp.get().a;
                int kPrime = tmp.get().b;

                Set<Edge> kernelAddedEdges = gPrime.getEdges().minus(g.getEdges());
                Set<Edge> easyEdges = easySolver.findEasyEdges(gPrime);
                System.err.println("Easy edges done, found " + easyEdges.size());

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

                IO.printf("k'=%d\n", kPrime);

                Optional<Graph> result = mfi.stepB1(gPrime, kPrime);

                if (result.isPresent()) {
                    Set<Edge> minimumFill = result.get().getEdges().minus(g.getEdges());

                    IO.println("Memoizer hits: " + MinFill.memoizerHits.longValue());

                    assert result.get().isChordal();
                    assert gPrime.addEdges(minimumFill).isChordal();

                    return minimumFill.union(kernelAddedEdges).union(easyEdges);
                }
            }
            k++;
        }
    }
}
