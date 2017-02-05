package com.github.nigelzor.hogs

enum class Objective {
	ONE,
	TWO,
	THREE,
	FOUR;

	val bits = 1 shl (8 + ordinal)
}
