package com.github.nigelzor.hogs

enum class Colour {
	BLUE,
	YELLOW,
	RED,
	GREEN;

	val bits = 1 shl (16 + ordinal)
}

operator fun <T> List<T>.get(colour: Colour): T {
	return this[colour.ordinal]
}

operator fun <T> MutableList<T>.set(colour: Colour, value: T) {
	this[colour.ordinal] = value
}