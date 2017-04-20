package minfill.data;

import java.util.Objects;

public class Pair<T1, T2> {
    public final T1 o1;
    public final T2 o2;

    public Pair(T1 o1, T2 o2) {
        if (o1 == null || o2 == null) throw new IllegalArgumentException();
        this.o1 = o1;
        this.o2 = o2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair<?, ?> pair = (Pair<?, ?>) o;

        return Objects.equals(o1, pair.o1) && Objects.equals(o2, pair.o2);
    }

    @Override
    public int hashCode() {
        int result = o1.hashCode();
        result = 31 * result + o2.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "(" + o1 + ", " + o2 + ")";
    }
}
