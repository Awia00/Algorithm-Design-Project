package minfill;

import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.sets.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aws on 26-04-2017.
 */

public class MinFillAlgorithmTest {

    private static List<String> graphs;
    private static List<String> badGraphs;

    @BeforeAll
    public static void Setup()
    {
        graphs = new ArrayList<>();
        badGraphs = new ArrayList<>();

        badGraphs.add("1.graph"); // kernel takes long time
        badGraphs.add("10.graph");
        badGraphs.add("100.graph");
        badGraphs.add("11.graph");
        badGraphs.add("13.graph"); // lot of easy edges - still k too high
        badGraphs.add("15.graph");
        badGraphs.add("16.graph");
        badGraphs.add("17.graph"); // k=266
        badGraphs.add("18.graph"); // non reducible creates a lot of edges
        badGraphs.add("19.graph"); // lot of easy edges - still k too high
        badGraphs.add("2.graph"); // k = 41
        badGraphs.add("21.graph"); // one component too big - kernel takes long time
        badGraphs.add("22.graph"); // k=79
        badGraphs.add("23.graph"); // k=59
        badGraphs.add("24.graph"); // k=11 should be doable
        badGraphs.add("25.graph"); // kernel takes long time
        badGraphs.add("26.graph"); // k=26
        badGraphs.add("28.graph"); // k=53
        badGraphs.add("29.graph"); // k=45
        badGraphs.add("3.graph"); // k=11 should be doable
        badGraphs.add("30.graph"); // k=47
        badGraphs.add("31.graph"); // k=27
        badGraphs.add("32.graph"); // easy solver takes long time - lot of easy edges, k=173 - branching takes long time
        badGraphs.add("33.graph"); // kernel takes too long
        badGraphs.add("34.graph"); // kernel takes too long one component too big.
        badGraphs.add("35.graph"); // kernel takes too long
        badGraphs.add("37.graph"); // k=47
        badGraphs.add("38.graph"); // k=47
        badGraphs.add("40.graph"); // k=51
        badGraphs.add("41.graph"); // Kernel takes too long
        badGraphs.add("42.graph"); // K=26
        badGraphs.add("44.graph"); // K=40
        badGraphs.add("46.graph"); // K=53
        badGraphs.add("47.graph"); // K=58
        badGraphs.add("50.graph"); // K=13
        badGraphs.add("51.graph"); // K=18
        badGraphs.add("52.graph"); // K=59
        badGraphs.add("56.graph"); // K=48
        badGraphs.add("57.graph"); // kernel takes too long
        badGraphs.add("59.graph"); // k=36
        badGraphs.add("62.graph"); // k=30
        badGraphs.add("63.graph"); // k=0 can be done, but takes a bit to kernel
        badGraphs.add("65.graph"); // k=78
        badGraphs.add("67.graph"); // k=40
        badGraphs.add("68.graph"); // kernel takes too long
        badGraphs.add("69.graph"); // k=67
        badGraphs.add("7.graph"); // k=265 simple solver takes long.
        badGraphs.add("70.graph"); // k=42 for a component
        badGraphs.add("71.graph"); // k=81
        badGraphs.add("72.graph"); // k=83
        badGraphs.add("75.graph"); // k=18
        badGraphs.add("76.graph"); // weird: k = 6 in a non reducible instance but takes long time to finish.
        badGraphs.add("77.graph"); // k=49
        badGraphs.add("78.graph"); // k=60
        badGraphs.add("79.graph"); // k=44
        badGraphs.add("8.graph"); // k=40
        badGraphs.add("80.graph"); // kernel takes too long.
        badGraphs.add("81.graph"); // k=79
        badGraphs.add("82.graph"); // kernel takes too long

        File folder = new File("res/instances/");
        if(folder.listFiles() != null){
            for (File fileEntry : folder.listFiles()) {
                String fileName = fileEntry.getName();
                if (!fileEntry.isDirectory() && !badGraphs.contains(fileName))
                    graphs.add(fileEntry.toString());
            }
        }


    }

    private void testMinFillGraph(String graph) throws FileNotFoundException{
        IO io = new IO(new FileInputStream(new File(graph)));

        Graph entireGraph = io.parse();

        Set<Edge> edges = Program.minFill(entireGraph);
        // check correct
        assert entireGraph.addEdges(edges).isChordal();
        // check minimality
        for (Edge edge : edges) {
            assert !entireGraph.addEdges(edges.remove(edge)).isChordal();
        }
    }


    @Test
    void testGraphs() throws FileNotFoundException
    {
        for (String graph : graphs) {
            System.out.println("Testing graph:" + graph);
            testMinFillGraph(graph);
        }
    }
}
