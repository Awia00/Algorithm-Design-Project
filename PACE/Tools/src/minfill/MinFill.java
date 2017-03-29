package minfill;

import minfill.data.Edge;
import minfill.data.Graph;
import minfill.data.Set;

import java.util.HashMap;
import java.util.Map;

public class MinFill {
    public boolean stepB1(Graph g, int k) {
        if (k < 0) return false;
        if (g.isChordal()) return true;

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

                if (stepB1(gPrime, kPrime)) return true;
            }
        }

        return false;
    }

    public boolean stepB2(Graph g, int k) {
        Set<Set<Integer>> piI = generateVitalPotentialMaximalCliques(g, k);

        return stepC(g, k, piI);
    }

    public Set<Set<Integer>> generatePiSC(Graph g, Set<Set<Integer>> piI)
    {
        Set<Set<Integer>> piSC = Set.empty();
        for (Set<Integer> omega : piI) {
            Graph gMinusOmega = g.inducedBy(g.vertices().minus(omega));
            Set<Set<Integer>> components = gMinusOmega.components();
            for (Set<Integer> component : components) {
                Set<Integer> s = g.neighborhood(component);
                Graph gMinusS = g.inducedBy(g.vertices().minus(s));
                for (Set<Integer> c : gMinusS.fullComponents()) {
                    if(s.isProperSubsetOf(omega) && omega.isSubsetOf(s.union(c)))
                    {
                        piSC = piSC.add(omega);
                    }
                }
            }
        }
        return piSC;
    }

    public int minFillF(Graph f, Set<Set<Integer>> piSC, Map<Graph, Integer> memoizer){
        Integer memoizedResult = memoizer.get(f);
        if(memoizedResult != null) return memoizedResult;

        int result = 0;
        for (Set<Integer> omegaPrime : piSC) {
            int fill = f.inducedBy(omegaPrime).getNonEdges().size();
            for (Set<Integer> cPrime : f.inducedBy(f.vertices().minus(omegaPrime)).components()) {
                fill += minFillF(f.cliqueify(omegaPrime).inducedBy(cPrime.union(f.neighborhood(cPrime))), piSC, memoizer);
                if (fill >= result) break;
            }
            result = Math.min(result, fill);
        }

        memoizer.put(f, result);
        return result;
    }

    public boolean stepC(Graph g, int k, Set<Set<Integer>> piI) {
        Set<Set<Integer>> piSC = generatePiSC(g, piI);
        Map<Graph, Integer> memoizer = new HashMap<>();
        for (Set<Integer> omega : piI) {
            int fill = g.inducedBy(omega).getNonEdges().size();
            for (Set<Integer> c : g.inducedBy(g.vertices().minus(omega)).components()) {
                fill += minFillF(g.cliqueify(omega).inducedBy(c.union(g.neighborhood(c))), piSC, memoizer);
            }
            if(fill<=k) return true;
        }
        return false;
    }

    // Implementation of Lemma 4.1
    // TODO: Check that we branch correctly on component size.
    private Set<Set<Integer>> enumerateQuasiCliques(Graph g, int k) {
        Set<Set<Integer>> potentialMaximalCliques = Set.empty();
        Set<Set<Integer>> vertexSubsets = g.vertices().subsetsOfSizeAtMost((int)(5*Math.sqrt(k)));

        for (Set<Integer> z : vertexSubsets) {
            Set<Integer> gMinusZ = g.vertices().minus(z);
            Graph h = g.inducedBy(gMinusZ).minimalTriangulation();

            // Case 1
            for (Set<Integer> s : h.minimalSeparators()) {
                if(g.inducedBy(s).isClique()){
                    Set<Integer> c = s.union(z);
                    if (g.isPotentialMaximalClique(c)) {
                        // TODO: Maybe check vitality
                        potentialMaximalCliques = potentialMaximalCliques.add(c);
                    }
                }
            }

            for (Set<Integer> maximalClique : h.maximalCliquesOfChordalGraph()) {
                // Case 2
                if (g.isClique(maximalClique)) {
                    Set<Integer> c = maximalClique.union(z);
                    if (g.isPotentialMaximalClique(c)) {
                        // TODO: maybe check vitality
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
                    if (g.isPotentialMaximalClique(c)) {
                        // TODO: maybe check vitality
                        potentialMaximalCliques = potentialMaximalCliques.add(c);
                    }
                }
            }
        }
        // TODO: Maybe first check vitality here? (bad use of memory?)
        return potentialMaximalCliques;
    }

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
                Set<Integer> path = gw.shortestPath(u, v).minus(nonEdge.vertices());

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
                    if (c.size() <= k) {
                        // Case i done, add to branch-list.
                        changes = changes.add(c);
                    }
                }
            }
        }
        return changes;
    }
}

