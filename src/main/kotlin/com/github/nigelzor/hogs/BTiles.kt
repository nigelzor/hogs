package com.github.nigelzor.hogs

import kotlin.Int as BTile

/**
 * BTile:
 * byte 0:
 * byte 1:
 *  0: Player N
 * byte 2:
 *  0: Objective N
 * byte 3: (LSB)
 *  0: Connection North
 *  1: Connection East
 *  2: Connection South
 *  3: Connection West
 */
object BTiles {
	val NORTH = 1 shl 0
	val EAST = 1 shl 1
	val SOUTH = 1 shl 2
	val WEST = 1 shl 3

	val OBJECTIVE_ONE = 1 shl 8
	val OBJECTIVE_TWO = 1 shl 9
	val OBJECTIVE_THREE = 1 shl 10
	val OBJECTIVE_FOUR = 1 shl 11

	val PLAYER_ONE = 1 shl 16
	val PLAYER_TWO = 1 shl 17
	val PLAYER_THREE = 1 shl 18
	val PLAYER_FOUR = 1 shl 19

	fun eachDirection(tile: BTile, closure: (Direction) -> Unit) {
		if (tile and NORTH != 0) closure.invoke(Direction.NORTH)
		if (tile and EAST != 0) closure.invoke(Direction.EAST)
		if (tile and SOUTH != 0) closure.invoke(Direction.SOUTH)
		if (tile and WEST != 0) closure.invoke(Direction.WEST)
	}

	fun eachObjective(tile: BTile, closure: (Objective) -> Unit) {
		if (tile and OBJECTIVE_ONE != 0) closure.invoke(Objective.ONE)
		if (tile and OBJECTIVE_TWO != 0) closure.invoke(Objective.TWO)
		if (tile and OBJECTIVE_THREE != 0) closure.invoke(Objective.THREE)
		if (tile and OBJECTIVE_FOUR != 0) closure.invoke(Objective.FOUR)
	}

	fun fromDirections(vararg directions: Direction): BTile {
		var result = 0
		for (direction in directions) {
			result = result or direction.bits
		}
		return result
	}

	fun rotate(tile: BTile, rotation: Rotation): BTile {
		var connections = tile and 0x0F
		connections = connections shl rotation.ordinal
		connections = connections or (connections shr 4)

		return (tile and 0x0F.inv()) or (connections and 0x0F)
	}

	fun contains(tile: BTile, direction: Direction): Boolean {
		return tile and direction.bits != 0
	}

	fun contains(tile: BTile, objective: Objective): Boolean {
		return tile and objective.bits != 0
	}

	fun contains(tile: BTile, colour: Colour): Boolean {
		return tile and colour.bits != 0
	}
}

// adding extension methods on Int is pretty nasty, but effective
fun BTile.rotate(rotation: Rotation): BTile {
	return BTiles.rotate(this, rotation)
}

fun BTile.contains(direction: Direction): Boolean {
	return BTiles.contains(this, direction)
}

fun BTile.contains(objective: Objective): Boolean {
	return BTiles.contains(this, objective)
}

fun BTile.contains(colour: Colour): Boolean {
	return BTiles.contains(this, colour)
}

fun BTile.containsNoObjectives(): Boolean {
	return this and 0xF00 == 0
}

fun BTile.containsNoPlayers(): Boolean {
	return this and 0xF0000 == 0
}

fun BTile.with(objective: Objective): BTile {
	return this or objective.bits
}

fun BTile.with(colour: Colour): BTile {
	return this or colour.bits
}

fun BTile.without(colour: Colour): BTile {
	return this and colour.bits.inv()
}