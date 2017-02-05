package com.github.nigelzor.hogs

import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.hasSize
import org.junit.Assert.assertThat
import org.junit.Test
import java.util.*

class BoardTest {
	@Test fun testFirstTurnWalkMoves() {
		val board = Board.defaultBoard()
		var potentialMoves = board.possibleWalkMoves(board.step.player)
		assertThat(potentialMoves, hasSize(1))

		// but if the corner tile is a L facing the wrong way
		val tmp = board.tiles[0, 0]
		board.tiles[0, 0] = board.tiles[0, 1]
		board.tiles[0, 1] = tmp
		potentialMoves = board.possibleWalkMoves(board.step.player)
		assertThat(potentialMoves, hasSize(0))

		// unless you've got the map
		potentialMoves = board.possibleWalkMoves(board.step.player, true)
		assertThat(potentialMoves, hasSize(1))
	}

	@Test fun testFirstTurnRotateMoves() {
		val board = Board.defaultBoard()
		val potentialMoves = board.possibleRotateMoves()
		assertThat(potentialMoves, hasSize(12 * 3))
	}

	@Test fun testFirstTurnLiftMoves() {
		val board = Board.defaultBoard()
		val potentialMoves = board.possibleLiftMoves(1)
		assertThat(potentialMoves, hasSize(12 * 6))
	}

	@Test fun testApplyFirstTurnMoves() {
		val board = Board.defaultBoard()
		val potentialMoves = board.possible()
		for (move in potentialMoves) {
			// just check that nothing throws
			board.clone().apply(move)
		}
	}

	@Test fun testApplyRandomFirstTurnMoves() {
		val rng = Random()
		val board = Board.defaultBoard()
		val potentialMoves = board.possible()
		(0..1000).forEach {
			val move = board.randomMove(rng)!!
			assertThat(potentialMoves, hasItem(move))
			board.clone().apply(move)
		}
	}
}
