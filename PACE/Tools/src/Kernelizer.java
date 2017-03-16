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
            List<String> inputList = new ArrayList<>();
            HashMap<String, Integer> usageMap = new HashMap<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                String[] split = line.split(" ");
                inputList.add(line);
                if(usageMap.containsKey(split[0]))
                    usageMap.put(split[0], usageMap.get(split[0])+1);
                else
                    usageMap.put(split[0], 1);
                if(usageMap.containsKey(split[1]))
                    usageMap.put(split[1], usageMap.get(split[1])+1);
                else
                    usageMap.put(split[1], 1);
            }
            List<String> toRemove = new ArrayList<>();
            for (Map.Entry<String, Integer> usageEntry : usageMap.entrySet()) {
                if(usageEntry.getValue() == 1)
                    for (String s : inputList) {
                        if (s.contains(usageEntry.getKey()))
                            toRemove.add(s);
                    }
            }
            inputList.removeAll(toRemove);
            for (String s : inputList) {
                System.out.println(s);
            }
        }
    }
}
