package minfill.graphs.adjacencymatrix;

import java.util.Iterator;

public class AdjacencyMatrixIterator implements Iterator<Integer> {
    private final boolean[] array;
    private final Integer[] mapToExternal;
    private final FilteredMap<Integer> mapToInternal;
    private int index;

    public AdjacencyMatrixIterator(boolean[] array, Integer[] mapToExternal, FilteredMap<Integer> mapToInternal) {
        this.array = array;
        this.mapToExternal = mapToExternal;
        this.mapToInternal = mapToInternal;
    }

    @Override
    public boolean hasNext() {
        if (index >= array.length) return false;

        do {
            if (array[index] && mapToInternal.contains(mapToExternal[index])) return true;
        }
        while(++index < array.length);
        return false;
    }

    @Override
    public Integer next() {
        if (hasNext()) {
            return mapToExternal[index++];
        }
        throw new IllegalStateException();
    }
}
