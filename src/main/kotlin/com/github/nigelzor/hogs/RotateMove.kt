package com.github.nigelzor.hogs

public data class RotateMove(val index: Index, val rotation: Rotation): Move {
	public override fun apply(board: Board) {
		board.tiles[index] = board.tiles[index]!!.rotate(rotation)
	}
}
