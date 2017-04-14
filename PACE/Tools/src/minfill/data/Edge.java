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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        if (from == edge.from && to == edge.to) return true;
        if (from == edge.to && to == edge.from) return true;

        return false;
    }

    @Override
    public int hashCode() {
        int result = from;
        result = 31 * result + to;
        return result;
    }
}
