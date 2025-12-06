package com.tkrason.aoc25.solutions

import com.tkrason.aoc25.Aoc25Days
import com.tkrason.aoc25.Solution
import com.tkrason.aoc25.util.getLogger
import org.slf4j.Logger

class Day5 : Solution {
	override val day: Aoc25Days = Aoc25Days.DAY_5
	override val logger: Logger = getLogger()

	override fun solvePart1(input: String) {

		val ranges = input
			.lines()
			.takeWhile { it != "" }

		val ingredients = input
			.lines()
			.drop(ranges.size)
			.drop(1)
			.map { it.toLong() }

		val freshRanges = ranges.map {
			val (from, to) = it.split("-").map { it.toLong() }
			from..to
		}

		val freshIngredients = ingredients.count { ingredient -> freshRanges.any { range -> ingredient in range } }

		logger.info("$freshIngredients")
	}

	override fun solvePart2(input: String) {
		val ranges = input
			.lines()
			.takeWhile { it != "" }

		val freshRanges = ranges.map {
			val (from, to) = it.split("-").map { it.toLong() }
			from..to
		}

		val freshRangesFirstAscending = freshRanges.sortedBy { it.first }
		val freshRangesLastDescending = freshRanges.sortedByDescending { it.last }

		val extendedRanges = freshRangesFirstAscending.map { range ->
			// will always find at least itself
			val bestExtension = freshRangesLastDescending.first { it.first in range }

			range.first  .. maxOf(range.last, bestExtension.last)
		}

		val (_, sum) = extendedRanges.fold(0L..0L to 0L) { (lastRange, sum), currentRange ->
			if(currentRange.first in lastRange) {
				if(currentRange.last <= lastRange.last) {
					lastRange to sum
				} else {
					val oversize = currentRange.last - lastRange.last
					lastRange.first..currentRange.last to sum + oversize
				}
			} else {
				currentRange to sum + currentRange.size()
			}
		}

		logger.info("$sum")
	}

	private fun LongRange.size() = endInclusive - start + 1L
}