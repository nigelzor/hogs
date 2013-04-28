package com.github.nigelzor.mcts

import java.util.Random

public trait GameState<Move> {
	val playerJustMoved: Int
	fun clone(): GameState<Move> // should return This (once implemented)
	fun apply(move: Move)
	fun possible(): Set<Move>
	fun randomMove(rng: Random): Move? {
		var possible = possible();
		if (possible.empty) {
			return null
		}
		return random(possible, rng)
	}
	fun result(playerJustMoved: Int): Double
}