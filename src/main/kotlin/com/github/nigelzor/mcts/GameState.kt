package com.github.nigelzor.mcts

public trait GameState<Move> {
	val playerJustMoved: Int
	fun clone(): GameState<Move> // should return This (once implemented)
	fun apply(move: Move)
	fun possible(): Set<Move>
	fun result(playerJustMoved: Int): Double
}