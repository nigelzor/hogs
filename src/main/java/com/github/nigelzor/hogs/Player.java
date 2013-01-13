package com.github.nigelzor.hogs;

import java.util.EnumSet;
import java.util.Set;

import com.google.common.base.Objects;

public class Player {
	private Colour colour;
	private Set<Objective> collected = EnumSet.noneOf(Objective.class);

	public Player(Colour colour) {
		this.colour = colour;
	}

	public Set<Objective> getCollected() {
		return collected;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(colour, collected);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() !=  obj.getClass()) return false;
		Player other = (Player) obj;
		return Objects.equal(colour, other.colour)
				&& Objects.equal(collected, other.collected);
	}
}
