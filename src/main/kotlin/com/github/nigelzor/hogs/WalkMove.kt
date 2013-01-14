package com.github.nigelzor.hogs

public open class WalkMove(val player : Player, val direction : Direction, val from : Position, val to : Position) : Move {
	public override fun apply(board : Board) : Board {
		from.players.remove(player)
		to.players.add(player)
		return board
	}
}
