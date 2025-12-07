package com.tkrason.aoc25.solutions

import com.tkrason.aoc25.Aoc25Days
import com.tkrason.aoc25.Solution
import com.tkrason.aoc25.util.getLogger
import org.slf4j.Logger

private const val SOURCE = 'S'
private const val BEAM = '|'
private const val EMPTY_SPACE = '.'
private const val SPLITTER = '^'

class Day7 : Solution {
	override val day: Aoc25Days = Aoc25Days.DAY_7

	override val logger: Logger = getLogger()

	override fun solvePart1(input: String) {
		val lines = input.lines()
		val emptyState = lines[1].toCharArray() // full of .

		val linesWithSplitters = lines.filterIndexed { index, _ -> index % 2 == 0 }

		val (_, sum) = linesWithSplitters.fold(emptyState to 0) { (state, splitCount), lineWithSplitters ->
			val updatedState = state.copyOf()

			val newSplitsCount = lineWithSplitters.foldIndexed(0) { index, acc, action ->
				val stateAbove = state[index]

				// continuation of state above
				if (action == EMPTY_SPACE) return@foldIndexed acc

				if (action == SOURCE) {
					updatedState[index] = BEAM
					return@foldIndexed acc
				}

				if (action == SPLITTER) {
					if(stateAbove == BEAM) {
						updatedState[index-1] = BEAM
						updatedState[index] = EMPTY_SPACE
						updatedState[index+1] = BEAM

						return@foldIndexed acc + 1
					}
				}

				return@foldIndexed acc
			}

			updatedState to splitCount + newSplitsCount
		}

		require(sum == 1649)
	}

	override fun solvePart2(input: String) {
		TODO("Not yet implemented")
	}
}