package com.github.nigelzor.hogs

import kotlin.Int as BTile

object TileFactory {
	fun straight() = BTiles.fromDirections(Direction.WEST, Direction.EAST)
	fun elbow() = BTiles.fromDirections(Direction.WEST, Direction.SOUTH)
	fun tee() = BTiles.fromDirections(Direction.WEST, Direction.SOUTH, Direction.EAST)
	fun homework() = BTiles.fromDirections(Direction.WEST, Direction.EAST).with(Objective.ONE)
	fun potions() = BTiles.fromDirections(Direction.NORTH, Direction.SOUTH).with(Objective.TWO)
	fun creatures() = BTiles.fromDirections(Direction.NORTH, Direction.EAST).with(Objective.THREE)
	fun tower() = BTiles.fromDirections(Direction.SOUTH).with(Objective.FOUR)
}
