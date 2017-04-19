package minfill.data;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * Created by aws on 19-04-2017.
 */
public class VertexPairIterable<T extends Comparable<T>> implements Iterable<Pair<T,T>> {
    private final Set<T> vertices;

    public VertexPairIterable(Set<T> vertices) {
        this.vertices = vertices;
    }

    @NotNull
    @Override
    public Iterator<Pair<T,T>> iterator() {
        return new VertexPairIterator<>(vertices);
    }
}
