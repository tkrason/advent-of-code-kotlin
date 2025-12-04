package com.tkrason.aoc25

import com.tkrason.aoc25.util.FileLoader
import org.slf4j.Logger
import kotlin.time.measureTime

enum class Aoc25Days {
	DAY_1,
	DAY_2,
	DAY_3,
	DAY_4;
}

interface Solution {
	val day: Aoc25Days
	val logger: Logger
	fun solvePart1(input: String)
	fun solvePart2(input: String)

	fun solve() {
		val part1Input = getPart1Input()

		//warm up part 1
		repeat(5) { solvePart1(part1Input) }
		val part1Duration = measureTime { solvePart1(part1Input) }

		val part2Input = getPart2Input()

		// warm up part 2
		repeat(5) { solvePart2(part2Input) }
		val part2Duration = measureTime { solvePart2(part2Input) }

		logger.info("--- $day stats ---")
		logger.info("part 1 took: $part1Duration")
		logger.info("part 2 took: $part2Duration")
		logger.info("--- --- --- --- --")
	}

	fun getPart1Input() = FileLoader.loadFileAsText("/inputs/${day.name.lowercase()}_part1.txt")
	fun getPart2Input() = FileLoader.loadFileAsText("/inputs/${day.name.lowercase()}_part2.txt")
}