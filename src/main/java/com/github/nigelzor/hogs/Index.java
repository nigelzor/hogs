package com.github.nigelzor.hogs;

class Index {
	private final int row;
	private final int col;

	public Index(int row, int col) {
		this.row = row;
		this.col = col;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
}