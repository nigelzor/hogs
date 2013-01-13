package com.github.nigelzor.hogs

import java.util.EnumSet

public enum class Direction(val row: Int, val col: Int) {
	NORTH : Direction(-1, 0)
	EAST : Direction(0, 1)
	SOUTH : Direction(1, 0)
	WEST : Direction(0, -1)

	public fun rotate(rotation: Rotation): Direction {
		return Direction.values()[(ordinal() + rotation.ordinal()) % 4];
	}

	public fun apply(input: Index): Index {
		return Index(input.row + row, input.col + col)
	}

}
