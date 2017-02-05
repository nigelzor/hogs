package com.github.nigelzor.hogs

data class LiftMove(val from: Index, val shifts: List<Index>): Move {
    override fun apply(board: Board) {
        val lifted = board.tiles[from]
        assert(lifted!!.containsNoPlayers(), { "can't lift tile containing players" })
        board.tiles[from] = null
        for ((row, col) in shifts) {
            board.tiles.shift(row, col)
        }
        val to = shifts.last()
        assert(board.tiles[to] == null, { "can't put down in non-empty space" })
        board.tiles[to] = lifted
    }
}