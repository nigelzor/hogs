package com.github.nigelzor.hogs

import kotlin.Int
import kotlin.Int as BTile

data class HomeConnection(val row: Int, val col: Int, val connections: BTile) {
	val index: Index
		get() = Index(row, col)
}
