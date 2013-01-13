package com.github.nigelzor.hogs;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;

public class BoardTest {

	@Test
	public void testFirstTurnWalkingMoves() {
		Board board = new Board();
		Set<Move> potentialMoves = board.potentialMoves();
		assertThat(potentialMoves, hasSize(2));
	}

}
