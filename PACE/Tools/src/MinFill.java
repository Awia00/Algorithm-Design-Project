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
                    gPrime = g.addEdge(edge);
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

    public boolean stepC(Graph g, int k, Set<Set<Integer>> piI) {
        return k >= minFill(g, k, piI);
    }

    public int minFill(Graph g, int k, Set<Set<Integer>> piI) {
        int min = Integer.MAX_VALUE;

        for (Set<Integer> omega : piI) {
            Set<Edge> nonEdges = g.inducedBy(omega).getNonEdges();
            int fill = nonEdges.size();

            // TODO Implement as separate method/function.
            Graph gOmega = g;
            for (Edge nonEdge : nonEdges) {
                gOmega = gOmega.addEdge(nonEdge);
            }

            int filled = fill;

            // TODO: Implement as described by section 5 towards the end.
            for (Graph component : g.inducedBy(g.vertices().minus(omega)).components()) {
                Set<Integer> cVertices = component.vertices();
                filled += minFill(
                        gOmega.inducedBy(cVertices.union(g.neighborhood(cVertices))),
                        k,
                        piI);
            }

            if (filled < min) {
                min = filled;
            }
        }
        return min;
    }

    public Set<Set<Integer>> generateVitalPotentialMaximalCliques(Graph g, int k) {
        throw new IllegalStateException("Not implemented");
    }

    /**
     * @param g A graph.
     * @param k Max number of edges to make g chordal.
     * @return A set of changes that, applied to g, reduces it.
     */
    public Set<Set<Edge>> branch(Graph g, int k) {
        double h = Math.sqrt(k);
        Set<Set<Edge>> changes = null; // TODO.
        Set<Integer>[] neighborhoods = g.neighborhoods();

        for (Edge nonEdge : g.getNonEdges()) {
            int u = nonEdge.from, v = nonEdge.to;

            // See the proof of Lemma 3.2.

            // X = N(u) \union N(v)
            Set<Integer> x = neighborhoods[u].intersect(neighborhoods[v]);

            // W = V(G)\{u,v} such that every vertex is nonadjacent to at least h vertices of x.
            Set<Integer> w = null; // TODO.
            for (Integer vertex : g.vertices()) {
                // for all vertices, except u and v.
                if (vertex == u || vertex == v) continue;

                // vertex is nonadjacent to at least h vertices of X.
                if (x.minus(neighborhoods[vertex]).size() >= h) {
                    w = w.add(vertex);
                }
            }

            Graph gw = g.inducedBy(w.add(u).add(v)); // G[W \union {u,v}]
            // If u and v are in same component in G[W \union {u,v}] rule 1 holds.
            if (gw.hasPath(u, v)) {
                Set<Edge> c = null; // TODO.

                // case 0: add edge between u and v.
                changes.add(c.add(nonEdge));

                // Find a shortest u,v-path in gw.
                Set<Integer> path = gw.shortestPath(u, v).minus(nonEdge.vertices());

                // case i: add edge between w_i in path and all vertices in x.
                for (int i = 0; i < path.size(); i++) {
                    c = null; // TODO.
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
                        changes.add(c);
                    }
                }
            }
        }
        return changes;
    }
}

interface Graph {
    Set<Integer> vertices();
    Set<Edge> edges();
    Set<Integer>[] neighborhoods();
    Set<Integer> neighborhood(int n);
    Set<Integer> neighborhood(Set<Integer> vertices);
    boolean isAdjacent(int a, int b);
    boolean hasPath(int a, int b);
    boolean isChordal();
    boolean isFullyConnected();
    Set<Graph> components();
    Set<Integer> minimalSeparator();
    Set<Integer> shortestPath(int from, int to);
    Graph addEdge(Edge e);
    Set<Edge> getNonEdges();
    Graph inducedBy(Set<Integer> vertices);
}

interface Set<T> extends Iterable<T> {
    boolean isEmpty();
    int size();
    Set<T> add(T element);
    Set<T> union(Set<T> other);
    Set<T> intersect(Set<T> other);
    Set<T> minus(Set<T> other);
    Set<T> remove(T element);
}

class Edge {
    public final int from, to;

    public Set<Integer> vertices() {
        return null;
    }

    Edge(int from, int to) {
        this.from = from;
        this.to = to;
    }
}