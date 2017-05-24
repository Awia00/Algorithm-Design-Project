package minfill;

import minfill.graphs.*;
import minfill.iterators.FilterIterable;
import minfill.graphs.PotentialQuasiCliqueIterable;
import minfill.sets.ImmutableSet;
import minfill.sets.Set;
import minfill.tuples.Pair;
import org.jetbrains.annotations.Contract;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.LongAdder;

public class MinFillFomin<T extends Comparable<T>> {
    public static LongAdder memoizerHits = new LongAdder();

    @Contract(pure = true)
    public Optional<Graph<T>> stepB1(Graph<T> g, int k) {
        if (k <= 0 ) {
            if(g.isChordal()) return Optional.of(g);
            else return Optional.empty();
        }

        Set<Set<Edge<T>>> branches = branch(g, k);

        if (branches.isEmpty()) {
            return stepB2(g,k);
        } else {
            for (Set<Edge<T>> branch : branches) {
                Graph<T> gPrime = g.addEdges(branch);
                int kPrime = k - branch.size();

                Optional<Graph<T>> res = stepB1(gPrime, kPrime);
                if (res.isPresent()) return res;
            }
        }

        return Optional.empty();
    }

    /**
     * @param g A graph.
     * @param k Max number of edges to make g chordal.
     * @return A set of changes that, applied to g, reduces it.
     */
    @Contract(pure = true)
    public Set<Set<Edge<T>>> branch(Graph<T> g, int k) {
        double h = Math.sqrt(k);
        java.util.Set<Set<Edge<T>>> changes = new HashSet<>();

        for (Edge<T> nonEdge : g.getNonEdges()) {
            T u = nonEdge.from, v = nonEdge.to;

            // See the proof of Lemma 3.2.

            // X = N(u) union N(v)
            Set<T> x = g.neighborhood(u).toSet().intersect(g.neighborhood(v).toSet());

            // W = V(G)\{u,v} such that every vertex is nonadjacent to at least h getVertices of x.
            Set<T> w = Set.empty();
            for (T vertex : g.getVertices().minus(x)) {
                // for all getVertices, except u and v.
                if (vertex == u || vertex == v) continue;

                // vertex is nonadjacent to at least h getVertices of X.
                if (x.minus(g.neighborhood(vertex).toSet()).size() >= h) {
                    w = w.add(vertex);
                }
            }

            Graph<T> gw = g.inducedBy(w.union(Set.of(u, v))); // G[W \\union {u,v}]
            // If u and v are in same component in G[W \\union {u,v}] rule 1 holds.
            if (gw.hasPath(u, v)) {
                assert !gw.isAdjacent(u,v);

                // Find a shortest u,v-path in gw.
                Set<T> path = Set.of(gw.shortestPath(u, v)).minus(nonEdge.vertices());

                // case i: add edge between w_i in path and all getVertices in x.
                for (T wi : path) {
                    Set<Edge<T>> c = Set.empty();
                    for (T vertex : x) {
                        // If x and vertex are distinct non-adjacent getVertices, add edges to change set.
                        if (vertex != wi && !g.isAdjacent(wi, vertex)) {
                            c = c.add(new Edge<>(wi, vertex));
                        }
                    }

                    // If number of added edges is greater than k, then we cannot use this subgraph.
                    if (!c.isEmpty() && c.size() <= k) {
                        // Case i done, add to branch-list.
                        changes.add(c);
                    }
                }

                // case 0: add edge between u and v.
                changes.add(Set.of(nonEdge));
                break;
            }
        }
        return Set.of(changes);
    }

    @Contract(pure = true)
    public Optional<Graph<T>> stepB2(Graph<T> g, int k) {
        IO.printf("Step B2: Non-reducible instance found. k=%d\n", k);
        Graph<T> gPrime = g;

        // reduction
        MinFillPolynomialReducer<T> minFillPolynomialReducer = new MinFillPolynomialReducer<>();
        Set<T> removableIntegers = minFillPolynomialReducer.findRemovableVertices(gPrime);
        if(removableIntegers.size()!=0){
            gPrime = gPrime.inducedBy(gPrime.getVertices().minus(removableIntegers));
            IO.printf("Removed %d getVertices\n", removableIntegers.size());
        }

        // shortcuts
        Set<Set<T>> piI;
        int maxSubsetSize = (int)(5*Math.sqrt(k)+5); // 5 is magic value, theoretically should be 2 or 3
        if(maxSubsetSize > gPrime.getVertices().size()) {
            IO.println("Shortcut for vital potential maximum clique taken");
            piI = exhaustiveVitalPotentialMaximalCliqueSearch(gPrime, k);
        }
        else if(k < 25) {
            IO.println("Shortcut 'search tree' taken");
            return MinFillSearchTree.minFillSearchTree(g, k);
        }
        else // Straight up Fomin
            piI = generateVitalPotentialMaximalCliques(gPrime, k);

        return stepC(g, k, piI);
    }

