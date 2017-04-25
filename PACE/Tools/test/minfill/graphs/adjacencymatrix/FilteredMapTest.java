package minfill.graphs.adjacencymatrix;

import minfill.sets.Set;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilteredMapTest {
    @Test
    public void testMatchingFilter() {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(1, 2);
        map.put(2, 3);
        map.put(3, 4);
        map.put(4, 5);

        FilteredMap<Integer> filtered = new FilteredMap<>(Set.of(1,2,3), Set.of(2,3,4), map);

        assertTrue(filtered.contains(1));
        assertTrue(filtered.contains(2));
        assertTrue(filtered.contains(3));

        Set keys = filtered.keySet();
        assertEquals(Set.of(1,2,3), keys);

        assertEquals(Integer.valueOf(2), filtered.get(1));
        assertEquals(Integer.valueOf(3), filtered.get(2));
        assertEquals(Integer.valueOf(4), filtered.get(3));
    }
}