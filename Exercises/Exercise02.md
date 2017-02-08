# Exercise 02

## 1) Search Tree
### a)
Independent set of planar graphs

since there most always be a vertex with degree at most 5 we can make the following algorithm:

find node u such that degree of u is at most 5
Observe that u is either in an independent set or one of its 5 neighbours is.
therefore:
    solve recursively when u is added to s and all the children and u is removed from the graph.
    for each neighbour v
        remove u and v and let V be in the IndSet and solve recursively

K gets reduced each recursion

notice we have 6 calls each reducing k therefore running time is 


    IndSet(G, k, S)
    {
        find vertex u such that Degree(u) <= 5
        return IndSet(G - {u} union neighbours(u), k-1, S union {u}) || any neighbour v (IndSet(G-{u,v}, k-1, S union {v}))
    }
6^k*(n+m)
### b)
For dominating set to be solved, we observe that either u or one or more of its neighbours must be in the dominating set - before we just stated that it was in A independent set but actually it must also be part of the maximum independent set. Therefore the algorithm finds out if a dominating set is in the graph of size k if the algorithm above finds a maximum independent set which is a dominating set. 

Maybe we need to change the first case where we do not remove all the neighbours.

### c)
find the vertex cover using the search tree algorithm and make V-S the independent set

## 2)
Observe that a leaf node can never be in the optimal solution unless its parent is also - therefore we have an exchange argument. 
if a parent has 1 child then picking the children the parent is just as good as picking the child.
if a paren has more than 1 child then the by taking the parent we use fewer elements than if we had taken every child (which is neccesary)

Therefore the dominating set can be found with the folowing algorithm
    while(tree has edges)
    {
        find vertex u with one or more *leaf* vertices
        add u to S
        remove u.
    }
finding a vertex with only leaf nodes = n+m
doing this n times.
this algorithm finds the dominating set in at most O(n^2).

## 3)
bruteforce: all k-subsets of m edges where sum of weights is atleast l. also including s and t must be in.
run dijeksta and see if a path exists.

## 4)
