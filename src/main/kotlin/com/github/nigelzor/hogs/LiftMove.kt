package com.github.nigelzor.hogs

data class LiftMove(val from: Index, val to: Index): Move {
    override fun apply(board: Board) {
        val lifted = board.tiles[from]
        assert(lifted!!.players.isEmpty(), { "can't lift tile containing players" })
        board.tiles[from] = null
        board.tiles.shift(to.row, to.col)
        assert(board.tiles[to] == null, { "can't put down in non-empty space" })
        board.tiles[to] = lifted
    }
}