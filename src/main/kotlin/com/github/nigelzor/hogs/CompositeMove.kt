package com.github.nigelzor.hogs

public data class CompositeMove(val moves: List<Move>): Move {
	override fun apply(board: Board) {
		moves.forEach { it.apply(board) }
	}
}