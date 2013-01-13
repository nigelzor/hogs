package com.github.nigelzor.hogs;

import java.util.EnumSet;
import java.util.Set;

import com.google.common.base.Function;

public enum Direction implements Function<Index, Index> {
	NORTH(-1, 0),
	EAST(0, 1),
	SOUTH(1, 0),
	WEST(0, -1);

	private final int row;
	private final int col;

	private Direction(int row, int col) {
		this.row = row;
		this.col = col;
	}

	public Direction rotate(Rotation rotation) {
		return Direction.values()[(ordinal() + rotation.ordinal()) % 4];
	}

	public static Set<Direction> rotate(Set<Direction> directions, Rotation rotation) {
		EnumSet<Direction> result = EnumSet.noneOf(Direction.class);
		for (Direction direction : directions) {
			result.add(direction.rotate(rotation));
		}
		return result;
	}

	@Override
	public Index apply(Index input) {
		return new Index(input.getRow() + row, input.getCol() + col);
	}

}
