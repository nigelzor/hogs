package com.github.nigelzor.hogs

import java.util.HashSet
import java.util.ArrayList
import com.github.nigelzor.mcts.GameState
import java.util.Random
import com.github.nigelzor.mcts.random

public data class Board(var homeConnections: List<HomeConnection>, var tiles: ShiftMatrix<Tile>): GameState<Move> {
	override var playerJustMoved = 2 // UCT player: { 1, 2 }

	val piToMove: Int // hogs player: { 0..3 }
		get() {
			if (playerJustMoved == 1) return 2
			if (playerJustMoved == 2) return 0
			throw IllegalStateException()
		}

	private var players: List<Player>
	var homes: List<Home>
	{
		players = Colour.values().map { Player(it) }
		// map { (i, player) -> ... } would be nice, but no
		homes = players.indices.mapTo(ArrayList<Home>(), { Home(players[it].colour, hashSetOf(it)) })
	}

	class object {
		val PLAYERS: Int = 4

		val BRAINDEAD: Int = -1

		fun tinyBoard(): Board {
			val homeConnections = listOf(
					HomeConnection(0, 0, setOf(Direction.SOUTH, Direction.EAST)),
					HomeConnection(0, 1, setOf(Direction.SOUTH, Direction.WEST)),
					HomeConnection(1, 1, setOf(Direction.NORTH, Direction.WEST)),
					HomeConnection(1, 0, setOf(Direction.NORTH, Direction.EAST)))

			val tiles = ShiftMatrix<Tile>(2, 2)
			tiles[0, 0] = TileFactory.tower().rotate(Rotation.ONE_HUNDRED_EIGHTY_DEGREES)
			tiles[0, 1] = TileFactory.homework()
			tiles[1, 1] = TileFactory.potions()
			tiles[1, 0] = TileFactory.creatures().rotate(Rotation.TWO_HUNDRED_SEVENTY_DEGREES)

			return Board(homeConnections, tiles)
		}

		fun defaultBoard(): Board {
			val homeConnections = listOf(
					HomeConnection(0, 0, setOf(Direction.SOUTH, Direction.EAST)),
					HomeConnection(0, 3, setOf(Direction.SOUTH, Direction.WEST)),
					HomeConnection(3, 3, setOf(Direction.NORTH, Direction.WEST)),
					HomeConnection(3, 0, setOf(Direction.NORTH, Direction.EAST)))

			val tiles = ShiftMatrix<Tile>(4, 4)
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

			return Board(homeConnections, tiles)
		}
	}

	private fun findPlayerTile(player: Int): Index? {
		for (index in tiles.indicies) {
			if (tiles[index]?.players?.contains(player) == true) {
				return index
			}
		}
		return null
	}

