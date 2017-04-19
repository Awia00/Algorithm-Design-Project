package minfill.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Created by aws on 19-04-2017.
 */
public class VertexPairIterator<T extends Comparable<T>> implements Iterator<Pair<T,T>> {
    private List<T> vertices;
    private int outerIndex = 0, innerIndex = 1;

    public VertexPairIterator(Set<T> vertices) {
        this.vertices = new ArrayList<>();
        for (T vertex : vertices) {
            this.vertices.add(vertex);
        }
    }

    @Override
    public boolean hasNext() {
        return outerIndex < vertices.size() && innerIndex < vertices.size();
    }

    @Override
    public Pair<T,T> next() {
        Pair<T,T> element = new Pair<>(vertices.get(outerIndex), vertices.get(innerIndex));
        innerIndex = (innerIndex+1)%vertices.size();
        if(innerIndex==0)
        {
            outerIndex++;
            innerIndex = outerIndex+1;
        }
        return element;
    }


    public static void main(String[] args){
        VertexPairIterator<Integer> iterator = new VertexPairIterator<Integer>(Set.empty());
        assert(!iterator.hasNext());

        iterator = new VertexPairIterator<>(Set.of(1));
        assert(!iterator.hasNext());

        iterator = new VertexPairIterator<>(Set.of(1,2));
        Pair<Integer,Integer> pair = iterator.next();
        assert (Objects.equals(pair,new Pair<>(1, 2)));
        assert (!iterator.hasNext());

        iterator = new VertexPairIterator<>(Set.of(1,2,3,4));
        pair = iterator.next();
        assert (Objects.equals(pair, new Pair<>(1, 2)));
        pair = iterator.next();
        assert (Objects.equals(pair, new Pair<>(1, 3)));
        pair = iterator.next();
        assert (Objects.equals(pair, new Pair<>(1, 4)));
        pair = iterator.next();
        assert (Objects.equals(pair, new Pair<>(2, 3)));
        pair = iterator.next();
        assert (Objects.equals(pair, new Pair<>(2, 4)));
        pair = iterator.next();
        assert (Objects.equals(pair, new Pair<>(3, 4)));
        assert (!iterator.hasNext());
    }
}
