package com.github.nigelzor.hogs

import java.util.HashSet
import com.github.nigelzor.mcts.GameState
import java.util.Random
import com.github.nigelzor.mcts.random

import kotlin.Int
import kotlin.Int as BTile

data class Board(var homeConnections: List<HomeConnection>, var tiles: ShiftMatrix<Tile>): GameState<Move> {
	override var playerJustMoved = 2 // UCT player: { 1, 2 }

	val piToMove: Int // hogs player: { 0..3 }
		get() {
			if (playerJustMoved == 1) return 2
			if (playerJustMoved == 2) return 0
			throw IllegalStateException()
		}

	private var players = Colour.values().map { Player(it) }
	var homes = players.mapIndexed { i, player -> Home(player.colour, hashSetOf(i)) }

	companion object {
		val PLAYERS: Int = 4

		val BRAINDEAD: Int = -1

		fun defaultBoard(): Board {
			val homeConnections = listOf(
					HomeConnection(0, 0, BTiles.fromDirections(Direction.SOUTH, Direction.EAST)),
					HomeConnection(0, 3, BTiles.fromDirections(Direction.SOUTH, Direction.WEST)),
					HomeConnection(3, 3, BTiles.fromDirections(Direction.NORTH, Direction.WEST)),
					HomeConnection(3, 0, BTiles.fromDirections(Direction.NORTH, Direction.EAST)))

			val tiles = ShiftMatrix.empty<Tile>(4, 4)
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
		val pi: Int
		if (playerJustMoved == 1) pi = 0
		else if (playerJustMoved == 2) pi = 2
		else throw IllegalStateException()

		for (i in players.indices) {
			if (hasWon(i)) {
				return if (i == pi) 1.0 else 0.0
			}
		}
		throw IllegalStateException()
	}

	private fun hasWon(player: Int): Boolean {
		return players[player].collected.size == 4 && homes[player].players.contains(player)
	}

	override fun possible(): Set<Move> {
		for (i in players.indices) {
			if (hasWon(i)) {
				return hashSetOf() // game is over
			}
		}

		if (piToMove == BRAINDEAD) return setOf(NoMove.INSTANCE)

		val options = HashSet<Move>()
		addMapWalkMoves(piToMove, options)
		addRotateWalkMoves(piToMove, options)
		addLiftWalkMoves(piToMove, options)
		return options
	}

	override fun randomMove(rng: Random): Move? {
		for (i in players.indices) {
			if (hasWon(i)) {
				return null // game is over
			}
		}

		if (piToMove == BRAINDEAD) return NoMove.INSTANCE

		val kind = rng.nextInt(3)
		if (kind == 0) {
			val options = addWalkMoves(piToMove, true) // should never be empty, since we've got the map
			val walkMove = random(options, rng)
			return andThenWalkRandomly(rng, walkMove)
		} else if (kind == 1) {
			val rotation = Rotation.values()[rng.nextInt(3) + 1] // disallow ZERO_DEGREES
			var index: Index
			do {
				index = random(tiles.indicies, rng)
			} while (!okToRotate(index)) // "you may not rotate classrooms"
			val rotateMove = RotateMove(index, rotation)
			return andThenWalkRandomly(rng, rotateMove)
		} else if (kind == 2) {
			var index: Index
			do {
				index = random(tiles.indicies, rng)
			} while (!okToLift(index))
			var row = index.row
			var col = index.col
			if (rng.nextBoolean()) {
				row = rng.nextInt(tiles.rows - 1)
				if (row >= index.row) row += 1
			} else {
				col = rng.nextInt(tiles.cols - 1)
				if (col >= index.col) col += 1
			}
			val liftMove = LiftMove(index, Index(row, col))
			return andThenWalkRandomly(rng, liftMove)
		}
		return NoMove.INSTANCE
	}

	private fun andThenWalkRandomly(rng: Random, firstMove: Move): Move {
		// 5% chance to stop after the first step
		// this is not equally-weighted, but faster to skip determining how many other options there are
		if (rng.nextInt(20) != 0) {
			val b2 = clone()
			firstMove.apply(b2)
			val allWalks = b2.addWalkMoves(piToMove)
			if (!allWalks.isEmpty()) {
				return CompositeMove(listOf(firstMove, random(allWalks, rng)))
			}
		}
		return firstMove
	}

	private fun addMapWalkMoves(player: Int, options: MutableSet<Move>) {
		val allWalks = addWalkMoves(player, true)
		for (walkMove in allWalks) {
			andThenWalk(player, walkMove, options)
		}
	}

	private fun addRotateWalkMoves(player: Int, options: MutableSet<Move>) {
		val allRotations = addRotateMoves()
		for (rotateMove in allRotations) {
			andThenWalk(player, rotateMove, options)
		}
	}

	private fun addLiftWalkMoves(player: Int, options: MutableSet<Move>) {
		val allLifts = addLiftMoves()
		for (shiftMove in allLifts) {
			andThenWalk(player, shiftMove, options)
		}
	}

	private fun andThenWalk(player: Int, firstMove: Move, options: MutableSet<Move>) {
		options.add(firstMove) // "you may also choose to stay where you are"

		val b2 = clone()
		firstMove.apply(b2)
		val allWalks = b2.addWalkMoves(player)
		for (walkMove in allWalks) {
			options.add(CompositeMove(listOf(firstMove, walkMove)))
		}
	}

	private fun addRotateMoves(): Set<RotateMove> {
		val options = HashSet<RotateMove>()
		for (index in tiles.indicies) {
			// TODO-NG: limit rotations for symmetric tiles
			if (okToRotate(index)) { // "you may not rotate classrooms"
				options.add(RotateMove(index, Rotation.NINETY_DEGREES))
				options.add(RotateMove(index, Rotation.ONE_HUNDRED_EIGHTY_DEGREES))
				options.add(RotateMove(index, Rotation.TWO_HUNDRED_SEVENTY_DEGREES))
			}
		}
		return options
	}

	private fun addLiftMoves(): Set<LiftMove> {
		val options = HashSet<LiftMove>()
		for (index in tiles.indicies) {
			// "you may not lift classrooms"
			// "you may not lift staircases with characters on them"
			if (okToLift(index)) {
				for (row in 0 until tiles.rows) {
					if (row != index.row) {
						options.add(LiftMove(index, Index(row, index.col)))
					}
				}
				for (col in 0 until tiles.cols) {
					if (col != index.col) {
						options.add(LiftMove(index, Index(index.row, col)))
					}
				}
			}
		}
		return options
	}

	private fun okToLift(index: Index) = tiles[index]!!.players.isEmpty() && okToRotate(index)

	private fun okToRotate(index: Index) = tiles[index]!!.objective == null

	/**
	 * only applicable for board-home transitions
	 * tile-to-tile walks need to consider the relative position of the tiles (my NORTH doesn't match your SOUTH unless
	 * you're directly below me).
	 */
	private fun canWalk(from: BTile, to: BTile): Boolean {
		return (from and to and 0x0F) != 0
	}

	private fun addHomeToTileWalkMoves(player: Int, homeConnection: HomeConnection, sneak: Boolean = false, options: MutableCollection<Move>) {
		val connectedIndex = homeConnection.index
		val connectedTile = tiles[connectedIndex]
		if (connectedTile != null && (sneak || canWalk(homeConnection.connections, connectedTile.connections))) {
			options.add(HomeToTileWalkMove(player, connectedIndex))
		}
	}

	private fun addTileToTileWalkMoves(index: Index, sneak: Boolean = false, options: MutableCollection<Move>) {
		if (sneak) {
			for (direction in Direction.values()) {
				val connectedIndex = direction.apply(index)
				if (valid(connectedIndex)) {
					options.add(TileToTileWalkMove(index, connectedIndex))
				}
			}
		} else {
			val tile = tiles[index]!!
			BTiles.eachDirection(tile.connections) { direction ->
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

	private fun addTileToHomeWalkMoves(player: Int, index: Index, sneak: Boolean = false, options: MutableCollection<Move>) {
		val tile = tiles[index]!!
		val homeConnection = homeConnections[player]
		if (index == homeConnection.index && (sneak || canWalk(tile.connections, homeConnection.connections))) {
			options.add(TileToHomeWalkMove(index, player))
		}
	}

	private fun addWalkMoves(player: Int, sneak: Boolean = false): Set<Move> {
		val options = HashSet<Move>()
		if (player in homes[player].players) {
			addHomeToTileWalkMoves(player, homeConnections[player], sneak, options)
		} else {
			val index = findPlayerTile(player)!!
			addTileToTileWalkMoves(index, sneak, options)
			addTileToHomeWalkMoves(player, index, sneak, options)
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

	fun print(out: Appendable) {
		out.append("Players:")
		for (i in players.indices) {
			out.append(" %s=%s".format(i, players[i].collected))
		}
		out.append("\n")
		for (row in 0 until tiles.rows) {
			for (col in 0 until tiles.cols) {
				val tile = tiles[row, col]
				out.append(if (tile?.players?.contains(0) == true) '0' else ' ')
				out.append(if (tile?.connectsTo(Direction.NORTH) == true) '║' else ' ')
				out.append(if (tile?.players?.contains(1) == true) '1' else ' ')
			}
			out.append("\n")
			for (col in 0 until tiles.cols) {
				val tile = tiles[row, col]
				if (tile == null) {
					out.append(" / ")
				} else {
					out.append(if (tile.connectsTo(Direction.WEST)) '═' else ' ')
					out.append(if (tile.objective != null) 'G' else '╬')
					out.append(if (tile.connectsTo(Direction.EAST)) '═' else ' ')
				}
			}
			out.append("\n")
			for (col in 0 until tiles.cols) {
				val tile = tiles[row, col]
				out.append(if (tile?.players?.contains(3) == true) '3' else ' ')
				out.append(if (tile?.connectsTo(Direction.SOUTH) == true) '║' else ' ')
				out.append(if (tile?.players?.contains(2) == true) '2' else ' ')
			}
			out.append("\n")
		}
	}

	override fun toString(): String {
		val sb = StringBuilder()
		print(sb)
		return sb.toString()
	}

}