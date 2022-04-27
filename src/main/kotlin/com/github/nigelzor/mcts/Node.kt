package com.github.nigelzor.mcts

import java.util.ArrayList
import java.util.HashSet

class Node<M: Any, P: Any>(val move: M? = null, val parent: Node<M, P>? = null, state: GameState<M, P>) {
	val useWeights = state.nextMoveIsRandom()
	val childNodes: MutableList<Node<M, P>> = ArrayList()
	var wins = 0.0
	var visits = 0
	val untriedMoves = HashSet(state.possible())
	val playerJustMoved = state.playerJustMoved

	fun select(): Node<M, P> {
		if (useWeights) {
			return selectByWeight()
		}
		return childNodes.maxByOrNull { it.wins / it.visits + Math.sqrt(2 * Math.log(visits.toDouble()) / it.visits) }!!
	}

	fun selectByWeight(): Node<M, P> {
		var d = random.nextDouble()
		for (c in childNodes) {
			val w = (c.move as Weighted).weight
			if (d <= w) {
				return c
			}
			d -= w
		}
		throw IllegalStateException("$d weight remaining, but no more child nodes")
	}

	fun add(move: M, state: GameState<M, P>): Node<M, P> {
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