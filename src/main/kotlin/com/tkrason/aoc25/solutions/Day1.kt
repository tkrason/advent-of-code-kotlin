package com.tkrason.aoc25.solutions

import com.tkrason.aoc25.Aoc25Days
import com.tkrason.aoc25.Solution
import com.tkrason.aoc25.util.getLogger
import kotlin.math.absoluteValue

class Day1 : Solution {

	override val day: Aoc25Days = Aoc25Days.DAY_1

	override val logger = getLogger()

	override fun solvePart1(input: String) {
		val (_, counter) = input
			.lines()
			.fold(initial = 50 to 0) { (startingAt, counter), rotation ->
				val rotationDirection = rotation.rotationDirection()
				val rotationSize = rotation.rotationSize() * rotationDirection

				val leftoverRotationSize = rotationSize % 100
				val finishedAt = startingAt + leftoverRotationSize

				val finishedAtNormalized = when {
					finishedAt < 0 -> 100 + finishedAt // going direction 1 -> 0 -> 99
					finishedAt >= 100 -> finishedAt - 100 // going direction 99 -> 0 -> 1
					else -> finishedAt // did not pass 0
				}

				val stoppedAt0 = if (finishedAtNormalized == 0) 1 else 0
				finishedAtNormalized to (counter + stoppedAt0)
			}

		logger.info("$counter")
	}

	override fun solvePart2(input: String) {
		val (_, pointingCounter) = input
			.lines()
			.fold(initial = 50 to 0) { (startingAt, counter), rotation ->
				val rotationDirection = rotation.rotationDirection()
				val rotationSize = rotation.rotationSize() * rotationDirection

				val numOfFullRotations = (rotationSize / 100).absoluteValue

				val leftoverRotationSize = rotationSize % 100
				val finishedAt = startingAt + leftoverRotationSize

				val (finishedAtNormalized, hasMadeRotation) = when {
					finishedAt < 0 -> 100 + finishedAt to true
					finishedAt >= 100 -> finishedAt - 100 to true
					else -> finishedAt to false
				}

				val finalRotation = when {
					leftoverRotationSize != 0 && finishedAtNormalized == 0 -> 1 // landed exactly at 0 on final rotation
					startingAt != 0 && leftoverRotationSize != 0 && hasMadeRotation -> 1 // turned over but not at 0
					else -> 0
				}

				finishedAtNormalized to (counter + numOfFullRotations + finalRotation)
			}

		logger.info("$pointingCounter")
	}

	fun gemini1Shot(input: String) {
		var currentPosition = 50 // The dial starts at 50
		var zeroCount = 0        // Counter for how many times we land on 0
		val dialSize = 100       // Numbers 0 through 99

		for (instruction in input.lines()) {
			// Parse the direction (first char) and amount (rest of string)
			val direction = instruction.first()
			val amount = instruction.substring(1).toInt()

			if (direction == 'R') {
				// Rotate Right (Add)
				currentPosition = (currentPosition + amount) % dialSize
			} else if (direction == 'L') {
				// Rotate Left (Subtract)
				// We use standard logic, then add dialSize and mod again to handle negative wrapping
				currentPosition = ((currentPosition - amount) % dialSize + dialSize) % dialSize
			}

			// Check if the dial stopped at 0
			if (currentPosition == 0) {
				zeroCount++
			}
		}

		logger.info("$zeroCount")
	}

	fun gemini1Shot2(input: String) {
		var currentPosition = 50 // Start at 50
		var zeroCount = 0L       // Use Long to prevent overflow if the input is huge
		val dialSize = 100

		for (instruction in input.lines()) {
			val direction = instruction.first()
			val amount = instruction.substring(1).toInt()

			if (direction == 'R') {
				// Logic: How many times do we cross a multiple of 100?
				// Example: Pos 90, R20 -> End 110. (90+20)/100 = 1 hit.
				val hits = (currentPosition + amount) / dialSize
				zeroCount += hits

				// Update position
				currentPosition = (currentPosition + amount) % dialSize

			} else if (direction == 'L') {
				// Logic: Distance to reach 0 going backwards
				// If we are at 0, we must rotate 100 to hit 0 again.
				// If we are at 50, we rotate 50 to hit 0.
				val distToFirstZero = if (currentPosition == 0) dialSize else currentPosition

				if (amount >= distToFirstZero) {
					// We reached 0 at least once
					var hits = 1L

					// Calculate remaining clicks after the first hit
					val remainingAmount = amount - distToFirstZero

					// Every 100 clicks after this counts as another hit
					hits += remainingAmount / dialSize

					zeroCount += hits
				}

				// Update position (same math as Part 1)
				currentPosition = ((currentPosition - amount) % dialSize + dialSize) % dialSize
			}
		}

		logger.info("$zeroCount")
	}

	private fun String.rotationDirection() = when (this[0]) {
		'L' -> -1
		'R' -> 1
		else -> error("panik")
	}

	private fun String.rotationSize() = this.drop(1).toInt()

}