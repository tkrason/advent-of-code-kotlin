package com.tkrason.aoc25.solutions

import com.tkrason.aoc25.Aoc25Days
import com.tkrason.aoc25.Solution
import com.tkrason.aoc25.util.getLogger
import kotlin.math.pow

class Day2 : Solution {
	override val day: Aoc25Days = Aoc25Days.DAY_2

	override val logger = getLogger()

	override fun solvePart1(input: String) {
		val ranges = input.split(",")

		// 500 solved (500 warm up)
		// sumRange (41.818375ms, 34.041583ms)
		val sum = ranges.sumOf {
			val (from, to) = it.split("-")
			val range = from.toLong()..to.toLong()
			sumRange(range)
		}

		require(sum == 30323879646)
	}

	private fun sumRange(range: LongRange): Long {
		val rangeStart = if (computeNextRepeating(range.first) == range.first) range.first else 0
		val rangeSum = generateSequence(range.first) { number ->
			val nextRepeating = computeNextRepeating(number + 1)
			if (nextRepeating > range.last) return@generateSequence null

			nextRepeating
		}
			.drop(1)
			.sum()

		return rangeStart + rangeSum
	}

	private fun computeNextRepeating(number: Long): Long {
		val numberOfDigits = number.toString().length
		val isEvenNumberOfDigits = numberOfDigits % 2 == 0
		// All numbers with odd number of digits have no chance to be mirrored,
		// so we start lookup from the next smallest digit with even digits e.g. 754 -> 1000
		val startNextRepeatingLookupFrom = when (isEvenNumberOfDigits) {
			true -> number
			false -> 10.toDouble().pow(numberOfDigits.toDouble()).toLong()
		}

		val targetNumberOfDigits = when (isEvenNumberOfDigits) {
			true -> numberOfDigits
			else -> numberOfDigits + 1
		}
		val multiplicand = 10.toDouble().pow(targetNumberOfDigits.toDouble() / 2).toLong() + 1
		val remainder = multiplicand - (startNextRepeatingLookupFrom % multiplicand)

		return startNextRepeatingLookupFrom + remainder
	}

	override fun solvePart2(input: String) {
		val ranges = input.split(",")

		val sum = ranges
			.fold(0L) { acc, range ->
				val (from, to) = range.split("-")

				val sum = (from.toLong()..to.toLong())
					.filter { id ->
						val stringId = id.toString()

						val maxWindowSizePossible = stringId.length / 2
						val hasAnyRepetition = (1..maxWindowSizePossible).any { windowSize ->
							if (stringId.length % windowSize != 0) return@any false

							val chunks = stringId.chunked(windowSize).toSet()
							chunks.size == 1
						}

						hasAnyRepetition
					}
					.sum()

				acc + sum
			}

		require(sum == 43872163557)
	}
}