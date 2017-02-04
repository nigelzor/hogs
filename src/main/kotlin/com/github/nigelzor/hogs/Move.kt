package com.github.nigelzor.hogs

interface Move {
	/**
	 * it is not the Move's responsibility to adjust the current turn
	 * or collect objectives, only to move tiles and/or players
	 */
	fun apply(board: Board)
}