    @Contract(pure = true)
    public Set<Set<T>> exhaustiveVitalPotentialMaximalCliqueSearch(Graph<T> g, int k) {
        java.util.Set<Set<T>> potentialMaximalCliques = new HashSet<>();

        for (Set<T> vertices : Set.subsetsOfSizeAtMost(g.getVertices(), g.getVertices().size())) {
            if (g.isVitalPotentialMaximalClique(vertices, k)) {
                potentialMaximalCliques.add(vertices);
            }
        }
        return Set.of(potentialMaximalCliques);
    }

    @Contract(pure = true)
    public Set<Set<T>> generateVitalPotentialMaximalCliques(Graph<T> g, int k) {
        IO.println("Generating vital potential maximal cliques");
        java.util.Set<Set<T>> vitalPotentialMaximalCliques = new HashSet<>();

        // all vertex subsets of size at most 5*sqrt(k)+2 (step 2)
        for (Set<T> vertices : Set.subsetsOfSizeAtMost(g.getVertices(), (int) (5 * Math.sqrt(k) + 2))) {
            if (g.isVitalPotentialMaximalClique(vertices, k)) {
                vitalPotentialMaximalCliques.add(vertices);
            }
        }
        IO.println("step B2: case 2 done: " + vitalPotentialMaximalCliques.size());

        // enumerate quasi-cliques. (Step 1)
        Iterable<Set<T>> vitalQuasiCliques = new FilterIterable<>(
                new PotentialQuasiCliqueIterable<>(g, k),
                t -> !vitalPotentialMaximalCliques.contains(t) && g.isVitalPotentialMaximalClique(t, k)
        );
        for (Set<T> vitalPotentialMaxClique : vitalQuasiCliques) {
            vitalPotentialMaximalCliques.add(vitalPotentialMaxClique);
        }

        IO.println("step B2: case 1 done: " + vitalPotentialMaximalCliques.size());

        // step 3 of generating vital potential maximal cliques
        for (T vertex : g.getVertices()) {
            Set<Edge<T>> fill = g.cliqueify(g.neighborhood(vertex).toSet());
            if(!fill.isEmpty()) {
                Graph<T> h = g.addEdges(fill);
                vitalQuasiCliques = new FilterIterable<>(
                        new PotentialQuasiCliqueIterable<>(h, k),
                        t -> !vitalPotentialMaximalCliques.contains(t) && g.isVitalPotentialMaximalClique(t, k)
                );
                for (Set<T> vitalPotentialMaxClique : vitalQuasiCliques) {
                    vitalPotentialMaximalCliques.add(vitalPotentialMaxClique);
                }
            }
        }
        IO.println("step B2: case 3 done: " + vitalPotentialMaximalCliques.size());
        return Set.of(vitalPotentialMaximalCliques);
    }

