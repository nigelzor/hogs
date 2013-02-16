package com.github.nigelzor.hogs

import java.util.HashSet
import com.github.nigelzor.mcts.GameState

public data class Board: GameState<Move> {
	override var playerJustMoved = 2 // TODO get rid of one of these
	public var currentPlayer: Int = 0

	private var players: List<Player>
	private var homes: List<Home>
	private var homeConnections: List<HomeConnection>
	private var tiles: ShiftMatrix<Tile>

	{
		players = Colour.values().map { Player(it) }
		// map { (i, player) -> ... } would be nice, but no
		homes = players.withIndices().map { Home(it.second.colour, hashSetOf(it.first)) }
		homeConnections = listOf(
				HomeConnection(0, 0, setOf(Direction.SOUTH, Direction.EAST)),
				HomeConnection(0, COLS - 1, setOf(Direction.SOUTH, Direction.WEST)),
				HomeConnection(ROWS - 1, COLS - 1, setOf(Direction.NORTH, Direction.WEST)),
				HomeConnection(ROWS - 1, 0, setOf(Direction.NORTH, Direction.EAST)))

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

	private fun findPlayerTile(player: Int): Index? {
		for (row in 0..ROWS - 1) {
			for (col in 0..COLS - 1) {
				if (tiles[row, col]?.players?.contains(player) == true) {
					return Index(row, col)
				}
			}
		}
		return null
	}

	override fun result(playerJustMoved: Int): Double {
		val pi = playerIndexFromPJM(playerJustMoved)
		for (i in players.indices) {
			if (players[i].collected.empty && homes[i].players.contains(players[i])) {
				return if (i == pi) 1.0 else 0.0
			}
		}
		throw IllegalStateException()
	}

	private fun playerIndexFromPJM(playerJustMoved: Int): Int {
		if (playerJustMoved == 1) return 0
		if (playerJustMoved == 2) return 2
		throw IllegalStateException()
	}

	override fun possible(): Set<Move> {
		var options: MutableSet<Move> = HashSet()
		for (i in players.indices) {
			if (players[i].collected.size < 4 && homes[i].players.contains(players[i])) {
				return options; // game is over
			}
		}

		//options.add(NoMove())
		addWalkMoves(options, currentPlayer)
		return options
	}

	private fun addWalkMoves(options: MutableSet<Move>, player: Int) {
		fun canWalk(from: Set<Direction>, to: Set<Direction>): Boolean {
			return from.any { to.contains(it.rotate(Rotation.ONE_HUNDRED_EIGHTY_DEGREES)) }
		}

		if (player in homes[player].players) {
			val homeConnection = homeConnections[player]
			val connectedIndex = homeConnection.index
			val connectedTile = tiles[connectedIndex]
			if (connectedTile != null && canWalk(homeConnection.connections, connectedTile.connections)) {
				options.add(WalkMove({ it.homes[player] }, { it.tiles[connectedIndex]!! }))
			}
		} else {
			val index = findPlayerTile(player)!!
			val tile = tiles[index]!!
			for (direction in tile.connections) {
				val connectedIndex = direction.apply(index)
				if (valid(connectedIndex)) {
					val connectedTile = tiles[connectedIndex]!!
					if (connectedTile.connectsTo(direction.rotate(Rotation.ONE_HUNDRED_EIGHTY_DEGREES))) {
						options.add(WalkMove({ it.tiles[index]!! }, { it.tiles[connectedIndex]!! }))
					}
				}
			}
			val homeConnection = homeConnections[player]
			if (index == homeConnection.index && canWalk(tile.connections, homeConnection.connections)) {
				options.add(WalkMove({ it.tiles[index]!! }, { it.homes[player] }))
			}
		}
	}

	private fun valid(index: Index): Boolean {
		return index.row >= 0 && index.row < ROWS && index.col >= 0 && index.col < COLS
	}

	override fun apply(move: Move) {
		playerJustMoved = 3 - playerJustMoved
		move.apply(this)
		currentPlayer = playerIndexFromPJM(playerJustMoved)
		for (row in 0..ROWS - 1) {
			for (col in 0..COLS - 1) {
				val tile = tiles[row, col]!!
				if (tile.objective != null) {
					for (player in tile.players) {
						players[player].collected.add(tile.objective)
					}
				}
			}
		}
	}

	override fun clone(): Board {
		val clone = Board()
		clone.playerJustMoved = playerJustMoved
		clone.currentPlayer = currentPlayer
		clone.players = players.map { it.clone() }
		clone.homes = homes.map { it.clone() }
		clone.tiles = tiles.clone({ it?.clone() })
		return clone
	}

	public fun print(out: Appendable) {
		for (row in 0..ROWS - 1) {
			for (col in 0..COLS - 1) {
				val tile = tiles[row, col]
				out.append(if (tile?.players?.contains(1) == true) '1' else ' ')
				out.append(if (tile?.connectsTo(Direction.NORTH) == true) 'X' else ' ')
				out.append(if (tile?.players?.contains(2) == true) '2' else ' ')
			}
			out.append("\n")
			for (col in 0..COLS - 1) {
				val tile = tiles[row, col]
				if (tile == null) {
					out.append(" / ")
				} else {
					out.append(if (tile?.connectsTo(Direction.WEST) == true) 'X' else ' ')
					out.append(if (tile?.objective != null) 'G' else 'X')
					out.append(if (tile?.connectsTo(Direction.EAST) == true) 'X' else ' ')
				}
			}
			out.append("\n")
			for (col in 0..COLS - 1) {
				val tile = tiles[row, col]
				out.append(if (tile?.players?.contains(4) == true) '4' else ' ')
				out.append(if (tile?.connectsTo(Direction.SOUTH) == true) 'X' else ' ')
				out.append(if (tile?.players?.contains(3) == true) '3' else ' ')
			}
			out.append("\n")
		}
	}

	fun toString(): String {
		val sb = StringBuilder()
		print(sb)
		return sb.toString()
	}

}