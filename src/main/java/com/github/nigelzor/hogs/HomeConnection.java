package com.github.nigelzor.hogs;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public class HomeConnection {
	private int row;
	private int col;
	private Set<Direction> edges;

	public HomeConnection(int row, int col, Direction... edges) {
		this.row = row;
		this.col = col;
		this.edges = ImmutableSet.copyOf(edges);
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public Set<Direction> getEdges() {
		return edges;
	}
}
