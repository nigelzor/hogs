package com.github.nigelzor.hogs

data class HomeToTileWalkMove(val from: Int, val to: Index): Move {
	override fun apply(board: Board) {
		val pi = board.piToMove
		val player = board.players[pi]
		val source = board.homes[from]
		val destination = board.tiles[to]!!

		assert(source.players.contains(pi), { "player not at source position" })
		assert(!destination.contains(player), { "player already at destination position" })

		source.players.remove(pi)
		board.tiles[to] = destination.with(player)
		BTiles.eachObjective(destination) {
			player.collected.add(it)
		}
	}
}
