package minfill.graphs;

import minfill.sets.Set;

import java.util.HashSet;

public interface Neighborhood extends Iterable<Integer> {
    boolean contains(Integer vertex);

    default Set<Integer> toSet() {
        java.util.Set<Integer> neighbors = new HashSet<>();
        for (Integer vertex : this) {
            neighbors.add(vertex);
        }
        return Set.of(neighbors);
    }
}
