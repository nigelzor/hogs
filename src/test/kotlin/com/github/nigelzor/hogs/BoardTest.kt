package com.github.nigelzor.hogs

import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.hasSize
import org.junit.Assert.assertThat
import org.junit.Test
import java.util.*

class BoardTest {
	@Test fun testFirstTurnMoves() {
		val board = Board.defaultBoard()
		val potentialMoves = board.possible()
		var expected = 0
		// 3 rotations of every non-classroom tile + Walk(Home->Board)
		// corners are T, so no rotation can block the walk
		expected += 3 * 12
		// but you can also rotate and not walk
		expected += 3 * 12
		// walk once, stop
		expected += 1
		// walk once, then back the way you came
		expected += 1
		// walk once, then continue to the next tile
		expected += 1
		// 12 liftable tiles, each with 6 tiles that could shift into place (and then don't walk)
		expected += 12 * 6
		// 12 liftable tiles, each with 6 tiles that could shift into place (and then walk? this number is wrong-ish)
		expected += 12 * 6
//		potentialMoves.forEach(::println)
		assertThat(potentialMoves, hasSize(expected))
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
