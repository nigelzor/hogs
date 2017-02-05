package com.github.nigelzor.hogs

data class TileToTileWalkMove(val from: Index, val to: Index): Move {
	override fun apply(board: Board) {
		val pi = board.piToMove
		val player = board.players[pi]
		val source = board.tiles[from]!!
		val destination = board.tiles[to]!!

		assert(source.contains(player), { "player not at source position" })
		assert(!destination.contains(player), { "player already at destination position" })

		board.tiles[from] = source.without(player)
		board.tiles[to] = destination.with(player)
		BTiles.eachObjective(destination) {
			board.players[pi] = player.with(it)
		}
	}
}
