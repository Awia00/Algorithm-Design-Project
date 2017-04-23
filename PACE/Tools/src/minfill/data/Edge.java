package minfill.data;

import java.util.Objects;

public class Edge {
    public final Integer from, to;
    private Set<Integer> vertices;

    public Set<Integer> vertices()
    {
        if (vertices == null) vertices = Set.of(from, to);
        return vertices;
    }

    public Edge(Integer from, Integer to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        return (Objects.equals(from, edge.from) && Objects.equals(to, edge.to)) ||
               (Objects.equals(from, edge.to) && Objects.equals(to, edge.from));
    }

    @Override
    public int hashCode() {
        return 31 * from + to;
    }
}
