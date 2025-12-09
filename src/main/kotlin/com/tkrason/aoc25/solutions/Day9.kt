package com.tkrason.aoc25.solutions

import com.tkrason.aoc25.Aoc25Days
import com.tkrason.aoc25.Solution
import com.tkrason.aoc25.util.getLogger
import org.slf4j.Logger

class Day9 : Solution {
	override val day: Aoc25Days = Aoc25Days.DAY_9
	override val logger: Logger = getLogger()

	data class Point(val x: Long, val y: Long)
	data class Data(val pointA: Point, val pointB: Point, val area: Long)

	override fun solvePart1(input: String) {

		val points = input
			.lines()
			.map {
				val (x, y) = it.split(",")
				Point(x = x.toLong(), y = y.toLong())
			}

		val rectanglesData = points.flatMapIndexed { index, point ->
			points.subList(index + 1, points.size).map { other ->
				Data(pointA = point, pointB = other, area = areaOf(point, other))
			}
		}

		val largest = rectanglesData.maxBy { it.area }

		logger.info("${largest.area}")
	}

	private fun areaOf(a: Point, b: Point): Long {
		return (maxOf(a.x, b.x) - minOf(a.x, b.x) + 1) * (maxOf(a.y, b.y) - minOf(a.y, b.y) + 1)
	}

	override fun solvePart2(input: String) {
		// TODO("Not yet implemented")
	}
}
