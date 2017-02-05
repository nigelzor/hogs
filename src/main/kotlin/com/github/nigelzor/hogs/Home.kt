package com.github.nigelzor.hogs

import kotlin.Int
import kotlin.Int as BTile

data class Home(val row: Int, val col: Int, val tile: BTile) {
	val index: Index
		get() = Index(row, col)

	fun contains(colour: Colour): Boolean {
		return tile.contains(colour)
	}

	fun with(colour: Colour): Home {
		return copy(tile = tile.with(colour))
	}

	fun without(colour: Colour): Home {
		return copy(tile = tile.without(colour))
	}
}
