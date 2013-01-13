package com.github.nigelzor.hogs;

import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

public class Tile implements Position {
	private Set<Direction> connections;
	private Optional<Objective> objective;
	private Set<Player> players;

	public Tile(Set<Direction> connections) {
		this.connections = connections;
		this.objective = Optional.absent();
	}

	public Tile(Set<Direction> connections, Objective objective) {
		this.connections = connections;
		this.objective = Optional.of(objective);
	}

	private Tile(Set<Direction> connections, Optional<Objective> objective, Set<Player> players) {
		this.connections = connections;
		this.objective = objective;
		this.players = players;
	}

	public Tile rotate(Rotation rotation) {
		return new Tile(Direction.rotate(connections, rotation), objective, players);
	}

	public Set<Direction> getConnections() {
		return connections;
	}

	public boolean connectsTo(Direction direction) {
		return connections.contains(direction);
	}

	@Override
	public Set<Player> getPlayers() {
		return players;
	}

	public Optional<Objective> getObjective() {
		return objective;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(connections, objective, players);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Tile other = (Tile) obj;
		return Objects.equal(connections, other.connections)
				&& Objects.equal(objective, other.objective)
				&& Objects.equal(players, other.players);
	}
}
