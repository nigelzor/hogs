package com.github.nigelzor.hogs;

import com.google.common.base.Function;

public abstract class Move implements Function<Board, Board> {

	/**
	 * it is not the Move's responsibility to adjust the current turn
	 * or collect objectives, only to move tiles and/or players
	 */
	@Override
	public abstract Board apply(Board input);

}
