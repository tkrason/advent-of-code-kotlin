package com.tkrason.aoc25.solutions

import com.tkrason.aoc25.Aoc25Days
import com.tkrason.aoc25.Solution
import com.tkrason.aoc25.util.getLogger

private const val EMPTY_SPACE = '.'
private const val FILLED_SPACE = '@'

class Day4 : Solution {
	override val day: Aoc25Days = Aoc25Days.DAY_4
	override val logger = getLogger()

	override fun solvePart1(input: String) {
		val startGridState = input
			.lines()
			.map { line -> line.map { it == FILLED_SPACE }.toTypedArray() }
			.toTypedArray()

		val lastGridState = constructGridSequence(startGridState = startGridState)
			.drop(1) // drop start state
			.first()

		val filledAtStart = startGridState.countFilled()
		val filledAtEnd = lastGridState.countFilled()

		val result = filledAtStart - filledAtEnd
		require(result == 1346)
		logger.info("$result")
	}

	override fun solvePart2(input: String) {
		val startGridState = input
			.lines()
			.map { line -> line.map { it == FILLED_SPACE }.toTypedArray() }
			.toTypedArray()

		val lastGridState = constructGridSequence(startGridState = startGridState).last()

		val filledAtStart = startGridState.countFilled()
		val filledAtEnd = lastGridState.countFilled()

		val result = filledAtStart - filledAtEnd
		require(result == 8493)
		logger.info("$result")
	}

	private fun constructGridSequence(startGridState: Array<Array<Boolean>>): Sequence<Array<Array<Boolean>>> =
		generateSequence(seed = startGridState) {
			val grid = it.copy()
			val xMax = it[0].size
			val yMax = it.size

			val positionsToRemove = grid
				.withIndex()
				.flatMap { (row, line) ->
					line.withIndex().mapNotNull { (column, isSpaceFilled) ->
						if (!isSpaceFilled) return@mapNotNull null
						val position = row to column
						val adjacentFilledCount = position.countAdjacentFilled(grid, xMax, yMax)
						if (adjacentFilledCount < 4) position else null
					}
				}

			if (positionsToRemove.isEmpty()) return@generateSequence null
			positionsToRemove.forEach { grid.makeEmpty(it) }

			grid
		}

	private fun Array<Array<Boolean>>.copy(): Array<Array<Boolean>> = this.map { it.copyOf() }.toTypedArray()

	private val multipliers = listOf(
		-1 to 1,
		-1 to 0,
		-1 to -1,
		0 to 1,
		// empty
		0 to -1,
		1 to 1,
		1 to 0,
		1 to -1
	)

	private fun Pair<Int, Int>.countAdjacentFilled(grid: Array<Array<Boolean>>, xMax: Int, yMax: Int): Long {
		return multipliers
			.sumOf { (yDiff, xDiff) ->
				val newY = first + yDiff
				val newX = second + xDiff
				if (newY in 0..<yMax && newX in 0..<xMax && grid[newY][newX]) 1L else 0L
			}
	}

	private fun Array<Array<Boolean>>.countFilled() = this.sumOf { it.filter { it }.size }

	private fun Array<Array<Boolean>>.makeEmpty(position: Pair<Int, Int>) {
		this[position.first][position.second] = false
	}
}