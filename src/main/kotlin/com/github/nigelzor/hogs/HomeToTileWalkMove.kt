package com.github.nigelzor.hogs

data class HomeToTileWalkMove(val from: Colour, val to: Index): Move {
	override fun apply(board: Board) {
		val player = board.step.player
		val source = board.homes[from]
		val destination = board.tiles[to]!!

		assert(source.contains(player), { "player not at source position" })
		assert(!destination.contains(player), { "player already at destination position" })

		board.homes[from] = source.without(player)
		board.tiles[to] = destination.with(player)
		BTiles.eachObjective(destination) {
			board.players[player] = board.players[player].with(it)
		}
	}
}
