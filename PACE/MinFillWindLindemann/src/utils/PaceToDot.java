package utils;

import minfill.graphs.Edge;
import minfill.graphs.Graph;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class PaceToDot {

    public static void main(String[] args) throws IOException {
        System.out.println(generateFromStream(Util.getInput(args)));
    }

    public static String generateFromStream(InputStream stream){
        String result = "Graph g {\n";
        try (Scanner scanner = new Scanner(stream)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                String[] split = line.split(" ");

                result += String.format("\t%s--%s\n", split[0], split[1]);
            }
        }
        return result + "}";
    }

    public static String generateFromGraph(Graph g){
        String result = "Graph g {\n";
        for (Edge edge : g.getEdges()) {
            result += String.format("\t%s--%s\n", edge.from, edge.to);
        }
        return result + "}";
    }
}
