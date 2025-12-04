package com.tkrason.aoc25.solutions

import com.tkrason.aoc25.Aoc25Days
import com.tkrason.aoc25.Solution
import com.tkrason.aoc25.util.getLogger

class Day3 : Solution {
	override val day: Aoc25Days = Aoc25Days.DAY_3
	override val logger = getLogger()

	override fun solvePart1(input: String) {
		val sum = input
			.lines()
			.map { battery ->
				val battery = battery.chunked(1).map { it.toInt() }
				val firstIndexOf = (1..9)
					.associateWith { numberLookingFor -> battery.indexOfFirst { it == numberLookingFor } }
					.filterValues { it != -1 } // not found

				val possibleNumbers = firstIndexOf.keys

				val lastIndexOf = possibleNumbers
					.associateWith { numberLookingFor -> battery.indexOfLast { it == numberLookingFor } }
					.filterValues { it != -1 }

				val possibleNumbersOrdered = possibleNumbers.sortedDescending()

				val (firstDigit, secondDigit) = possibleNumbersOrdered.firstNotNullOf { firstDigit ->
					val indexOfFirstDigit = firstIndexOf.getValue(firstDigit)

					val secondDigitOrNull = possibleNumbersOrdered.firstOrNull { secondDigit ->
						val indexOfSecondDigit = lastIndexOf.getValue(secondDigit)
						indexOfSecondDigit > indexOfFirstDigit
					}

					if(secondDigitOrNull == null) return@firstNotNullOf null else firstDigit to secondDigitOrNull
				}

				"$firstDigit$secondDigit".toInt()
			}
			.sum()

		logger.info("$sum")
	}

	override fun solvePart2(input: String) {
		val sum = input
			.lines()
			.map { battery ->
				val battery = battery.map { it.digitToInt() }

				val (_, result) = (11 downTo 0).fold(0 to "") { (firstPossibleIndex, result), lastPossibleIndex ->
					val consideredBatterySubsection = battery.subList(firstPossibleIndex, battery.size - lastPossibleIndex)

					val firstIndexOf = (1..9)
						.associateWith { numberLookingFor -> consideredBatterySubsection.indexOfFirst { it == numberLookingFor } }
						.filterValues { it != -1 }

					val biggestNumber = firstIndexOf.keys.maxOf { it }
					val indexOfBiggestNumber = firstIndexOf.getValue(biggestNumber)

					val nextSubsectionStartAt = firstPossibleIndex + indexOfBiggestNumber + 1
					nextSubsectionStartAt to "$result$biggestNumber"
				}

				result.toBigDecimal()
			}
			.sumOf { it }

		logger.info("$sum")
	}
}