package com.github.nigelzor.hogs

import java.util.HashSet
import com.github.nigelzor.mcts.GameState
import java.util.Random
import com.github.nigelzor.mcts.random

import kotlin.Int
import kotlin.Int as BTile


data class Board(var players: MutableList<Player>, var homes: MutableList<Home>, var tiles: ShiftMatrix<BTile>, var step: Step, var rolled: Rolled): GameState<Move> {
	override val playerJustMoved: Int // UCT player: { 1, 2 }
		get() = when (step.player.ordinal) {
			0 -> 2
			2 -> 1
			else -> throw IllegalStateException()
		}

	val piToMove: Int // hogs player: { 0..3 }
		get() = step.player.ordinal

	companion object {
		val POSSIBLE_ROLLS = setOf(RollMove(Rolled.MAP), RollMove(Rolled.ROTATE), RollMove(Rolled.LIFT))

		fun defaultBoard(): Board {
			val players = Colour.values().map { Player(it) }.toMutableList()

			val homes = listOf(
					Home(0, 0, BTiles.fromDirections(Direction.SOUTH, Direction.EAST)),
					Home(0, 3, BTiles.fromDirections(Direction.SOUTH, Direction.WEST)),
					Home(3, 3, BTiles.fromDirections(Direction.NORTH, Direction.WEST)),
					Home(3, 0, BTiles.fromDirections(Direction.NORTH, Direction.EAST)))
					.mapIndexed { i, home -> home.with(players[i]) }
					.toMutableList()

			val tiles = ShiftMatrix.empty<BTile>(4, 4)
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

			return Board(players, homes, tiles, RollStep(players.first().colour), Rolled.MAP)
		}
	}

	enum class Rolled {
		MAP, ROTATE, LIFT
	}

	interface Step {
		val player: Colour
	}

	data class RollStep(override val player: Colour): Step

	data class ActStep(override val player: Colour): Step

	data class MoveStep(override val player: Colour): Step

	fun next(step: Step): Step {
		return when (step) {
			is RollStep -> ActStep(step.player)
			is ActStep -> MoveStep(step.player)
			is MoveStep -> RollStep(nextPlayer(step.player))
			else -> throw IllegalStateException()
		}
	}

	private fun nextPlayer(player: Colour): Colour {
		return Colour.values()[when (player.ordinal) {
			0 -> 2
			2 -> 0
			else -> throw IllegalStateException()
		}]
	}

	private fun findPlayerTile(player: Int): Index? {
		for (index in tiles.indicies) {
			if (tiles[index]?.contains(players[player]) == true) {
				return index
			}
		}
		return null
	}

	override fun result(playerJustMoved: Int): Double {
		val pi = when (playerJustMoved) {
			1 -> 0
			2 -> 2
			else -> throw IllegalStateException()
		}

		for (i in players.indices) {
			if (hasWon(i)) {
				return if (i == pi) 1.0 else 0.0
			}
		}
		throw IllegalStateException()
	}

	private fun hasWon(player: Int): Boolean {
		return players[player].collectedEverything() && homes[player].contains(players[player])
	}

	override fun possible(): Set<Move> {
		for (i in players.indices) {
			if (hasWon(i)) {
				return setOf() // game is over
			}
		}

		if (step is RollStep) {
			return POSSIBLE_ROLLS
		}

		if (step is ActStep) {
			if (rolled == Rolled.MAP) {
				return possibleWalkMoves(piToMove, true)
			}
			if (rolled == Rolled.ROTATE) {
				return possibleRotateMoves()
			}
			if (rolled == Rolled.LIFT) {
				return possibleLiftMoves()
			}
		}

		if (step is MoveStep) {
			// "you may also choose to stay where you are"
			return possibleWalkMoves(piToMove, false) + NoMove.INSTANCE
		}

		throw IllegalStateException("unhandled step $step")
	}

	override fun randomMove(rng: Random): Move? {
		for (i in players.indices) {
			if (hasWon(i)) {
				return null // game is over
			}
		}

		// FIXME-NG: use correct distribution
		if (step is RollStep) {
			return random(POSSIBLE_ROLLS, rng)
		}

		if (step is ActStep) {
			if (rolled == Rolled.MAP) {
				return random(possibleWalkMoves(piToMove, true), rng)
			}
			if (rolled == Rolled.ROTATE) {
				val rotation = Rotation.values()[rng.nextInt(3) + 1] // disallow ZERO_DEGREES
				var index: Index
				do {
					index = random(tiles.indicies, rng)
				} while (!okToRotate(index)) // "you may not rotate classrooms"
				return RotateMove(index, rotation)
			}
			if (rolled == Rolled.LIFT) {
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
				return LiftMove(index, Index(row, col))
			}
		}

		if (step is MoveStep) {
			// "you may also choose to stay where you are"
			if (rng.nextInt(20) == 0) {
				return NoMove.INSTANCE
			}
			val possibleWalkMoves = possibleWalkMoves(piToMove, false)
			if (possibleWalkMoves.isEmpty()) {
				return NoMove.INSTANCE
			}
			return random(possibleWalkMoves, rng)
		}

		throw IllegalStateException("unhandled step $step")
	}

