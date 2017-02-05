package com.github.nigelzor.hogs

// NOTE-NG: data classes with no members don't appear to work, so this is an enum instead
enum class NoMove : Move {
	INSTANCE;

	override fun apply(board: Board) {
		// boring
	}

	override fun toString(): String {
		return "NoMove()"
	}
}
