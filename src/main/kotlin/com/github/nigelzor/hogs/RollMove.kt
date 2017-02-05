package com.github.nigelzor.hogs

data class RollMove(val rolled: Board.Rolled): Move {
    override fun apply(board: Board) {
        board.rolled = rolled
    }
}