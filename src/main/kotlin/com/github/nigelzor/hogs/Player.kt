package com.github.nigelzor.hogs

import java.util.HashSet

public data class Player(val colour: Colour, val collected: Set<Objective> = HashSet()) {

}
