package minfill;

import minfill.data.*;
import minfill.data.Set;

import java.util.*;

public class IOManager {
    private String[] nodeNames;

    public void print(Set<Edge> minFill) {
        for (Edge edge : minFill) {
            System.out.printf("%s %s", nodeNames[edge.from], nodeNames[edge.to]);
        }
    }

    public Graph parse() {
        Map<String, Integer> nodeIndexer = new HashMap<>();
        int nextNodeId = 0;
        java.util.Set<Edge> edges = new HashSet<>();
        try (Scanner scanner = new Scanner(System.in)) {
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

        return new ImmutableGraph(new ImmutableSet<>(nodeIndexer.values()), new ImmutableSet<>(edges));
    }
}
