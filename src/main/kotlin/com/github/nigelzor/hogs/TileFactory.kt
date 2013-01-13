package com.github.nigelzor.hogs

public class TileFactory() {
	class object {
		public fun straight(): Tile {
			return Tile(hashSetOf(Direction.WEST, Direction.EAST))
		}
		public fun elbow(): Tile {
			return Tile(hashSetOf(Direction.WEST, Direction.SOUTH))
		}
		public fun tee(): Tile {
			return Tile(hashSetOf(Direction.WEST, Direction.SOUTH, Direction.EAST))
		}
		public fun homework(): Tile {
			return Tile(hashSetOf(Direction.WEST, Direction.EAST), Objective.ONE)
		}
		public fun potions(): Tile {
			return Tile(hashSetOf(Direction.NORTH, Direction.SOUTH), Objective.TWO)
		}
		public fun creatures(): Tile {
			return Tile(hashSetOf(Direction.NORTH, Direction.EAST), Objective.THREE)
		}
		public fun tower(): Tile {
			return Tile(hashSetOf(Direction.SOUTH), Objective.FOUR)
		}
	}
}
