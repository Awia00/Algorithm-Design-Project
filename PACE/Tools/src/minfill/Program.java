package minfill;

import minfill.data.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Optional;

public class Program {
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length != 0 && !args[0].startsWith("-")) { // Hack to read from file
            System.setIn(new FileInputStream(new File(args[0])));
        }

        IOManager io = new IOManager();
        MinFillKernel kernel = new MinFillKernel();
        MinFill mfi = new MinFill();

        Graph entireGraph = io.parse();
        System.err.printf("Graph of size (|V|, |E|) = (%d, %d)\n", entireGraph.vertices().size(), entireGraph.getEdges().size());

        for (Set<Integer> component : entireGraph.components()) {
            Graph g = entireGraph.inducedBy(component);
            System.err.printf("Component of size (|V|, |E|) = (%d, %d)\n", component.size(), g.getEdges().size());

            Triple<Set<Integer>, Set<Integer>, Integer> abk = kernel.kernelProcedure1And2(g);

            System.err.printf("Kernel procedure 1 and 2 done. k=%d\n", abk.c);

            int k = abk.c;
            while (true) {
                Optional<Pair<Graph, Integer>> tmp = kernel.kernelProcedure3(g, abk.a, abk.b, k);
                System.err.printf("Kernel procedure 3 for k=%d done\n", k);
                if (tmp.isPresent()) {
                    Graph gPrime = tmp.get().o1;
                    int kPrime = tmp.get().o2;

                    System.err.printf("k'=%d\n", kPrime);

                    Optional<Graph> result = mfi.stepB1(gPrime, kPrime);

                    if (result.isPresent()) {
                        Set<Edge> minimumFill = result.get().getEdges().minus(entireGraph.getEdges());
                        io.print(minimumFill);
                        System.err.println("Memoizer hits: " + MinFill.memoizerHits.longValue());
                        assert result.get().isChordal();
                        break;
                    }
                }
                k++;
            }
        }
    }
}
