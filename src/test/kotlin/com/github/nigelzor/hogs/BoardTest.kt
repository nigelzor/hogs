package com.github.nigelzor.hogs

import org.hamcrest.Matchers.hasSize
import org.junit.Assert.assertThat
import org.junit.Test

public class BoardTest {
	[Test]
	public fun testFirstTurnWalkingMoves() {
		var board = Board.defaultBoard()
		var potentialMoves = board.possible()
		// NoMove, Walk(Home->Board), 3 rotations of every tile
		assertThat(potentialMoves, hasSize(1 + 1 + 3 * 16))
	}
}
