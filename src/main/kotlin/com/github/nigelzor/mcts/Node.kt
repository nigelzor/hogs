package com.github.nigelzor.mcts

import java.util.ArrayList
import java.util.HashSet

public class Node<Move>(move: Move? = null, parent: Node<Move>? = null, state: GameState<Move>) {
	val move = move
	val parentNode = parent
	val childNodes: MutableList<Node<Move>> = ArrayList()
	var wins = 0.0
	var visits = 0
	val untriedMoves: MutableSet<Move> = HashSet(state.possible())
	val playerJustMoved = state.playerJustMoved

	fun select(): Node<Move>? {
		return childNodes.sortBy { it.wins / it.visits + Math.sqrt(2 * Math.log(visits.toDouble()) / it.visits) }.last
	}

	fun add(move: Move, state: GameState<Move>): Node<Move> {
		val node = Node(move, this, state)
		untriedMoves.remove(move)
		childNodes.add(node)
		return node
	}

	fun update(result: Double) {
		visits++
		wins += result
	}

	fun toString(): String {
		return "[M:${move} W/V:${wins}/${visits} U:${untriedMoves}]"
	}

}