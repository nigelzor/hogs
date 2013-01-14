package com.github.nigelzor.hogs

import java.util.HashSet

public data class Board {
	private var currentPlayer: Int = 0
	private val players: List<Player>
	private val homes: List<Home>
	private val homeConnections: List<HomeConnection>
	private val tiles: ShiftMatrix<Tile>

	{
		val players: MutableList<Player> = arrayListOf()
		val homes: MutableList<Home> = arrayListOf()
		for (i in 0..PLAYERS - 1) {
			val colour = Colour.values()[i]
			val player = Player(colour)
			val home = Home(colour, hashSetOf(player))
			players.add(player)
			homes.add(home)
		}
		this.players = players
		this.homes = homes

		val homeConnections: MutableList<HomeConnection> = arrayListOf()
		homeConnections.add(HomeConnection(0, 0, setOf(Direction.SOUTH, Direction.EAST)))
		homeConnections.add(HomeConnection(0, COLS - 1, setOf(Direction.SOUTH, Direction.WEST)))
		homeConnections.add(HomeConnection(ROWS - 1, COLS - 1, setOf(Direction.NORTH, Direction.WEST)))
		homeConnections.add(HomeConnection(ROWS - 1, 0, setOf(Direction.NORTH, Direction.EAST)))
		this.homeConnections = homeConnections

		tiles = ShiftMatrix<Tile>(ROWS, COLS)
		tiles[0, 0] = TileFactory.tee()
		tiles[1, 0] = TileFactory.straight().rotate(Rotation.NINETY_DEGREES)
		tiles[2, 0] = TileFactory.elbow().rotate(Rotation.ONE_HUNDRED_EIGHTY_DEGREES)
		tiles[0, 3] = TileFactory.tee().rotate(Rotation.NINETY_DEGREES)
		tiles[0, 2] = TileFactory.straight()
		tiles[0, 1] = TileFactory.elbow().rotate(Rotation.TWO_HUNDRED_SEVENTY_DEGREES)
		tiles[3, 3] = TileFactory.tee().rotate(Rotation.ONE_HUNDRED_EIGHTY_DEGREES)
		tiles[2, 3] = TileFactory.straight().rotate(Rotation.NINETY_DEGREES)
		tiles[1, 3] = TileFactory.elbow()
		tiles[3, 0] = TileFactory.tee().rotate(Rotation.TWO_HUNDRED_SEVENTY_DEGREES)
		tiles[3, 1] = TileFactory.straight()
		tiles[3, 2] = TileFactory.elbow().rotate(Rotation.NINETY_DEGREES)
		tiles[1, 1] = TileFactory.tower().rotate(Rotation.ONE_HUNDRED_EIGHTY_DEGREES)
		tiles[1, 2] = TileFactory.homework()
		tiles[2, 2] = TileFactory.potions()
		tiles[2, 1] = TileFactory.creatures().rotate(Rotation.TWO_HUNDRED_SEVENTY_DEGREES)
	}

	class object {
		val PLAYERS: Int = 4
		val ROWS: Int = 4
		val COLS: Int = 4
	}

	private fun findPlayerTile(player: Player): Index? {
		for (row in 0..ROWS - 1) {
			for (col in 0..COLS - 1) {
				if (tiles[row, col]?.players?.contains(player) == true) {
					return Index(row, col)
				}
			}
		}
		return null
	}

	public fun potentialMoves(): Set<Move> {
		var options: MutableSet<Move> = HashSet()
		options.add(NoMove())
		var player: Player = players[currentPlayer]
		addWalkMoves(options, player)
		return options
	}
	private fun addWalkMoves(options: MutableSet<Move>, player: Player): Unit {
		for (home : Home in homes) {
			if (player in home.players) {
				val homeConnection: HomeConnection = homeConnections[currentPlayer]
				val connecting: Tile? = tiles[homeConnection.row, homeConnection.col]
				if (connecting != null) {
					for (direction : Direction in homeConnection.edges) {
						if (connecting.connectsTo(direction.rotate(Rotation.ONE_HUNDRED_EIGHTY_DEGREES))) {
							options.add(WalkMove(player, direction, home, connecting))
						}

					}
				}
				return
			}
		}
		var index: Index = findPlayerTile(player)!!
		var tile: Tile = tiles[index.row, index.col]!!
		for (direction : Direction in tile.connections) {
			var connectedIndex: Index = direction.apply(index)
			if (valid(connectedIndex)) {
				var connectedTile: Tile = tiles[connectedIndex.row, connectedIndex.col]!!
				if (connectedTile.connectsTo(direction.rotate(Rotation.ONE_HUNDRED_EIGHTY_DEGREES))) {
					options.add(WalkMove(player, direction, tile, connectedTile))
				}
			}
		}
	}
	private fun valid(index: Index): Boolean {
		return index.row >= 0 && index.row < ROWS && index.col >= 0 && index.col < COLS
	}

	public fun print(out: Appendable) {
		for (row in 0..ROWS - 1) {
			for (col in 0..COLS - 1) {
				out.append(' ')
				out.append(if (tiles.get(row, col)?.connectsTo(Direction.NORTH) == true) 'X' else ' ')
				out.append(' ')
			}
			out.append("\n")
			for (col in 0..COLS - 1) {
				if (tiles.get(row, col) == null) {
					out.append(" / ")
				} else {
					out.append(if (tiles.get(row, col)?.connectsTo(Direction.WEST) == true) 'X' else ' ')
					out.append(if (tiles.get(row, col)?.objective != null) 'G' else 'X')
					out.append(if (tiles.get(row, col)?.connectsTo(Direction.EAST) == true) 'X' else ' ')
				}
			}
			out.append("\n")
			for (col in 0..COLS - 1) {
				out.append(' ')
				out.append(if (tiles.get(row, col)?.connectsTo(Direction.SOUTH) == true) 'X' else ' ')
				out.append(' ')
			}
			out.append("\n")
		}
	}

}