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

        for (Set<Integer> component : entireGraph.components()) {
            Graph g = entireGraph.inducedBy(component);
            Triple<Set<Integer>, Set<Integer>, Integer> abk = kernel.kernelProcedure1And2(g);

            int k = abk.c;
            while (true) {
                Optional<Pair<Graph, Integer>> tmp = kernel.kernelProcedure3(g, abk.a, abk.b, k);
                if (tmp.isPresent()) {
                    Graph gPrime = tmp.get().o1;
                    int kPrime = tmp.get().o2;

                    Optional<Graph> result = mfi.stepB1(gPrime, kPrime);

                    if (result.isPresent()) {
                        Set<Edge> minimumFill = result.get().getEdges().minus(entireGraph.getEdges());
                        io.print(minimumFill);
                        System.err.println("Memoizer hits: " + MinFill.memoizerHits.longValue());
                        break;
                    }
                }
                k++;
            }
        }


    }
}
