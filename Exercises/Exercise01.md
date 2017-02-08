# Exercise 01

## 1)
3 coloring with probability 2/3c*
Take a node and color it a random color.
Note that for each edge it has probability 6/9 satisfiable color combinations:
    r, r
    g, g 
    b, b 
    r, g 
    r, b 
    g, r 
    g, b 
    b, r
    b, g 

Therefore:
Random variable X = the number of satisfied edges
Xi = ei is satisfied
Notice that c*<=m
E[Xi] = the expected value of edge to be satisfied: 2/3
E[X] = sum of the probability of all edges m beign satisfied = 2/3m <= 2/3c*

## 2)
### a)
Total size is: 100_000
H = 80_000
T = 20_000
P = 0.01 for not voting what you want

X = number of people voting H
Xi = i voting H 

E[Xi]=4/5*(1-P)+1/5*P = 0.794
E[X] = Sum of all i E[Xi] = 73 800


### b)
P=0.5

## 3)
probability of guessing 1 element the same is k/n
