import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by aws on 16-03-2017.
 */
public class Kernelizer {

    public static void main(String[] args) throws IOException
    {
        InputStream input;
        if (args.length == 0)
            input = System.in;
        else
            input = new FileInputStream(new File(args[0]));

        try (Scanner scanner = new Scanner(input)) {
            HashMap<String, List<String>> graph = new HashMap<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                String[] split = line.split(" ");
                if(graph.containsKey(split[0]))
                    graph.get(split[0]).add(split[1]);
                else{
                    List<String> list = new ArrayList<>();
                    list.add(split[1]);
                    graph.put(split[0], list);
                }
                if(graph.containsKey(split[1]))
                    graph.get(split[1]).add(split[0]);
                else{
                    List<String> list = new ArrayList<>();
                    list.add(split[0]);
                    graph.put(split[1], list);
                }
            }
            boolean hasChanged = true;
            while(hasChanged)
            {
                hasChanged = false;
                for (Map.Entry<String, List<String>> vertexAndEdges : graph.entrySet()) {
                    if(vertexAndEdges.getValue().size()==1)
                    {
                        String other = vertexAndEdges.getValue().get(0);
                        graph.get(other).remove(vertexAndEdges.getKey());
                        vertexAndEdges.getValue().remove(other);
                        hasChanged = true;
                    }
                }
            }
            HashSet<String> outputted = new HashSet<>();
            for (Map.Entry<String, List<String>> vertexAndEdges : graph.entrySet()) {
                for (String s : vertexAndEdges.getValue()) {
                    if(!outputted.contains(s)){
                        System.out.println(vertexAndEdges.getKey() + " " + s);
                        outputted.add(vertexAndEdges.getKey());
                    }
                }
            }
        }
    }
}
