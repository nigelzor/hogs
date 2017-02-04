package com.github.nigelzor.hogs

object TileFactory {
	fun straight(): Tile {
		return Tile.fromDirections(Direction.WEST, Direction.EAST)
	}
	fun elbow(): Tile {
		return Tile.fromDirections(Direction.WEST, Direction.SOUTH)
	}
	fun tee(): Tile {
		return Tile.fromDirections(Direction.WEST, Direction.SOUTH, Direction.EAST)
	}
	fun homework(): Tile {
		return Tile.fromDirections(Direction.WEST, Direction.EAST).copy(objective = Objective.ONE)
	}
	fun potions(): Tile {
		return Tile.fromDirections(Direction.NORTH, Direction.SOUTH).copy(objective = Objective.TWO)
	}
	fun creatures(): Tile {
		return Tile.fromDirections(Direction.NORTH, Direction.EAST).copy(objective = Objective.THREE)
	}
	fun tower(): Tile {
		return Tile.fromDirections(Direction.SOUTH).copy(objective = Objective.FOUR)
	}
}
