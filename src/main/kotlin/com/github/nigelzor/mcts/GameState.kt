package com.github.nigelzor.mcts

import java.util.Random

interface GameState<Move: Any, Player: Any> {
	val playerJustMoved: Player
	fun clone(): GameState<Move, Player> // should return This (once implemented)
	fun apply(move: Move)
	fun nextMoveIsRandom(): Boolean = false
	fun possible(): Set<Move>
	fun randomMove(rng: Random): Move? {
		val possible = possible()
		if (possible.isEmpty()) {
			return null
		}
		return random(possible, rng)
	}
	fun result(playerJustMoved: Player): Double
}