package com.github.nigelzor.hogs

public class TileFactory() {
	class object {
		public fun straight(): Tile {
			return Tile.fromDirections(Direction.WEST, Direction.EAST)
		}
		public fun elbow(): Tile {
			return Tile.fromDirections(Direction.WEST, Direction.SOUTH)
		}
		public fun tee(): Tile {
			return Tile.fromDirections(Direction.WEST, Direction.SOUTH, Direction.EAST)
		}
		public fun homework(): Tile {
			return Tile.fromDirections(Direction.WEST, Direction.EAST).copy(objective = Objective.ONE)
		}
		public fun potions(): Tile {
			return Tile.fromDirections(Direction.NORTH, Direction.SOUTH).copy(objective = Objective.TWO)
		}
		public fun creatures(): Tile {
			return Tile.fromDirections(Direction.NORTH, Direction.EAST).copy(objective = Objective.THREE)
		}
		public fun tower(): Tile {
			return Tile.fromDirections(Direction.SOUTH).copy(objective = Objective.FOUR)
		}
	}
}
