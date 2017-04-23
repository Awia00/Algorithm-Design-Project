package minfill.data;

import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.Map;

public class ImmutableChordalGraph extends ImmutableGraph implements ChordalGraph {
    protected ImmutableChordalGraph(Set<Integer> vertices, Map<Integer, Set<Integer>> neighborhoods) {
        super(vertices, neighborhoods);
        assert isChordal();
    }

    @Override
    @Contract(pure = true) // Kumar, Madhavan page 10(164)
    public Set<Set<Integer>> minimalSeparators() { // todo might not work.
        List<Integer> peo = maximumCardinalitySearch();
        Set<Set<Integer>> separators = Set.empty();
        for (int i = 0; i < peo.size()-1; i++) {
            Set<Integer> separator = mAdj(peo, i);
            if(separator.size() <= mAdj(peo, i+1).size()){
                separators = separators.add(separator);
            }
        }
        return separators;
    }

    @Override
    @Contract(pure = true) // blair page 20
    public Set<Set<Integer>> maximalCliques() {
        List<Integer> peo = maximumCardinalitySearch();
        Set<Set<Integer>> cliques = Set.empty();
        for (int i = 0; i < peo.size()-1; i++) {
            Integer v1 = peo.get(i);
            Integer v2 = peo.get(i+1);
            if(i == 0) cliques = cliques.add(neighborhood(v1).add(v1));
            if(mAdj(peo, i).size() <= mAdj(peo, i+1).size()) { // Li = vertices with labels greater than i but we already know how many we have left since we go in order
                cliques = cliques.add(neighborhood(v2).add(v2));
            }
        }
        return cliques;
    }

    @Override
    public ChordalGraph minimalTriangulation() {
        return this;
    }
}
