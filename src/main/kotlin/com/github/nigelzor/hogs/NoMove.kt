package com.github.nigelzor.hogs

public class NoMove(): Move {
	public override fun apply(board: Board): Board {
		return board
	}
}