    // Implementation of Lemma 4.1
    @Contract(pure = true)
    private java.util.Set<Set<T>> enumerateQuasiCliques(Graph<T> g, int k) {
        java.util.Set<Set<T>> potentialMaximalCliques = new HashSet<>();
        Iterable<Set<T>> vertexSubsets = Set.subsetsOfSizeAtMost(g.getVertices(), (int)(5*Math.sqrt(k)));

        for (Set<T> z : vertexSubsets) {
            Set<T> gMinusZ = g.getVertices().minus(z);
            ChordalGraph<T> h = g.inducedBy(gMinusZ).minimalTriangulation();

            // Case 1
            for (Set<T> s : h.minimalSeparators()) {
                if(g.isClique(s)){
                    Set<T> c = s.union(z);
                    if (!potentialMaximalCliques.contains(c) && g.isPotentialMaximalClique(c)) {
                        potentialMaximalCliques.add(c);
                    }
                }
            }

            for (Set<T> maximalClique : h.maximalCliques()) {
                // Case 2
                if (g.isClique(maximalClique)) {
                    Set<T> c = maximalClique.union(z);
                    if (!potentialMaximalCliques.contains(c) && g.isPotentialMaximalClique(c)) {
                        potentialMaximalCliques.add(c);
                    }
                }

                // Case 3
                Graph<T> gMinusKUnionZ = g.inducedBy(g.getVertices().minus(maximalClique.union(z)));
                for (T y : z) {
                    Set<T> Y = Set.of(y);
                    for (Set<T> bi : gMinusKUnionZ.components()) {
                        if (g.neighborhood(bi).contains(y)) {
                            Y = Y.union(bi);
                        }
                    }
                    Set<T> c = g.neighborhood(Y).add(y);
                    if (!potentialMaximalCliques.contains(c) && g.isPotentialMaximalClique(c)) {
                        potentialMaximalCliques.add(c);
                    }
                }
            }
        }
        //IO.println("quasi subsets done");
        return potentialMaximalCliques;
    }

    @Contract(pure = true)
    public Optional<Graph<T>> stepC(Graph<T> g, int k, Set<Set<T>> piI) {
        IO.println("Step C: All ("+piI.size()+") vital potential maximal cliques found.");
        Map<Pair, Set<Set<T>>> piSC = generatePiSC(g, piI);
        Map<Graph<T>, Set<Edge<T>>> memoizer = new HashMap<>();
        for (Set<T> omega : piI) {
            Set<Edge<T>> fill = g.cliqueify(omega);
            Graph<T> filled = g.addEdges(fill);
            for (Set<T> c : g.inducedBy(g.getVertices().minus(omega)).components()) {
                Set<T> neighborhoodC = g.neighborhood(c);
                fill = fill.union(minFillF(filled.inducedBy(c.union(neighborhoodC)), new Pair<>(neighborhoodC, c), piSC, memoizer));
            }
            if(fill.size()<=k) return Optional.of(g.addEdges(fill));
        }
        return Optional.empty();
    }

    @Contract(pure = true)
    public Map<Pair, Set<Set<T>>> generatePiSC(Graph<T> g, Set<Set<T>> piI)
    {
        Map<Pair, Set<Set<T>>> piSC = new HashMap<>();
        for (Set<T> omega : piI) {
            Graph<T> gMinusOmega = g.inducedBy(g.getVertices().minus(omega));
            Set<Set<T>> components = gMinusOmega.components();
            for (Set<T> component : components) {
                Set<T> s = g.neighborhood(component);
                for (Set<T> c : g.fullComponents(s)) {
                    if(s.isProperSubsetOf(omega) && omega.isSubsetOf(s.union(c)))
                    {
                        Pair pair = new Pair<>(s, c);
                        if(!piSC.containsKey(pair)) piSC.put(pair, new ImmutableSet<>(omega));
                        else piSC.put(pair, piSC.get(pair).add(omega));
                    }
                }
            }
        }
        return piSC;
    }

    public Set<Edge<T>> minFillF(Graph<T> f, Pair sc, Map<Pair, Set<Set<T>>> piSC, Map<Graph<T>, Set<Edge<T>>> memoizer){
        Set<Edge<T>> memoizedResult = memoizer.get(f);
        if(memoizedResult != null) {
            memoizerHits.increment();
            return memoizedResult;
        }

        Set<Edge<T>> result = f.getNonEdges();
        if(!piSC.containsKey(sc)){
            IO.println("SC not found in piSC");
            return result;
        }
        for (Set<T> omegaPrime : piSC.get(sc)) {
            Set<Edge<T>> fill = f.cliqueify(omegaPrime);
            Graph<T> filled = f.addEdges(fill);

            for (Set<T> cPrime : f.inducedBy(f.getVertices().minus(omegaPrime)).components()) {
                if (fill.size() >= result.size()) break;
                Set<T> neighborhoodCPrime = f.neighborhood(cPrime);
                fill = fill.union(minFillF(filled.inducedBy(cPrime.union(neighborhoodCPrime)), new Pair<>(neighborhoodCPrime, cPrime), piSC, memoizer));
            }
            if (fill.size() < result.size()) result = fill;
        }

        memoizer.put(f, result);
        return result;
    }
}

