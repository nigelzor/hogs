package com.github.nigelzor.hogs

import java.util.HashSet

public data class Home(val colour: Colour, override val players: MutableSet<Player> = HashSet()) : Position {

}