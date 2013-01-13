package com.github.nigelzor.hogs;

import java.util.EnumSet;
import java.util.Set;

public enum Direction {
	NORTH,
	EAST,
	SOUTH,
	WEST;

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
}
