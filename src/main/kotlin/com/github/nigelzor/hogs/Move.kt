package com.github.nigelzor.hogs

interface Move {
	/**
	 * it is not the Move's responsibility to adjust the current turn.
	 * it should move tiles and/or players and collect objectives.
	 */
	fun apply(board: Board)
}

