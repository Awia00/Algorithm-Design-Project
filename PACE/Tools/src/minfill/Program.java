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
            Pair<Graph, Integer> tmp = kernel.kernelize(g, k);
            Graph gPrime = tmp.o1;
            int kPrime = tmp.o2;

            if (kPrime < 0) {
                k -= kPrime;
            } else {
                Optional<Graph> result = mfi.stepB1(gPrime, kPrime);

                if (result.isPresent()) {
                    Set<Edge> minimumFill = result.get().getEdges().minus(g.getEdges());
                    io.print(minimumFill);
                    break;
                } else {
                    k++;
                }
            }
        }
    }
}
