package com.github.nigelzor.hogs

enum class Colour {
	BLUE,
	YELLOW,
	RED,
	GREEN;

	val bits = 1 shl (16 + ordinal)
}
