package com.github.nigelzor.hogs

import com.github.nigelzor.mcts.Weighted

data class RollMove(val rolled: Rolled): Move, Weighted {
    override val weight: Double
        get() = rolled.weight

    override fun apply(board: Board) {
        board.rolled = rolled
    }
}