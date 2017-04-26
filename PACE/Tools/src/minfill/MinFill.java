package minfill;

import minfill.graphs.ChordalGraph;
import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.sets.ImmutableSet;
import minfill.tuples.Pair;
import minfill.sets.Set;
import org.jetbrains.annotations.Contract;

import java.util.*;
import java.util.concurrent.atomic.LongAdder;

public class MinFill {
    public static LongAdder memoizerHits = new LongAdder();

    @Contract(pure = true)
    public Optional<Graph> stepB1(Graph g, int k) {
        if (g.isChordal()) return Optional.of(g);
        if (k <= 0) return Optional.empty();

        Set<Set<Edge>> branches = branch(g, k);

        if (branches.isEmpty()) {
            return stepB2(g,k);
        } else {
            for (Set<Edge> branch : branches) {
                Graph gPrime = g.addEdges(branch);
                int kPrime = k - branch.size();

                Optional<Graph> res = stepB1(gPrime, kPrime);
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
    public Set<Set<Edge>> branch(Graph g, int k) {
        double h = Math.sqrt(k);
        java.util.Set<Set<Edge>> changes = new HashSet<>();

        for (Edge nonEdge : g.getNonEdges()) {
            int u = nonEdge.from, v = nonEdge.to;

            // See the proof of Lemma 3.2.

            // X = N(u) \\union N(v)
            Set<Integer> x = g.neighborhood(u).toSet().intersect(g.neighborhood(v).toSet());

            // W = V(G)\{u,v} such that every vertex is nonadjacent to at least h vertices of x.
            Set<Integer> w = Set.empty();
            for (Integer vertex : g.vertices().minus(x)) {
                // for all vertices, except u and v.
                if (vertex == u || vertex == v) continue;

                // vertex is nonadjacent to at least h vertices of X.
                if (x.minus(g.neighborhood(vertex).toSet()).size() >= h) {
                    w = w.add(vertex);
                }
            }

            Graph gw = g.inducedBy(w.union(Set.of(u, v))); // G[W \\union {u,v}]
            // If u and v are in same component in G[W \\union {u,v}] rule 1 holds.
            if (gw.hasPath(u, v)) {
                Set<Edge> c = Set.empty();

                // case 0: add edge between u and v.
                changes.add(c.add(nonEdge));

                // Find a shortest u,v-path in gw.
                Set<Integer> path = Set.of(gw.shortestPath(u, v)).minus(nonEdge.vertices());

                // case i: add edge between w_i in path and all vertices in x.
                for (int wi : path) {
                    c = Set.empty();
                    for (Integer vertex : x) {
                        // If x and vertex are distinct non-adjacent vertices, add edges to change set.
                        if (vertex != wi && !g.isAdjacent(wi, vertex)) {
                            c = c.add(new Edge(wi, vertex));
                        }
                    }

                    // If number of added edges is greater than k, then we cannot use this subgraph.
                    if (!c.isEmpty() && c.size() <= k) {
                        // Case i done, add to branch-list.
                        changes.add(c);
                    }
                }
            }
        }
        return Set.of(changes);
    }

    @Contract(pure = true)
    public Optional<Graph> stepB2(Graph g, int k) {
        System.err.printf("Step B2: Non-reducible instance found. k=%d\n", k);
        int subsetMaxSize = (int)(5*Math.sqrt(k)+2);
        Set<Set<Integer>> piI;
        if(subsetMaxSize > g.vertices().size())
            piI = generateVitalPotentialMaximalCliquesLowK(g, g.vertices().size());
        else
            piI = generateVitalPotentialMaximalCliques(g, k);

        return stepC(g, k, piI);
    }

    @Contract(pure = true)
    public Set<Set<Integer>> generateVitalPotentialMaximalCliquesLowK(Graph g, int k) {
        java.util.Set<Set<Integer>> potentialMaximalCliques = new HashSet<>();

        for (Set<Integer> vertices : Set.subsetsOfSizeAtMost(g.vertices(), k)) {
            if (g.isVitalPotentialMaximalClique(vertices, k)) {
                potentialMaximalCliques.add(vertices);
            }
        }
        return Set.of(potentialMaximalCliques);
    }

    @Contract(pure = true)
    public Set<Set<Integer>> generateVitalPotentialMaximalCliques(Graph g, int k) {
        java.util.Set<Set<Integer>> potentialMaximalCliques = new HashSet<>();

        // enumerate quasi-cliques. (Step 1)
        potentialMaximalCliques.addAll(enumerateQuasiCliques(g, k));
        System.err.println("step B2: case 1 done: " + potentialMaximalCliques.size());

        // all vertex subsets of size at most 5*sqrt(k)+2 (step 2)
        for (Set<Integer> vertices : Set.subsetsOfSizeAtMost(g.vertices(), (int) (5 * Math.sqrt(k) + 2))) {
            if (g.isVitalPotentialMaximalClique(vertices, k)) {
                potentialMaximalCliques.add(vertices);
            }
        }
        System.err.println("step B2: case 2 done: " + potentialMaximalCliques.size());

        // step 3 of generating vital potential maximal cliques
        for (Integer vertex : g.vertices()) {
            Set<Edge> fill = g.cliqueify(g.neighborhood(vertex).toSet());
            if(!fill.isEmpty()) {
                Graph h = g.addEdges(fill);
                potentialMaximalCliques.addAll(enumerateQuasiCliques(h, k));
            }
        }
        System.err.println("step B2: case 3 done: " + potentialMaximalCliques.size());
        return Set.of(potentialMaximalCliques);
    }

    // Implementation of Lemma 4.1
    // TODO: Check that we branch correctly on component size.
    @Contract(pure = true)
    private java.util.Set<Set<Integer>> enumerateQuasiCliques(Graph g, int k) {
        java.util.Set<Set<Integer>> potentialMaximalCliques = new HashSet<>();
        Iterable<Set<Integer>> vertexSubsets = Set.subsetsOfSizeAtMost(g.vertices(), (int)(5*Math.sqrt(k)));

        for (Set<Integer> z : vertexSubsets) {
            Set<Integer> gMinusZ = g.vertices().minus(z);
            ChordalGraph h = g.inducedBy(gMinusZ).minimalTriangulation();

            // Case 1
            for (Set<Integer> s : h.minimalSeparators()) {
                if(g.isClique(s)){
                    Set<Integer> c = s.union(z);
                    if (!potentialMaximalCliques.contains(c) && g.isVitalPotentialMaximalClique(c, k)) {
                        potentialMaximalCliques.add(c);
                    }
                }
            }

            for (Set<Integer> maximalClique : h.maximalCliques()) {
                // Case 2
                if (g.isClique(maximalClique)) {
                    Set<Integer> c = maximalClique.union(z);
                    if (!potentialMaximalCliques.contains(c) && g.isVitalPotentialMaximalClique(c, k)) {
                        potentialMaximalCliques.add(c);
                    }
                }

                // Case 3
                Graph gMinusKUnionZ = g.inducedBy(g.vertices().minus(maximalClique.union(z)));
                for (Integer y : z) {
                    Set<Integer> Y = Set.of(y);
                    for (Set<Integer> bi : gMinusKUnionZ.components()) {
                        if (g.neighborhood(bi).contains(y)) {
                            Y = Y.union(bi);
                        }
                    }
                    Set<Integer> c = g.neighborhood(Y).add(y);
                    if (!potentialMaximalCliques.contains(c) && g.isVitalPotentialMaximalClique(c, k)) {
                        potentialMaximalCliques.add(c);
                    }
                }
            }
        }
        //System.err.println("quasi subsets done");
        return potentialMaximalCliques;
    }

    @Contract(pure = true)
    public Optional<Graph> stepC(Graph g, int k, Set<Set<Integer>> piI) {
        System.err.println("Step C: All vital potential maximal cliques found.");
        Map<Pair, Set<Set<Integer>>> piSC = generatePiSC(g, piI);
        Map<Graph, Set<Edge>> memoizer = new HashMap<>();
        for (Set<Integer> omega : piI) {
            Set<Edge> fill = g.cliqueify(omega);
            Graph filled = g.addEdges(fill);
            for (Set<Integer> c : g.inducedBy(g.vertices().minus(omega)).components()) {
                Set<Integer> neighborhoodC = g.neighborhood(c);
                fill = fill.union(minFillF(filled.inducedBy(c.union(neighborhoodC)), new Pair<>(neighborhoodC, c), piSC, memoizer));
            }
            if(fill.size()<=k) return Optional.of(g.addEdges(fill));
        }
        return Optional.empty();
    }

    @Contract(pure = true)
    public Map<Pair, Set<Set<Integer>>> generatePiSC(Graph g, Set<Set<Integer>> piI)
    {
        Map<Pair, Set<Set<Integer>>> piSC = new HashMap<>();
        for (Set<Integer> omega : piI) {
            Graph gMinusOmega = g.inducedBy(g.vertices().minus(omega));
            Set<Set<Integer>> components = gMinusOmega.components();
            for (Set<Integer> component : components) {
                Set<Integer> s = g.neighborhood(component);
                for (Set<Integer> c : g.fullComponents(s)) {
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

    public Set<Edge> minFillF(Graph f, Pair sc, Map<Pair, Set<Set<Integer>>> piSC, Map<Graph, Set<Edge>> memoizer){
        Set<Edge> memoizedResult = memoizer.get(f);
        if(memoizedResult != null) {
            memoizerHits.increment();
            return memoizedResult;
        }

        Set<Edge> result = f.getNonEdges();
        if(!piSC.containsKey(sc))
            System.err.println("SC not found in piSC");
        for (Set<Integer> omegaPrime : piSC.get(sc)) {
            Set<Edge> fill = f.cliqueify(omegaPrime);
            Graph filled = f.addEdges(fill);

            for (Set<Integer> cPrime : f.inducedBy(f.vertices().minus(omegaPrime)).components()) {
                if (fill.size() >= result.size()) break;
                Set<Integer> neighborhoodCPrime = f.neighborhood(cPrime);
                fill = fill.union(minFillF(filled.inducedBy(cPrime.union(neighborhoodCPrime)), new Pair<>(neighborhoodCPrime, cPrime), piSC, memoizer));
            }
            if (fill.size() < result.size()) result = fill;
        }

        memoizer.put(f, result);
        return result;
    }
}

