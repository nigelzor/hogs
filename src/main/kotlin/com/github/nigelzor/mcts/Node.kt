package com.github.nigelzor.mcts

import java.util.ArrayList
import java.util.HashSet

class Node<Move: Any>(val move: Move? = null, val parent: Node<Move>? = null, state: GameState<Move>) {
	val childNodes: MutableList<Node<Move>> = ArrayList()
	var wins = 0.0
	var visits = 0
	val untriedMoves = HashSet(state.possible())
	val playerJustMoved = state.playerJustMoved

	fun select(): Node<Move>? {
		return childNodes.maxBy { it.wins / it.visits + Math.sqrt(2 * Math.log(visits.toDouble()) / it.visits) }
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

	override fun toString(): String {
		return "[M:${move} W/V:${wins}/${visits}=${formatThree((100.0 * wins)/visits)}% U:${untriedMoves.size}]"
	}

}