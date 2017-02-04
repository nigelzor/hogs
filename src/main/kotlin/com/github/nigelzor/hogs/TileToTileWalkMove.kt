package com.github.nigelzor.hogs

public data class TileToTileWalkMove(val from: Index, val to: Index): Move {
	public override fun apply(board: Board) {
		val player = board.piToMove

		assert(board.tiles[from]!!.players.contains(player), { "player not at source position" })
		assert(!board.tiles[to]!!.players.contains(player), { "player already at destination position" })

		board.tiles[from]!!.players.remove(player)
		board.tiles[to]!!.players.add(player)
	}
}
