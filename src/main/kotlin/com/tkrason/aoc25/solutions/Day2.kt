package com.tkrason.aoc25.solutions

import com.tkrason.aoc25.Aoc25Days
import com.tkrason.aoc25.Solution
import com.tkrason.aoc25.util.getLogger

class Day2 : Solution {
	override val day: Aoc25Days = Aoc25Days.DAY_2

	override val logger = getLogger()

	override fun solvePart1(input: String) {
		val ranges = input.split(",")

		val sum = ranges
			.fold(0L) { acc, range ->
				val (from, to) = range.split("-")

				val sum = (from.toLong() .. to.toLong())
					.filter { id -> id.toString().length % 2 == 0 }
					.filter { id ->
						val stringId = id.toString()
						val (firstHalf, secondHalf) = stringId.chunked(stringId.length / 2)

						firstHalf == secondHalf
					}
					.sum()

				acc + sum
			}

		logger.info("$sum")
	}

	override fun solvePart2(input: String) {
		val ranges = input.split(",")

		val sum = ranges
			.fold(0L) { acc, range ->
				val (from, to) = range.split("-")

				val sum = (from.toLong() .. to.toLong())
					.filter { id ->
						val stringId = id.toString()

						val maxWindowSizePossible = stringId.length / 2
						val hasAnyRepetition = (1..maxWindowSizePossible).any { windowSize ->
							if(stringId.length % windowSize != 0) return@any false

							val chunks = stringId.chunked(windowSize).toSet()
							chunks.size == 1
						}

						hasAnyRepetition
					}
					.sum()

				acc + sum
			}

		logger.info("$sum")
	}
}