package com.github.nigelzor.hogs

import kotlin.Int
import kotlin.Int as BTile

data class Home(val row: Int, val col: Int, val tile: BTile) {
	val index: Index
		get() = Index(row, col)

	fun contains(player: Player): Boolean {
		return tile.contains(player)
	}

	fun rotate(rotation: Rotation): Home {
		return copy(tile = tile.rotate(rotation))
	}

	fun with(player: Player): Home {
		return copy(tile = tile.with(player))
	}

	fun without(player: Player): Home {
		return copy(tile = tile.without(player))
	}
}
