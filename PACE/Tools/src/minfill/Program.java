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
    private static MinFillKernel kernel = new MinFillKernel();
    private static MinFillEasySolver easySolver = new MinFillEasySolver();
    private static MinFill mfi = new MinFill();
    private static IO io = new IO();

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length != 0 && !args[0].startsWith("-")) { // Hack to read from file
            io = new IO(new FileInputStream(new File(args[0])));
        }

        Graph entireGraph = io.parse();
        IO.printf("Graph of size (|V|, |E|) = (%d, %d)\n", entireGraph.vertices().size(), entireGraph.getEdges().size());

        io.print(minFill(entireGraph));
    }

    public static Set<Edge> minFill(Graph entireGraph){
        Set<Edge> componentResult = Set.empty();
        for (Set<Integer> component : entireGraph.components()) {
            componentResult = componentResult.union(perComponent(entireGraph.inducedBy(component)));
        }

        IO.printf("minFillSize: %d\n",componentResult.size());
        assert entireGraph.addEdges(componentResult).isChordal();
        for (Edge edge : componentResult) {
            assert !entireGraph.addEdges(componentResult.remove(edge)).isChordal(); // minimality
        }
        return componentResult;
    }

    private static Set<Edge> perComponent(Graph g) {
        IO.printf("Component of size (|V|, |E|) = (%d, %d)\n", g.vertices().size(), g.getEdges().size());

        Triple<Set<Integer>, Set<Integer>, Integer> abk = kernel.kernelProcedure1And2(g);

        IO.printf("Kernel procedure 1 and 2 done. k=%d\n", abk.c);

        int k = abk.c;

        while (true) {
            Optional<Pair<Graph, Integer>> tmp = kernel.kernelProcedure3(g, abk.a, abk.b, k);
            if (tmp.isPresent()) {
                Graph gPrime = tmp.get().a;
                int kPrime = tmp.get().b;

                Set<Edge> kernelAddedEdges = gPrime.getEdges().minus(g.getEdges());
                int amtOfRemovedVertices = g.vertices().size()-gPrime.vertices().size();
                IO.printf("Kernel procedure 3 for k=%d, edges added= %d vertices pruned=%d \n", kPrime, kernelAddedEdges.size(), amtOfRemovedVertices);

                Set<Set<Integer>> components = gPrime.components();

                if (components.size() > 1) {
                    Set<Edge> componentResult = Set.empty();
                    for (Set<Integer> component : components) {
                        componentResult = componentResult.union(perComponent(gPrime.inducedBy(component)));
                    }
                    return componentResult.union(kernelAddedEdges);
                }

                Set<Edge> easyEdges = easySolver.findEasyEdges(gPrime);
                IO.printf("Easy edges done, found %d\n", easyEdges.size());

                gPrime = gPrime.addEdges(easyEdges);
                kPrime -= easyEdges.size();

                if(!easyEdges.isEmpty())
                    return perComponent(gPrime).union(kernelAddedEdges).union(easyEdges);

                Optional<Graph> result = mfi.stepB1(gPrime, kPrime);

                if (result.isPresent()) {
                    Set<Edge> minimumFill = result.get().getEdges().minus(g.getEdges());

                    IO.printf("MinFill found of size: %d Memoizer hits: %d\n", minimumFill.size(), MinFill.memoizerHits.longValue());
                    MinFill.memoizerHits.reset();

                    assert result.get().isChordal();
                    assert gPrime.addEdges(minimumFill).isChordal();

                    return minimumFill.union(kernelAddedEdges).union(easyEdges);
                }
            }
            k++;
        }
    }
}
