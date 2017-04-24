package minfill.data;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SetTest {
    @Test
    public void ofWhenEmptyTest() {
        assertEquals(EmptySet.instance(), Set.of());
        assertEquals(EmptySet.instance(), Set.of(new ArrayList<>()));
    }
}