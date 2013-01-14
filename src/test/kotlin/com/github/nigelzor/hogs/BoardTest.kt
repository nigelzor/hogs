package com.github.nigelzor.hogs

import org.junit.Test
import org.junit.Assert.assertThat
import org.hamcrest.Matchers.hasSize

public class BoardTest {
	[Test]
	public fun testFirstTurnWalkingMoves() {
		var board : Board? = Board()
		var potentialMoves : Set<Move?>? = board?.potentialMoves()
		assertThat(potentialMoves, hasSize(2))
	}
}
