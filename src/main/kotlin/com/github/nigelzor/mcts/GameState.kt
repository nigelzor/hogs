package com.github.nigelzor.mcts

import java.util.Random

interface GameState<Move: Any> {
	val playerJustMoved: Int
	fun clone(): GameState<Move> // should return This (once implemented)
	fun apply(move: Move)
	fun possible(): Set<Move>
	fun randomMove(rng: Random): Move? {
		val possible = possible()
		if (possible.isEmpty()) {
			return null
		}
		return random(possible, rng)
	}
	fun result(playerJustMoved: Int): Double
}