package com.github.nigelzor.hogs;

public class WalkMove extends Move {
	private final Direction direction;
	private final Player player;
	private final Position from;
	private final Position to;

	public WalkMove(Player player, Direction direction, Position from, Position to) {
		this.player = player;
		this.direction = direction;
		this.from = from;
		this.to = to;
	}

	@Override
	public Board apply(Board input) {
		from.getPlayers().remove(player);
		to.getPlayers().add(player);
		return input;
	}
}