	fun possibleRotateMoves(): Set<RotateMove> {
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

	fun possibleLiftMoves(): Set<LiftMove> {
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

	private fun okToLift(index: Index) = tiles[index]!!.containsNoPlayers() && okToRotate(index)

	private fun okToRotate(index: Index) = tiles[index]!!.containsNoObjectives()

	/**
	 * only applicable for board-home transitions
	 * tile-to-tile walks need to consider the relative position of the tiles (my NORTH doesn't match your SOUTH unless
	 * you're directly below me).
	 */
	private fun canWalk(from: BTile, to: BTile): Boolean {
		return (from.rotate(Rotation.ONE_HUNDRED_EIGHTY_DEGREES) and to and 0x0F) != 0
	}

	private fun addHomeToTileWalkMoves(player: Int, homeConnection: Home, sneak: Boolean = false, options: MutableCollection<Move>) {
		val connectedIndex = homeConnection.index
		val connectedTile = tiles[connectedIndex]
		if (connectedTile != null && (sneak || canWalk(homeConnection.tile, connectedTile))) {
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
			BTiles.eachDirection(tile) { direction ->
				val connectedIndex = direction.apply(index)
				if (valid(connectedIndex)) {
					val connectedTile = tiles[connectedIndex]!!
					if (connectedTile.contains(direction.rotate(Rotation.ONE_HUNDRED_EIGHTY_DEGREES))) {
						options.add(TileToTileWalkMove(index, connectedIndex))
					}
				}
			}
		}
	}

	private fun addTileToHomeWalkMoves(player: Int, index: Index, sneak: Boolean = false, options: MutableCollection<Move>) {
		val tile = tiles[index]!!
		val homeConnection = homes[player]
		if (index == homeConnection.index && (sneak || canWalk(tile, homeConnection.tile))) {
			options.add(TileToHomeWalkMove(index, player))
		}
	}

	fun possibleWalkMoves(player: Int, sneak: Boolean = false): Set<Move> {
		val options = HashSet<Move>()
		if (homes[player].contains(players[player])) {
			addHomeToTileWalkMoves(player, homes[player], sneak, options)
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
		step = next(step)
	}

	override fun clone(): Board {
		return copy(players=players.toMutableList(), homes=homes.toMutableList(), tiles=tiles.clone())
	}

	fun print(out: Appendable) {
		out.append("Step: $step\n")
		out.append("Players:")
		players.forEach { player ->
			out.append(" ${player.colour}=")
			if (player.collected.contains(Objective.ONE)) out.append('1')
			if (player.collected.contains(Objective.TWO)) out.append('2')
			if (player.collected.contains(Objective.THREE)) out.append('3')
			if (player.collected.contains(Objective.FOUR)) out.append('4')
		}
		out.append("\n")
		for (row in 0 until tiles.rows) {
			for (col in 0 until tiles.cols) {
				val tile = tiles[row, col]
				out.append(if (tile?.contains(Colour.BLUE) == true) 'B' else ' ')
				out.append(if (tile?.contains(Direction.NORTH) == true) '║' else ' ')
				out.append(if (tile?.contains(Colour.YELLOW) == true) 'Y' else ' ')
			}
			out.append("\n")
			for (col in 0 until tiles.cols) {
				val tile = tiles[row, col]
				if (tile == null) {
					out.append(" / ")
				} else {
					out.append(if (tile.contains(Direction.WEST)) '═' else ' ')
					// I wanted to use ①②③④, but they're double-wide
					out.append(when {
						tile.contains(Objective.ONE) -> '1'
						tile.contains(Objective.TWO) -> '2'
						tile.contains(Objective.THREE) -> '3'
						tile.contains(Objective.FOUR) -> '4'
						else -> '╬'
					})
					out.append(if (tile.contains(Direction.EAST)) '═' else ' ')
				}
			}
			out.append("\n")
			for (col in 0 until tiles.cols) {
				val tile = tiles[row, col]
				out.append(if (tile?.contains(Colour.GREEN) == true) 'G' else ' ')
				out.append(if (tile?.contains(Direction.SOUTH) == true) '║' else ' ')
				out.append(if (tile?.contains(Colour.RED) == true) 'R' else ' ')
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