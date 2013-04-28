package com.github.nigelzor.hogs

import java.util.HashSet

public data class Player(val colour: Colour, val collected: MutableSet<Objective> = HashSet()) {

	public fun clone(): Player {
		return copy(collected=HashSet(collected))
	}

}
