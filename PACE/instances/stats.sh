#!/bin/bash
for graph in `ls *.graph | sort -V`; do
	echo -n "$graph "
	java GraphStatistics "$graph"
done
