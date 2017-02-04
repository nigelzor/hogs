package com.github.nigelzor.hogs

enum class Direction(val row: Int, val col: Int) {
	NORTH(-1, 0),
	EAST(0, 1),
	SOUTH(1, 0),
	WEST(0, -1);

	val bits = 1 shl ordinal

	fun rotate(rotation: Rotation): Direction {
		return Direction.values()[(ordinal + rotation.ordinal) % 4]
	}

	fun apply(input: Index): Index {
		return Index(input.row + row, input.col + col)
	}
}
