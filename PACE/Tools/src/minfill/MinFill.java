package minfill;

import minfill.data.*;
import org.jetbrains.annotations.Contract;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MinFill {
    @Contract(pure = true)
    public Optional<Graph> stepB1(Graph g, int k) {
        if (g.isChordal()) return Optional.of(g);
        if (k <= 0) return Optional.empty();

        Set<Set<Edge>> branches = branch(g, k);

        if (branches.isEmpty()) {
            return stepB2(g,k);
        } else {
            for (Set<Edge> branch : branches) {
                Graph gPrime = g;
                for (Edge edge : branch) {
                    gPrime = gPrime.addEdge(edge);
                }

                int kPrime = k - branch.size();

                Optional<Graph> res = stepB1(gPrime, kPrime);
                if (res.isPresent()) return res;
            }
        }

        return Optional.empty();
    }

    @Contract(pure = true)
    public Optional<Graph> stepB2(Graph g, int k) {
        Set<Set<Integer>> piI = generateVitalPotentialMaximalCliques(g, k);

        return stepC(g, k, piI);
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
        if(memoizedResult != null) return memoizedResult;

        Set<Edge> result = f.getNonEdges();
        for (Set<Integer> omegaPrime : piSC.get(sc)) {
            Graph filled = f.cliqueify(omegaPrime);
            Set<Edge> fill = f.inducedBy(omegaPrime).getNonEdges();

            for (Set<Integer> cPrime : f.inducedBy(f.vertices().minus(omegaPrime)).components()) {
                Set<Integer> neighborhoodCPrime = f.neighborhood(cPrime);
                fill = fill.union(minFillF(filled.inducedBy(cPrime.union(neighborhoodCPrime)), new Pair<>(neighborhoodCPrime, cPrime), piSC, memoizer));
                if (fill.size() >= result.size()) break;
            }
            if (fill.size() < result.size()) result = fill;
        }

        memoizer.put(f, result);
        return result;
    }

    @Contract(pure = true)
    public Optional<Graph> stepC(Graph g, int k, Set<Set<Integer>> piI) {
        Map<Pair, Set<Set<Integer>>> piSC = generatePiSC(g, piI);
        Map<Graph, Set<Edge>> memoizer = new HashMap<>();
        for (Set<Integer> omega : piI) {
            Set<Edge> fill = g.inducedBy(omega).getNonEdges();
            for (Set<Integer> c : g.inducedBy(g.vertices().minus(omega)).components()) {
                Set<Integer> neighborhoodC = g.neighborhood(c);
                fill = fill.union(minFillF(g.cliqueify(omega).inducedBy(c.union(neighborhoodC)), new Pair<>(neighborhoodC, c), piSC, memoizer));
            }
            if(fill.size()<=k) return Optional.of(g.addEdges(fill));
        }
        return Optional.empty();
    }

    // Implementation of Lemma 4.1
    // TODO: Check that we branch correctly on component size.
    @Contract(pure = true)
    private Set<Set<Integer>> enumerateQuasiCliques(Graph g, int k) {
        Set<Set<Integer>> potentialMaximalCliques = Set.empty();
        Set<Set<Integer>> vertexSubsets = g.vertices().subsetsOfSizeAtMost((int)(5*Math.sqrt(k)));

        for (Set<Integer> z : vertexSubsets) {
            Set<Integer> gMinusZ = g.vertices().minus(z);
            Graph h = g.inducedBy(gMinusZ).minimalTriangulation();

            // Case 1
            for (Set<Integer> s : h.minimalSeparatorsOfChordalGraph()) {
                if(g.inducedBy(s).isClique()){
                    Set<Integer> c = s.union(z);
                    if (g.isVitalPotentialMaximalClique(c, k)) {
                        potentialMaximalCliques = potentialMaximalCliques.add(c);
                    }
                }
            }

            for (Set<Integer> maximalClique : h.maximalCliquesOfChordalGraph()) {
                // Case 2
                if (g.isClique(maximalClique)) {
                    Set<Integer> c = maximalClique.union(z);
                    if (g.isVitalPotentialMaximalClique(c, k)) {
                        potentialMaximalCliques = potentialMaximalCliques.add(c);
                    }
                }

                // Case 3
                for (Integer y : z) {
                    Set<Integer> Y = Set.empty();
                    for (Set<Integer> bi : g.inducedBy(g.vertices().minus(z.union(maximalClique))).components()) {
                        Y = Y.union(bi.add(y));
                    }
                    Set<Integer> c = g.neighborhood(Y).add(y);
                    if (g.isVitalPotentialMaximalClique(c, k)) {
                        potentialMaximalCliques = potentialMaximalCliques.add(c);
                    }
                }
            }
        }
        return potentialMaximalCliques;
    }

    @Contract(pure = true)
    public Set<Set<Integer>> generateVitalPotentialMaximalCliques(Graph g, int k) {
        // enumerate quasi-cliques. (Step 1)
        Set<Set<Integer>> potentialMaximalCliques = enumerateQuasiCliques(g, k);

        // all vertex subsets of size at most 5*sqrt(k)+2 (step 2)
        for (Set<Integer> vertices : g.vertices().subsetsOfSizeAtMost((int) (5 * Math.sqrt(k) + 2))) {
            if (g.isVitalPotentialMaximalClique(vertices, k)) {
                potentialMaximalCliques = potentialMaximalCliques.add(vertices);
            }
        }

        // step 3 of generating vital potential maximal cliques
        for (Integer vertex : g.vertices()) {
            Graph h = g.cliqueify(g.neighborhood(vertex));

            potentialMaximalCliques = potentialMaximalCliques.union(enumerateQuasiCliques(h, k));
        }
        return potentialMaximalCliques;
    }

    /**
     * @param g A graph.
     * @param k Max number of edges to make g chordal.
     * @return A set of changes that, applied to g, reduces it.
     */
    @Contract(pure = true)
    public Set<Set<Edge>> branch(Graph g, int k) {
        double h = Math.sqrt(k);
        Set<Set<Edge>> changes = Set.empty();
        Map<Integer, Set<Integer>> neighborhoods = g.neighborhoods();

        for (Edge nonEdge : g.getNonEdges()) {
            int u = nonEdge.from, v = nonEdge.to;

            // See the proof of Lemma 3.2.

            // X = N(u) \\union N(v)
            Set<Integer> x = neighborhoods.get(u).intersect(neighborhoods.get(v));

            // W = V(G)\{u,v} such that every vertex is nonadjacent to at least h vertices of x.
            Set<Integer> w = Set.empty();
            for (Integer vertex : g.vertices()) {
                // for all vertices, except u and v.
                if (vertex == u || vertex == v) continue;

                // vertex is nonadjacent to at least h vertices of X.
                if (x.minus(neighborhoods.get(vertex)).size() >= h) {
                    w = w.add(vertex);
                }
            }

            Graph gw = g.inducedBy(w.add(u).add(v)); // G[W \\union {u,v}]
            // If u and v are in same component in G[W \\union {u,v}] rule 1 holds.
            if (gw.hasPath(u, v)) {
                Set<Edge> c = Set.empty();

                // case 0: add edge between u and v.
                changes = changes.add(c.add(nonEdge));

                // Find a shortest u,v-path in gw.
                Set<Integer> path = Set.of(gw.shortestPath(u, v)).minus(nonEdge.vertices());

                // case i: add edge between w_i in path and all vertices in x.
                for (int i = 0; i < path.size(); i++) {
                    c = Set.empty();
                    for (Integer vertex : x) {
                        // If x and vertex are distinct non-adjacent vertices, add edges to change set.
                        if (vertex != i && !g.isAdjacent(i, vertex)) {
                            c = c.add(new Edge(i, vertex));
                        }
                    }

                    // If number of added edges is greater than k, then we cannot use this subgraph.
                    // TODO: Check if this is sound.
                    if (!c.isEmpty() && c.size() <= k) {
                        // Case i done, add to branch-list.
                        changes = changes.add(c);
                    }
                }
            }
        }
        return changes;
    }
}

