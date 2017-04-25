package minfill.graphs;

import minfill.sets.Set;
import org.jetbrains.annotations.Contract;

import java.util.List;

public interface ChordalGraph extends Graph {
    @Contract(pure = true) // Kumar, Madhavan page 10(164)
    default Set<Set<Integer>> minimalSeparators() { // todo might not work.
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

    @Contract(pure = true) // blair page 20
    default Set<Set<Integer>> maximalCliques() {
        List<Integer> peo = maximumCardinalitySearch();
        Set<Set<Integer>> cliques = Set.empty();
        for (int i = 0; i < peo.size()-1; i++) {
            Integer v1 = peo.get(i);
            Integer v2 = peo.get(i+1);
            if(i == 0) cliques = cliques.add(neighborhood(v1).toSet().add(v1));
            if(mAdj(peo, i).size() <= mAdj(peo, i+1).size()) { // Li = vertices with labels greater than i but we already know how many we have left since we go in order
                cliques = cliques.add(neighborhood(v2).toSet().add(v2));
            }
        }
        return cliques;
    }

    @Override
    default ChordalGraph minimalTriangulation() {
        return this;
    }
}
