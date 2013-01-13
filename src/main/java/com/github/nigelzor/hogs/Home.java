package com.github.nigelzor.hogs;

import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

public class Home implements Position {
	private final Colour colour;
	private final Set<Player> players;

	public Home(Colour colour) {
		this.colour = colour;
		this.players = Sets.newHashSet();
	}

	public Colour getColour() {
		return colour;
	}

	@Override
	public Set<Player> getPlayers() {
		return players;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(colour, players);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Home other = (Home) obj;
		return Objects.equal(colour, other.colour)
				&& Objects.equal(players, other.players);
	}
}
