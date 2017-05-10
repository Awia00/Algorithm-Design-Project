package minfill.graphs;

import minfill.sets.Set;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Edge implements Comparable<Edge>{
    public final Integer from, to;
    private Set<Integer> vertices;

    public Set<Integer> vertices()
    {
        if (vertices == null) vertices = Set.of(from, to);
        return vertices;
    }

    public Edge(Integer from, Integer to) {
        if(from < to){
            this.from = from;
            this.to = to;
        }else{
            this.to = from;
            this.from = to;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        return (Objects.equals(from, edge.from) && Objects.equals(to, edge.to));
    }

    @Override
    public int hashCode() {
        return 31 * from + to;
    }

    @Override
    public int compareTo(@NotNull Edge o) {
        int result = from.compareTo(o.from);
        if(result == 0){
            return to.compareTo(o.to);
        }
        return result;
    }
}
