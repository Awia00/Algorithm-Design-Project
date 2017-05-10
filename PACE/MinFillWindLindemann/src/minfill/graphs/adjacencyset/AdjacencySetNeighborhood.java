package minfill.graphs.adjacencyset;

import minfill.graphs.Neighborhood;
import minfill.sets.Set;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class AdjacencySetNeighborhood implements Neighborhood {
    private final Set<Integer> neighborhood;

    public AdjacencySetNeighborhood(Set<Integer> neighborhood) {
        this.neighborhood = neighborhood;
    }

    @Override
    public boolean contains(Integer vertex) {
        return neighborhood.contains(vertex);
    }

    @Override
    public Set<Integer> toSet() {
        return neighborhood;
    }

    @NotNull
    @Override
    public Iterator<Integer> iterator() {
        return neighborhood.iterator();
    }
}
