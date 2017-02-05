package com.github.nigelzor.hogs

data class Player(val colour: Colour, val collected: Int = 0) {

	fun with(objective: Objective): Player {
		return copy(collected = collected or objective.bits)
	}

	fun collectedEverything(): Boolean {
		return collected == Objective.ALL_BITS
	}

}
