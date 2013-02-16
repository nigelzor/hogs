package com.github.nigelzor.hogs

public data class HomeConnection(val row: Int, val col: Int, val connections: Set<Direction>) {
	val index: Index
		get() = Index(row, col)
}
