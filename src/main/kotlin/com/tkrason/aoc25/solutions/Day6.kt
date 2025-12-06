package com.tkrason.aoc25.solutions

import com.tkrason.aoc25.Aoc25Days
import com.tkrason.aoc25.Solution
import com.tkrason.aoc25.util.extensions.chunkedByCondition
import com.tkrason.aoc25.util.getLogger
import org.slf4j.Logger

private const val SIGN_ADDITION = "+"
private const val SIGN_MULTIPLICATION = "*"

class Day6 : Solution {
	override val day = Aoc25Days.DAY_6
	override val logger: Logger = getLogger()

	override fun solvePart1(input: String) {

		val lines = input.lines()
		val numbers = lines
			.take(lines.size - 1) // do not take last row
			.map {
				it.split(" ")
					.filter { it != "" }
					.map { it.toLong() }
			}

		val cols = (0 until numbers[0].size).map { index ->
			numbers.map { it[index] }
		}

		val operations = lines
			.last()
			.split(" ")
			.filter { it != "" }

		val sum = cols.zip(operations) { col, operation ->
			col.reduce { acc, number ->
				when (operation) {
					SIGN_ADDITION -> acc + number
					SIGN_MULTIPLICATION -> acc * number
					else -> error("Not supported")
				}
			}
		}.sum()

		require(sum == 6299564383938)
		logger.info("$sum")
	}

	override fun solvePart2(input: String) {
		val lines = input.lines()
		val numbers = lines.take(lines.size - 1) // do not take last row

		val rowLength = numbers[0].length
		val x = (0 until rowLength)
			.map { index -> numbers.map { it[index] } }
			.map { chars -> chars.fold("") { acc, char -> "$acc$char" } }
			.chunkedByCondition(chunkCondition = { it.isBlank() }, valueTransform = { it.trim().toLong() })

		val operations = lines
			.last()
			.split(" ")
			.filter { it != "" }

		val sum = x.zip(operations) { numbers, operation ->
			numbers.reduce { acc, number ->
				when (operation) {
					SIGN_ADDITION -> acc + number
					SIGN_MULTIPLICATION -> acc * number
					else -> error("Not supported")
				}
			}
		}.sum()

		require(sum == 11950004808442)
		logger.info("$sum")
	}
}