package com.github.nigelzor.hogs

import org.hamcrest.Matchers.hasSize
import org.junit.Assert.assertThat
import org.junit.Test

public class BoardTest {
	[Test]
	public fun testFirstTurnWalkingMoves() {
		var board = Board()
		var potentialMoves = board.possible()
		assertThat(potentialMoves, hasSize(2))
	}
}
