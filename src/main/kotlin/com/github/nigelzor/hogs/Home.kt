package com.github.nigelzor.hogs

import java.util.HashSet

public data class Home(val colour: Colour, override val players: MutableSet<Int> = HashSet()): Position {

	public fun clone(): Home {
		return copy(players = HashSet(players))
	}

}
