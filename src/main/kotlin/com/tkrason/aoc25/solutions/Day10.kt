package com.tkrason.aoc25.solutions

import com.tkrason.aoc25.Aoc25Days
import com.tkrason.aoc25.Solution
import com.tkrason.aoc25.util.getLogger
import org.slf4j.Logger

class Day10 : Solution {
	override val day: Aoc25Days = Aoc25Days.DAY_10

	override val logger: Logger = getLogger()

	data class Machine(
		val state: Int, // holding state
		val buttons: List<Int>,
		val clicks: Int,
	)

	override fun solvePart1(input: String) {

		val machines = input
			.lines()
			.map { it.split(" ").dropLast(1) }  // drop joltage
			.map {
				val startState = it.first().substring(1, it.first().length - 1) // drop []
				val buttonStrings = it.drop(1).map { it.substring(1, it.length - 1) } // drop ()

				val state = startState.foldIndexed(0) { index, acc, char ->
					if (char == '.') return@foldIndexed acc
					val value = 1 shl index

					acc + value
				}

				val buttons = buttonStrings.map {
					val numbers = it.split(",").map { it.toInt() }
					val stateToggleMask = numbers.fold(0) { acc, number ->
						val value = 1 shl number
						acc + value
					}
					stateToggleMask
				}

				Machine(
					state = state,
					buttons = buttons,
					clicks = 0,
				)
			}

		val clicks = machines.sumOf { computeFewestClicks(it) }

		require(clicks == 514)
	}

	private fun computeFewestClicks(startingMachine: Machine): Int {
		val queue = ArrayDeque<Machine>()
		queue.add(startingMachine)

		while (queue.isNotEmpty()) {
			val machine = queue.removeFirst()
			val newStates = machine.buttons.map { button ->

				val newState = machine.state xor button
				if (newState == 0) return machine.clicks + 1

				Machine(
					state = newState,
					buttons = machine.buttons,
					clicks = machine.clicks + 1
				)
			}

			queue.addAll(newStates)
		}

		error("Machine not computable!")
	}

	override fun solvePart2(input: String) {
		// TODO("Not yet implemented")
	}
}