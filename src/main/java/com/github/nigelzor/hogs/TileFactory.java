package com.github.nigelzor.hogs;

import java.util.EnumSet;

public class TileFactory {

	/**
	 * west-east
	 */
	public static Tile straight() {
		return new Tile(EnumSet.of(Direction.WEST, Direction.EAST));
	}

	/**
	 * west-south
	 */
	public static Tile elbow() {
		return new Tile(EnumSet.of(Direction.WEST, Direction.SOUTH));
	}

	/**
	 * west-north-east
	 */
	public static Tile tee() {
		return new Tile(EnumSet.of(Direction.WEST, Direction.SOUTH, Direction.EAST));
	}

	/**
	 * west-east, objective one
	 */
	public static Tile homework() {
		return new Tile(EnumSet.of(Direction.WEST, Direction.EAST), Objective.ONE);
	}

	/**
	 * north-south, objective two
	 */
	public static Tile potions() {
		return new Tile(EnumSet.of(Direction.NORTH, Direction.SOUTH), Objective.TWO);
	}

	/**
	 * north-east, objective three
	 */
	public static Tile creatures() {
		return new Tile(EnumSet.of(Direction.NORTH, Direction.EAST), Objective.THREE);
	}

	/**
	 * south, objective four
	 */
	public static Tile tower() {
		return new Tile(EnumSet.of(Direction.SOUTH), Objective.FOUR);
	}

}
