package com.github.nigelzor.hogs

import jet.Int as BTile
import java.util.EnumSet

/**
 * BTile:
 * byte 0:
 * byte 1:
 * byte 2
 * byte 3: (LSB)
 *  0: Connection North
 *  1: Connection East
 *  2: Connection South
 *  3: Connection West
 */
public class BTiles {
	class object {
		fun eachDirection(tile: BTile, closure: (Direction) -> Unit) {
			if (tile and 0x01 != 0) closure.invoke(Direction.NORTH)
			if (tile and 0x02 != 0) closure.invoke(Direction.EAST)
			if (tile and 0x04 != 0) closure.invoke(Direction.SOUTH)
			if (tile and 0x08 != 0) closure.invoke(Direction.WEST)
		}

		// very slow!
		fun toDirections(tile: BTile): Set<Direction> {
			val result: MutableSet<Direction> = hashSetOf()
			if (tile and 0x01 != 0) result.add(Direction.NORTH)
			if (tile and 0x02 != 0) result.add(Direction.EAST)
			if (tile and 0x04 != 0) result.add(Direction.SOUTH)
			if (tile and 0x08 != 0) result.add(Direction.WEST)
			return result
		}

		fun fromDirections(directions: Iterable<Direction>): BTile {
			var result = 0
			for (direction in directions) {
				result = result or (1 shl direction.ordinal())
			}
			return result
		}

		fun rotate(tile: BTile, rotation: Rotation): BTile {
			if (rotation == Rotation.ZERO_DEGREES) return tile

			var connections = tile and 0x0F
			connections = connections or (connections shl 4)
			connections = connections shl rotation.ordinal()
			connections = connections shr 4

			return (tile and 0x0F.inv()) or (connections and 0x0F)
		}

		fun contains(tile: BTile, direction: Direction): Boolean {
			return tile and (1 shl direction.ordinal()) != 0
		}
	}
}