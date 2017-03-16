import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class PaceToDot {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Usage: java PaceToDot <filename>");
            return;
        }

        try (Scanner scanner = new Scanner(new File(args[0]))) {
            System.out.println("Graph G {");
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                String[] split = line.split(" ");

                System.out.format("\t%s--%s\n", split[0], split[1]);
            }
            System.out.println("}");
        }
    }
}
