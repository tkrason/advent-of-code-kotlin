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
					if (stateAbove == BEAM) {
						updatedState[index - 1] = BEAM
						updatedState[index] = EMPTY_SPACE
						updatedState[index + 1] = BEAM

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
		val lines = input.lines()
		val emptyState = lines[1].toCharArray() // full of .
		val linesWithSplitters = lines.filterIndexed { index, _ -> index % 2 == 0 }

		// TODO: A lot of space for optimization by not copying arrays
		val sum = countTimelines(
			linesWithSplitters = linesWithSplitters,
			previousState = emptyState,
			cache = mutableMapOf()
		)

		require(sum == 16937871060075L)
	}

	private fun countTimelines(
		linesWithSplitters: List<String>,
		previousState: CharArray,
		cache: MutableMap<Pair<Int, Int>, Long>,
	): Long {

		if (linesWithSplitters.isEmpty()) return 1L

		val currentLine = linesWithSplitters.first()
		val depth = linesWithSplitters.size

		val sum = currentLine.mapIndexed { actionIndex, action ->
			if (action == EMPTY_SPACE) {
				val stateAbove = previousState[actionIndex]

				return@mapIndexed if (stateAbove == BEAM) {
					val cacheKey = depth to actionIndex
					cache.getOrPut(cacheKey) {
						countTimelines(
							linesWithSplitters = linesWithSplitters.drop(1),
							previousState = previousState.copyOf(),
							cache = cache
						)
					}
				} else 0L
			}

			if (action == SOURCE) {
				val newState = previousState.copyOf()
				newState[actionIndex] = BEAM
				return@mapIndexed countTimelines(
					linesWithSplitters = linesWithSplitters.drop(1),
					previousState = newState,
					cache = cache,
				)
			}

			if (action == SPLITTER) {
				val stateAbove = previousState[actionIndex]
				if (stateAbove == BEAM) {
					val leftTimeline = previousState.copyOf()
					val rightTimeline = previousState.copyOf()

					leftTimeline[actionIndex - 1] = BEAM
					leftTimeline[actionIndex] = EMPTY_SPACE

					rightTimeline[actionIndex] = EMPTY_SPACE
					rightTimeline[actionIndex + 1] = BEAM

					val cacheKeyLeft = depth to actionIndex - 1
					val countLeft = cache.getOrPut(cacheKeyLeft) {
						countTimelines(
							linesWithSplitters = linesWithSplitters.drop(1),
							previousState = leftTimeline,
							cache = cache
						)
					}

					val cacheKeyRight = depth to actionIndex + 1
					val countRight = cache.getOrPut(cacheKeyRight) {
						countTimelines(
							linesWithSplitters = linesWithSplitters.drop(1),
							previousState = rightTimeline,
							cache = cache
						)
					}

					return@mapIndexed countLeft + countRight
				}

				return@mapIndexed 0L
			}

			error("action not correct")
		}.sum()

		return sum
	}
}
