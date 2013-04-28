package com.github.nigelzor.hogs

import java.util.HashSet
import java.util.ArrayList
import com.github.nigelzor.mcts.GameState
import java.util.Random
import com.github.nigelzor.mcts.random

public data class Board: GameState<Move> {
	override var playerJustMoved = 2 // TODO get rid of one of these
	public var currentPlayer: Int = 0

	private var players: List<Player>
	var homes: List<Home>
	private var homeConnections: List<HomeConnection>
	var tiles: ShiftMatrix<Tile>

	{
		players = Colour.values().map { Player(it) }
		// map { (i, player) -> ... } would be nice, but no
		homes = players.indices.mapTo(ArrayList<Home>(), { Home(players[it].colour, hashSetOf(it)) })
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
			if (hasWon(i)) {
				return if (i == pi) 1.0 else 0.0
			}
		}
		throw IllegalStateException()
	}

	private fun hasWon(player: Int): Boolean {
		return players[player].collected.size > 1 && homes[player].players.contains(player)
	}

	private fun playerIndexFromPJM(playerJustMoved: Int): Int {
		if (playerJustMoved == 1) return 0
		if (playerJustMoved == 2) return 2
		throw IllegalStateException()
	}

	override fun possible(): Set<Move> {
		for (i in players.indices) {
			if (hasWon(i)) {
				return hashSetOf(); // game is over
			}
		}

		var options = HashSet<Move>()
		options.add(NoMove.INSTANCE)
		options.addAll(addWalkMoves(currentPlayer))
		options.addAll(addRotateMoves())
		return options
	}

	override fun randomMove(rng: Random): Move? {
		for (i in players.indices) {
			if (hasWon(i)) {
				return null; // game is over
			}
		}

		var kind = rng.nextInt(13)
		if (kind < 6) {
			var options = addWalkMoves(currentPlayer)
			if (!options.empty) {
				return random(options, rng)
			}
		} else if (kind < 12) {
			var rotation = Rotation.values()[rng.nextInt(3) + 1] // disallow ZERO_DEGREES
			return RotateMove(Index(rng.nextInt(ROWS), rng.nextInt(COLS)), rotation)
		}
		return NoMove.INSTANCE
	}

	private fun addRotateMoves(): Set<Move> {
		var options = HashSet<Move>()
		for (row in 0..ROWS - 1) {
			for (col in 0..COLS - 1) {
				val index = Index(row, col)
				// TODO-NG: limit rotations for symmetric tiles
				options.add(RotateMove(index, Rotation.NINETY_DEGREES))
				options.add(RotateMove(index, Rotation.ONE_HUNDRED_EIGHTY_DEGREES))
				options.add(RotateMove(index, Rotation.TWO_HUNDRED_SEVENTY_DEGREES))
			}
		}
		return options
	}

	private fun addWalkMoves(player: Int): Set<Move> {
		fun canWalk(from: Set<Direction>, to: Set<Direction>): Boolean {
			return from.any { to.contains(it.rotate(Rotation.ONE_HUNDRED_EIGHTY_DEGREES)) }
		}

		var options = HashSet<Move>()
		if (player in homes[player].players) {
			val homeConnection = homeConnections[player]
			val connectedIndex = homeConnection.index
			val connectedTile = tiles[connectedIndex]
			if (connectedTile != null && canWalk(homeConnection.connections, connectedTile.connections)) {
				options.add(HomeToTileWalkMove(player, connectedIndex))
			}
		} else {
			val index = findPlayerTile(player)!!
			val tile = tiles[index]!!
			for (direction in tile.connections) {
				val connectedIndex = direction.apply(index)
				if (valid(connectedIndex)) {
					val connectedTile = tiles[connectedIndex]!!
					if (connectedTile.connectsTo(direction.rotate(Rotation.ONE_HUNDRED_EIGHTY_DEGREES))) {
						options.add(TileToTileWalkMove(index, connectedIndex))
					}
				}
			}
			val homeConnection = homeConnections[player]
			if (index == homeConnection.index && canWalk(tile.connections, homeConnection.connections)) {
				options.add(TileToHomeWalkMove(index, player))
			}
		}
		return options
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
		out.append("Players:")
		for (i in players.indices) {
			out.append(" %s=%s".format(i, players[i].collected))
		}
		out.append("\n")
		for (row in 0..ROWS - 1) {
			for (col in 0..COLS - 1) {
				val tile = tiles[row, col]
				out.append(if (tile?.players?.contains(0) == true) '0' else ' ')
				out.append(if (tile?.connectsTo(Direction.NORTH) == true) 'X' else ' ')
				out.append(if (tile?.players?.contains(1) == true) '1' else ' ')
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
				out.append(if (tile?.players?.contains(3) == true) '3' else ' ')
				out.append(if (tile?.connectsTo(Direction.SOUTH) == true) 'X' else ' ')
				out.append(if (tile?.players?.contains(2) == true) '2' else ' ')
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