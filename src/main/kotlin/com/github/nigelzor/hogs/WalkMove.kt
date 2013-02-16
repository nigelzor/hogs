package com.github.nigelzor.hogs

import kotlin.test.assertFalse
import kotlin.test.assertTrue

public data class WalkMove(val from: (Board) -> Position, val to: (Board) -> Position): Move {
	public override fun apply(board: Board) {
		val player = board.currentPlayer

		assert(from(board).players.contains(player), "player not at source position")
		assert(!to(board).players.contains(player), "player already at destination position")

		from(board).players.remove(player)
		to(board).players.add(player)
	}
}
