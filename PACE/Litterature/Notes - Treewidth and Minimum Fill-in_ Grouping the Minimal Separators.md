Notation:

$G(V,E)$. Shorthand: $G$.

$V(G)$ is the vertex set of $G$. Shorthand $V$ when the graph from which the vertex set is taken is obvious.

$E(G)$ is the edge set of $G$. Shorthand $E$ when the graph from which the edge set is taken is obvious.



# Clique

A clique is a complete subgraph of $G$.

A maximal clique in $G$, is a complete subgraph, where the addition of an additional vertex from $G$ would render the subgraph incomplete.



# Minimal Separator

Given to nonadjacent vertices $a,b\in V(G)$ a vertex set $S \subseteq V$ is an $a,b$-separator if the removal of $S$ from $G$ separates $a$ and $b$ in different components.

$S$ is a minimal $a,b$-separator if no proper subset of $S$ separates $a$ and $b$.

$S$ is a minimal separator of $G$ if for some $a,b\in V$, $S$ is a minimal $a,b$-separator.

We denote by $\Delta_G$ the set of all minimal separators of $G$.



All minimal separators of a chordal graph are cliques. ([8] Proposition 2.4)



# Full Components

Let $G$ be a graph, and $S$ be a minimal separator of $G$. $\mathcal{C}_G(S)$ is the set of connected components in $G-S$. A component $C\in\mathcal{C}_G(S)$ is a full component associated to $S$ if every vertex of $S$ is adjacent to some vertex of $C$.

In [8] $\mathcal{C}_G^*(S)$ is the set of all full components associated to $S$.

A set $S \subseteq V$  is a minimal $a,b$-separator if and only if $a$ and $b$ are in different full components associated to $S$. ([8] Lemma 2.6)



If $C\in\mathcal{C}_G(S)$, $(S,C)=S\cup C$ is a block associated to $S$. A block $(S,C)$ is full if $C$ is a full component associated to $S$.

Two separators $S$ and $T$ cross if $T$ intersects at least two distinct components of $G-S$. If $S$ and $T$ do not cross, they are called parallel.

Let $G$ be a graph, $S$ a minimal separator of $G$ and $\Omega$ a clique of $G$. Then $\Omega$ is included in some block associated to $S$. In particular, the minimal separators of a chordal graph are pairwise parallel. ([8] Lemma 2.8)

Let $S\in\Delta_G$ be a minimal separator. We denote by $G_S$ the graph obtained from $G$ by completing $S$, i.e. by adding an edge between every pair of non-adjacent vertices of $S$. if $\Gamma \subseteq\Delta_G$ is a set of separators of $G$, $G_\Gamma$ is the graph obtained by completing all the seperators of $\Gamma$.

Let $\Gamma\in\Delta_G$ be a maximal set of pairwise parallel separators of $G$. Then $H=G_\Gamma$ is a minimal triangulation of $G$ and $\Delta_H=\Gamma$. Conversely, let $H$ be a minimal triangulation of a graph $G$. Then $\Delta_H$ is a maximal set of pairwise parallel separators of $G$ and $H=G_{\Delta_H}$.



# Potential Maximal Cliques

A vertex set $\Omega$ of a graph $G$ is called a potential maximal clique if there is a minimal triangulation $H$ of $G$ such that $\Omega$ is a maximal clique of $H$. ([8] Definition 3.1)



