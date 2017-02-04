package com.github.nigelzor.hogs

import org.hamcrest.Matchers.hasSize
import org.junit.Assert.assertThat
import org.junit.Test

class BoardTest {
	@Test fun testFirstTurnWalkingMoves() {
		val board = Board.defaultBoard()
		val potentialMoves = board.possible()
		// 3 rotations of every non-classroom tile + Walk(Home->Board)
		// corners are T, so no rotation can block the walk
		assertThat(potentialMoves, hasSize(3 * 12))
	}
}
