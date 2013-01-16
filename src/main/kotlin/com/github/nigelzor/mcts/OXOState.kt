package com.github.nigelzor.mcts

import kotlin.nullable.toSet

public class OXOState: GameState<Int> {
	override var playerJustMoved = 2
	var board = array(0,0,0,0,0,0,0,0,0)

	override fun clone(): OXOState {
		val clone = OXOState()
		clone.playerJustMoved = playerJustMoved
		clone.board = board.copyOf()
		return clone
	}

	override fun apply(move: Int) {
		assert(board[move] == 0, "position already taken")
		playerJustMoved = 3 - playerJustMoved
		board[move] = playerJustMoved
	}

	override fun possible(): Set<Int> {
		return board.indices.filter { board[it] == 0 }.toSet()
	}

	override fun result(playerJustMoved: Int): Double {
		for ((x,y,z) in array(Triple(0, 1, 2), Triple(3, 4, 5), Triple(6, 7, 8), Triple(0, 3, 6), Triple(1, 4, 7), Triple(2, 5, 8), Triple(0, 4, 8), Triple(2, 4, 6))) {
			if (board[x] == board[y] && board[y] == board[z]) {
				if (board[x] == playerJustMoved) {
					return 1.0
				} else {
					return 0.0
				}
			}
		}
		if (possible().empty) return 0.5 // draw
		throw IllegalStateException()
	}

	fun toString(): String {
		var s = StringBuilder()
		for (i in board.indices) {
			s.append(".XO"[board[i]])
			if (i % 3 == 2) s.append("\n")
		}
		return s.toString()
	}

}