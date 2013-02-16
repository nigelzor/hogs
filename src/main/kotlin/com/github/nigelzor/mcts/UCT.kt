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

val random = Random()
fun <T> random(values: Set<T>): T {
	return values.toList()[random.nextInt(values.size())]
}

/**
 * Conduct a UCT search for itermax iterations starting from rootstate.
 * Return the best move from the rootstate.
 * Assumes 2 alternating players (player 1 starts), with game results in the range [0.0, 1.0].
 */
fun <Move> UCT(rootstate: GameState<Move>, itermax: Int, verbose: Boolean = false): Move {
	val rootnode = Node(state = rootstate)

	for (i in 0..itermax) {
		var node = rootnode
		val state = rootstate.clone()

		// select
		while (node.untriedMoves.empty && !node.childNodes.empty) { // node is fully expanded and non-terminal
			node = node.select()!!
			state.apply(node.move!!)
		}

		// expand
		if (!node.untriedMoves.empty) { // node is non-terminal
			val move = random(node.untriedMoves)
			state.apply(move)
			node = node.add(move, state) // add child and descend tree
		}

		// rollout - this can often be made orders of magnitude quicker using a state.randomMove() function
		while (!state.possible().empty) {
			state.apply(random(state.possible()))
		}

		// backpropagate
		while (true) {
			node.update(state.result(node.playerJustMoved))
			val next = node.parentNode
			if (next == null) {
				break
			} else {
				node = next
			}
		}
	}

	// if (verbose) ...
	println(rootnode.childNodes.makeString("\n"));

	// return the most-visited node
	return rootnode.childNodes.sortBy { it.visits }.last!!.move!!

}

fun <T> playUCT(state: GameState<T>) {
	while (!state.possible().empty) {
		println(state)
		val itermax = if (state.playerJustMoved == 1) 10 else 10
		val m = UCT(rootstate = state, itermax = itermax, verbose = false)
		println("Best Move: ${m}")
		state.apply(m)
	}
	if (state.result(state.playerJustMoved) == 1.0)
		println("Player ${state.playerJustMoved} wins!")
	else if (state.result(state.playerJustMoved) == 0.0)
		println("Player ${3 - state.playerJustMoved} wins!")
	else
		println("Nobody wins!")
}

fun main(args: Array<String>) {
	playUCT(OXOState())
}