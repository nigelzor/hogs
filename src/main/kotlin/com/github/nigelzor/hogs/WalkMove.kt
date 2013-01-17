package com.github.nigelzor.hogs

public open class WalkMove(val from: Position, val to: Position): Move {
	public override fun apply(board: Board) {
		val player = board.currentPlayer
		assert(from.players.contains(player))
		from.players.remove(player)
		to.players.add(player)
	}
}
