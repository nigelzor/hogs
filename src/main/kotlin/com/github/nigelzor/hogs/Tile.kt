package com.github.nigelzor.hogs;

import java.util.HashSet

public data class Tile(val connections: Set<Direction>, val objective: Objective? = null, override val players: MutableSet<Player> = HashSet()) : Position {

	public fun rotate(rotation: Rotation): Tile {
		return Tile(connections.map { it.rotate(rotation) }.toSet(), objective, players)
	}

	public fun connectsTo(direction: Direction): Boolean {
		return connections.contains(direction)
	}

}
