package minfill.data;

public class Edge {
    public final int from, to;

    public Set<Integer> vertices() {
        return Set.of(from, to);
    }

    public Edge(int from, int to) {
        this.from = from;
        this.to = to;
    }
}
