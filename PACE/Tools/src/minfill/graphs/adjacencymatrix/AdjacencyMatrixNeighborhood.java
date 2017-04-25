package minfill.graphs.adjacencymatrix;

import minfill.graphs.Neighborhood;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

class AdjacencyMatrixNeighborhood implements Neighborhood {
    private final Integer[] mapToExternal;
    private final FilteredMap<Integer> mapToInternal;
    private final boolean[] neighborhood;

    public AdjacencyMatrixNeighborhood(Integer[] mapToExternal, FilteredMap<Integer> mapToInternal, boolean[] neighborhood) {
        this.mapToExternal = mapToExternal;
        this.mapToInternal = mapToInternal;
        this.neighborhood = neighborhood;
    }

    @Override
    public boolean contains(Integer vertex) {
        return neighborhood[mapToInternal.get(vertex)];
    }

    @NotNull
    @Override
    public Iterator<Integer> iterator() {
        return new AdjacencyMatrixIterator(neighborhood, mapToExternal, mapToInternal);
    }
}

