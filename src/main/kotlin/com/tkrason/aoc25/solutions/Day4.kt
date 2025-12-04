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

		// ...InPlaceFast does not work here, as it's not doing removal only of one iteration
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

		val filledAtStart = startGridState.countFilled()
		// 500 solved (500 warm up)
		// constructGridSequence (5.216123625s, 4.752609542s)
		// constructGridSequenceInPlaceFast (1.231971083s, 1.273960791s)
		val lastGridState = constructGridSequenceInPlaceFast(startGridState = startGridState).last()
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

	private fun constructGridSequenceInPlaceFast(startGridState: Array<Array<Boolean>>): Sequence<Array<Array<Boolean>>> =
		generateSequence(seed = startGridState) {
			val grid = it
			val xMax = it[0].size
			val yMax = it.size

			var changed = false;

			// TODO: Optimize to be only 1D BooleanArray
			grid.forEachIndexed { y, line ->
				line.forEachIndexed inner@{ x, isSpaceFilled ->
					if (!isSpaceFilled) return@inner

					val adjacentFilledCount = countAdjacentFilledFast(grid, xMax, yMax, y, x)
					if (adjacentFilledCount < 4) {
						grid[y][x] = false
						changed = true
					}
				}
			}
			if (!changed) return@generateSequence null
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

	private fun countAdjacentFilledFast(grid: Array<Array<Boolean>>, xMax: Int, yMax: Int, y: Int, x: Int): Int {
		// top left
		// y = +1, x= -1
		val tly = y+1
		val tlx = x-1
		val tl = if(tly < yMax && tlx >= 0 && grid[tly][tlx]) 1 else 0
		
		// top mid
		// y = +1
		val tmy = y+1
		val tm = if(tmy < yMax && grid[tmy][x]) 1 else 0

		// top right
		// y = +1, x= +1
		val topRightY = y+1
		val topRightX = x+1
		val topRight = if(topRightY < yMax && topRightX < xMax && grid[topRightY][topRightX]) 1 else 0
		
		// mid left
		// x= -1
		val midLeftX = x-1
		val midLeft = if(midLeftX >= 0 && grid[y][midLeftX]) 1 else 0

		// mid right
		// x= +1
		val midRightX = x+1
		val midRight = if(midRightX < xMax && grid[y][midRightX]) 1 else 0

		// bottom left
		// y = -1, x= -1
		val bottomLeftY = y-1
		val bottomLeftX = x-1
		val bottomLeft = if(bottomLeftY >= 0 && bottomLeftX >= 0 && grid[bottomLeftY][bottomLeftX]) 1 else 0

		// bottom mid
		// y = -1
		val bottomMidY = y-1
		val bottomMid = if(bottomMidY >= 0 && grid[bottomMidY][x]) 1 else 0

		// bottom right
		// y = -1, x= +1
		val bottomRightY = y-1
		val bottomRightX = x+1
		val bottomRight = if(bottomRightY >= 0 && bottomRightX < xMax && grid[bottomRightY][bottomRightX]) 1 else 0
		
		return tl + tm + topRight + midLeft + midRight + bottomLeft + bottomMid + bottomRight
	}

	private fun Array<Array<Boolean>>.countFilled() = this.sumOf { it.filter { it }.size }

	private fun Array<Array<Boolean>>.makeEmpty(position: Pair<Int, Int>) {
		this[position.first][position.second] = false
	}

	private fun Array<Array<Boolean>>.makeEmptyFast(y: Int, x: Int) {
		this[y][x] = false
	}
}