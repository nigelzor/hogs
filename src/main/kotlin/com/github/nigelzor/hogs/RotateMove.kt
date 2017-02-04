package com.github.nigelzor.hogs

data class RotateMove(val index: Index, val rotation: Rotation): Move {
	override fun apply(board: Board) {
		board.tiles[index] = board.tiles[index]!!.rotate(rotation)
	}
}
