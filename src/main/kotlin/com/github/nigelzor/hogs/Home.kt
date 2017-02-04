package com.github.nigelzor.hogs

import java.util.HashSet

data class Home(val colour: Colour, override val players: MutableSet<Int> = HashSet()): Position {

	fun clone(): Home {
		return copy(players = HashSet(players))
	}

}
