package minfill;

import minfill.graphs.adjacencymatrix.AdjacencyMatrixGraph;
import minfill.graphs.adjacencyset.AdjacencySetGraph;
import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.sets.Set;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class IOManager implements AutoCloseable {
    private String[] nodeNames;
    private final InputStream input;

    public IOManager() {
        this(System.in);
    }

    public IOManager(InputStream input) {
        this.input = input;
    }

    public void print(Set<Edge> minFill) {
        System.err.flush(); // Flush the error stream to avoid overlaps.
        for (Edge edge : minFill) {
            System.out.printf("%s %s\n", nodeNames[edge.from], nodeNames[edge.to]);
        }
        System.out.flush();
    }

    public Graph parse() {
        Map<String, Integer> nodeIndexer = new HashMap<>();
        int nextNodeId = 0;
        java.util.Set<Edge> edges = new HashSet<>();
        try (Scanner scanner = new Scanner(input)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                String[] tokens = line.split(" ");

                if (!nodeIndexer.containsKey(tokens[0])) {
                    nodeIndexer.put(tokens[0], nextNodeId++);
                }
                if (!nodeIndexer.containsKey(tokens[1])) {
                    nodeIndexer.put(tokens[1], nextNodeId++);
                }

                edges.add(new Edge(nodeIndexer.get(tokens[0]), nodeIndexer.get(tokens[1])));
            }
        }

        nodeNames = new String[nodeIndexer.size()];

        for (Map.Entry<String, Integer> entry : nodeIndexer.entrySet()) {
            nodeNames[entry.getValue()] = entry.getKey();
        }

        return new AdjacencySetGraph(Set.of(nodeIndexer.values()), Set.of(edges));
    }

    @Override
    public void close() throws IOException {
        nodeNames = null;
        input.close();
    }
}
