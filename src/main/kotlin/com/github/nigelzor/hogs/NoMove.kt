package com.github.nigelzor.hogs

// NOTE-NG: data classes with no members don't appear to work, so this is an enum instead
public enum class NoMove(): Move {
	INSTANCE

	public override fun apply(board: Board) {
		// boring
	}

	public fun toString(): String {
		return "NoMove()"
	}
}
