package com.github.nigelzor.hogs

data class TileToHomeWalkMove(val from: Index, val to: Int): Move {
	override fun apply(board: Board) {
		val player = board.piToMove

		assert(board.tiles[from]!!.players.contains(player), { "player not at source position" })
		assert(!board.homes[to].players.contains(player), { "player already at destination position" })

		board.tiles[from]!!.players.remove(player)
		board.homes[to].players.add(player)
	}
}
