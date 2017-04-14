package minfill;

import minfill.data.Edge;
import minfill.data.Graph;
import minfill.data.Pair;
import minfill.data.Set;

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


        Graph g = io.parse();

        int k = 0;
        while (true) {
            Optional<Pair<Graph, Integer>> tmp = kernel.kernelize(g, k);
            if (tmp.isPresent()) {
                Graph gPrime = tmp.get().o1;
                int kPrime = tmp.get().o2;

                Optional<Graph> result = mfi.stepB1(gPrime, kPrime);

                if (result.isPresent()) {
                    Set<Edge> minimumFill = result.get().getEdges().minus(g.getEdges());
                    io.print(minimumFill);
                    System.err.println("Memoizer hits: " + MinFill.memoizerHits.longValue());
                    return;
                }
            }
            k++;
        }
    }
}
