package com.github.nigelzor.mcts

/*
 * This is a very simple implementation of the UCT Monte Carlo Tree Search algorithm in Python 2.7.
 * The function UCT(rootstate, itermax, verbose = False) is towards the bottom of the code.
 * It aims to have the clearest and simplest possible code, and for the sake of clarity, the code
 * is orders of magnitude less efficient than it could be made, particularly by using a
 * state.GetRandomMove() or state.DoRandomRollout() function.
 *
 * Example GameState classes for Nim, OXO and Othello are included to give some idea of how you
 * can write your own GameState use UCT in your 2-player game. Change the game to be played in
 * the UCTPlayGame() function at the bottom of the code.
 *
 * Written by Peter Cowling, Ed Powley, Daniel Whitehouse (University of York, UK) September 2012.
 *
 * Licence is granted to freely use and distribute for any sensible/legal purpose so long as this comment
 * remains in any distributed code.
 *
 * For more information about Monte Carlo Tree Search check out our web site at www.mcts.ai
 */

import java.util.Random
import com.google.common.collect.Iterables

val random = Random()
fun <T: Any> random(values: Collection<T>, rng: Random = random): T {
	return Iterables.get(values, rng.nextInt(values.size))!!
}

fun <T> random(values: List<T>, rng: Random = random): T {
	return values[rng.nextInt(values.size)]
}

/**
 * Conduct a UCT search for itermax iterations starting from rootstate.
 * Return the best move from the rootstate.
 * Assumes 2 alternating players (player 1 starts), with game results in the range [0.0, 1.0].
 */
fun <Move: Any> UCT(rootstate: GameState<Move>, itermax: Int, verbose: Boolean = false): Move {
	val rootnode = Node(state = rootstate)

	for (i in 0..itermax) {
		var node = rootnode
		val state = rootstate.clone()

		// select
		while (node.untriedMoves.isEmpty() && !node.childNodes.isEmpty()) { // node is fully expanded and non-terminal
			node = node.select()!!
			state.apply(node.move!!)
		}

		// expand
		if (!node.untriedMoves.isEmpty()) { // node is non-terminal
			val move = random(node.untriedMoves)
			state.apply(move)
			node = node.add(move, state) // add child and descend tree
		}

		// rollout - this can often be made orders of magnitude quicker using a state.randomMove() function
		while (true) {
			val move = state.randomMove(random) ?: break
			state.apply(move)
		}

		// backpropagate
		while (true) {
			node.update(state.result(node.playerJustMoved))
			node = node.parent ?: break
		}
	}

	// return the most-visited node
	val sortedMoves = rootnode.childNodes.sortedBy { it.visits }

	if (verbose) {
		sortedMoves.takeLast(10).forEach(::println)
	}

	return sortedMoves.last().move!!
}

fun formatNanos(nanos: Long): String {
	if (nanos < 1000) {
		return "${formatThree(nanos.toDouble())} ns"
	}
	val micros = nanos / 1000.0
	if (micros < 1000) {
		return "${formatThree(micros)} us"
	}
	val millis = micros / 1000.0
	if (millis < 1000) {
		return "${formatThree(millis)} ms"
	}
	val secs = millis / 1000.0
	return "${formatThree(secs)} s"
}

fun formatThree(num: Double): String {
	if (num >= 100)
		return "%.0f".format(num)
	if (num >= 10)
		return "%.1f".format(num)
	if (num >= 1)
		return "%.2f".format(num)
	return "%1.3f".format(num)
}

fun <T: Any> playUCT(state: GameState<T>) {
	val startOfGame = System.nanoTime()
	var turn = 0
	sim@ do {
		while (!state.possible().isEmpty()) {
			println(state)
			val itermax = if (state.playerJustMoved == 1) 25000 else 10000
			val startOfTurn = System.nanoTime()
			val m = UCT(rootstate = state, itermax = itermax, verbose = true)
			println("Turn ${turn++} Best Move: ${m} in ${formatNanos(System.nanoTime() - startOfTurn)}")
			state.apply(m)
			if (turn > 50) {
				println("Out of time!")
				break@sim
			}
		}
		if (state.result(state.playerJustMoved) == 1.0)
			println("Player ${state.playerJustMoved} wins!")
		else if (state.result(state.playerJustMoved) == 0.0)
			println("Player ${3 - state.playerJustMoved} wins!")
		else
			println("Nobody wins!")
	} while (false)
	println("Total time: ${formatNanos(System.nanoTime() - startOfGame)}")
}

fun main(args: Array<String>) {
	playUCT(OXOState())
}