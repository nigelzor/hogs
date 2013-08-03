package com.github.nigelzor.hogs;

import java.util.HashSet

import jet.Int as BTile

public data class Tile(val connections: BTile, val objective: Objective? = null, override val players: MutableSet<Int> = HashSet()): Position {
	class object {
		fun fromDirections(vararg ds : Direction): Tile {
			return Tile(BTiles.fromDirections(ds.toList()))
		}
	}

	public fun rotate(rotation: Rotation): Tile {
		return Tile(BTiles.rotate(connections, rotation), objective, players)
	}

	public fun connectsTo(direction: Direction): Boolean {
		return BTiles.contains(connections, direction)
	}

	public fun clone(): Tile {
		return copy(players=HashSet(players))
	}

}