	override fun result(playerJustMoved: Int): Double {
		val pi = if (playerJustMoved == 1) 0
			else if (playerJustMoved == 2) 2
			else throw IllegalStateException()

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

	override fun possible(): Set<Move> {
		for (i in players.indices) {
			if (hasWon(i)) {
				return hashSetOf(); // game is over
			}
		}

		var options = HashSet<Move>()
		options.add(NoMove.INSTANCE)
		if (piToMove == BRAINDEAD) return options
		options.addAll(addWalkMoves(piToMove))
		options.addAll(addRotateMoves())
		return options
	}

	override fun randomMove(rng: Random): Move? {
		for (i in players.indices) {
			if (hasWon(i)) {
				return null; // game is over
			}
		}

		if (piToMove == BRAINDEAD) return NoMove.INSTANCE

		var kind = rng.nextInt(13)
		if (kind < 6) {
			var options = addWalkMoves(piToMove)
			if (!options.empty) {
				return random(options, rng)
			}
		} else if (kind < 12) {
			var rotation = Rotation.values()[rng.nextInt(3) + 1] // disallow ZERO_DEGREES
			return RotateMove(random(tiles.indicies, rng), rotation)
		}
		return NoMove.INSTANCE
	}

	private fun addRotateMoves(): Set<Move> {
		var options = HashSet<Move>()
		for (index in tiles.indicies) {
			// TODO-NG: limit rotations for symmetric tiles
			options.add(RotateMove(index, Rotation.NINETY_DEGREES))
			options.add(RotateMove(index, Rotation.ONE_HUNDRED_EIGHTY_DEGREES))
			options.add(RotateMove(index, Rotation.TWO_HUNDRED_SEVENTY_DEGREES))
		}
		return options
	}

	private fun canWalk(from: Set<Direction>, to: Set<Direction>): Boolean {
		return from.any { to.contains(it.rotate(Rotation.ONE_HUNDRED_EIGHTY_DEGREES)) }
	}

	private fun addWalkMoves(player: Int, sneak: Boolean = false): Set<Move> {

		fun addHomeToTileWalkMoves(homeConnection: HomeConnection, options: MutableCollection<Move>) {
			val connectedIndex = homeConnection.index
			val connectedTile = tiles[connectedIndex]
			if (connectedTile != null && (sneak || canWalk(homeConnection.connections, connectedTile.connections))) {
				options.add(HomeToTileWalkMove(player, connectedIndex))
			}
		}

		fun addTileToTileWalkMoves(index: Index, options: MutableCollection<Move>) {
			if (sneak) {
				for (direction in Direction.values()) {
					val connectedIndex = direction.apply(index)
					if (valid(connectedIndex)) {
						options.add(TileToTileWalkMove(index, connectedIndex))
					}
				}
			} else {
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
			}
		}

		fun addTileToHomeWalkMoves(index: Index, options: MutableCollection<Move>) {
			val tile = tiles[index]!!
			val homeConnection = homeConnections[player]
			if (index == homeConnection.index && (sneak || canWalk(tile.connections, homeConnection.connections))) {
				options.add(TileToHomeWalkMove(index, player))
			}
		}

		var options = HashSet<Move>()
		if (player in homes[player].players) {
			addHomeToTileWalkMoves(homeConnections[player], options)
		} else {
			val index = findPlayerTile(player)!!
			addTileToTileWalkMoves(index, options)
			addTileToHomeWalkMoves(index, options)
		}
		return options
	}

	private fun valid(index: Index): Boolean {
		return tiles.contains(index)
	}

	override fun apply(move: Move) {
		move.apply(this)
		playerJustMoved = 3 - playerJustMoved
		for (index in tiles.indicies) {
			val tile = tiles[index]!!
			if (tile.objective != null) {
				for (player in tile.players) {
					players[player].collected.add(tile.objective)
				}
			}
		}
	}

	override fun clone(): Board {
		val clone = Board(homeConnections, tiles.clone { it?.clone() })
		clone.playerJustMoved = playerJustMoved
		clone.players = players.map { it.clone() }
		clone.homes = homes.map { it.clone() }
		return clone
	}

	public fun print(out: Appendable) {
		out.append("Players:")
		for (i in players.indices) {
			out.append(" %s=%s".format(i, players[i].collected))
		}
		out.append("\n")
		for (row in tiles.rows.indices) {
			for (col in tiles.cols.indices) {
				val tile = tiles[row, col]
				out.append(if (tile?.players?.contains(0) == true) '0' else ' ')
				out.append(if (tile?.connectsTo(Direction.NORTH) == true) 'X' else ' ')
				out.append(if (tile?.players?.contains(1) == true) '1' else ' ')
			}
			out.append("\n")
			for (col in tiles.cols.indices) {
				val tile = tiles[row, col]
				if (tile == null) {
					out.append(" / ")
				} else {
					out.append(if (tile.connectsTo(Direction.WEST) == true) 'X' else ' ')
					out.append(if (tile.objective != null) 'G' else 'X')
					out.append(if (tile.connectsTo(Direction.EAST) == true) 'X' else ' ')
				}
			}
			out.append("\n")
			for (col in tiles.cols.indices) {
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