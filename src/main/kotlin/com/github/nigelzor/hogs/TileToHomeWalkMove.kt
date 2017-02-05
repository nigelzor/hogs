package com.github.nigelzor.hogs

data class TileToHomeWalkMove(val from: Index, val to: Int): Move {
	override fun apply(board: Board) {
		val pi = board.piToMove
		val player = board.players[pi]
		val source = board.tiles[from]!!
		val destination = board.homes[to]

		assert(source.contains(player), { "player not at source position" })
		assert(!destination.contains(player), { "player already at destination position" })

		board.tiles[from] = source.without(player)
		board.homes[to] = destination.with(player)
	}
}
