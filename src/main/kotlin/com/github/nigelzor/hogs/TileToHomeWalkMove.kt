package com.github.nigelzor.hogs

data class TileToHomeWalkMove(val from: Index, val to: Colour): Move {
	override fun apply(board: Board) {
		val player = board.step.player
		val source = board.tiles[from]!!
		val destination = board.homes[to]

		assert(source.contains(player), { "player not at source position" })
		assert(!destination.contains(player), { "player already at destination position" })

		board.tiles[from] = source.without(player)
		board.homes[to] = destination.with(player)
	}
}
