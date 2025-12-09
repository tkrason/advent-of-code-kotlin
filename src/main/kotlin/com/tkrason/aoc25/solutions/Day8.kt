package com.tkrason.aoc25.solutions

import com.tkrason.aoc25.Aoc25Days
import com.tkrason.aoc25.Solution
import com.tkrason.aoc25.util.getLogger
import org.slf4j.Logger
import kotlin.math.pow
import kotlin.math.sqrt

class Day8 : Solution {
	override val day = Aoc25Days.DAY_8
	override val logger: Logger = getLogger()

	data class Point(
		val x: Double,
		val y: Double,
		val z: Double,
	)

	data class Data(
		val pointA: Point,
		val pointB: Point,
		val dist: Double,
	)

	override fun solvePart1(input: String) {
		val points = input
			.lines()
			.map {
				val (x, y, z) = it.split(",").map { it.toDouble() }
				Point(x, y, z)
			}

		val pointToPointDistanceData = points.flatMapIndexed { index, point ->
			// index+1 to not count dist to self
			points.subList(index + 1, points.size).map { other ->
				val distance = point.distanceTo(other)
				Data(pointA = point, pointB = other, dist = distance)
			}
		}

		val relevantPointsByDistance = pointToPointDistanceData
			.sortedBy { it.dist }
			.take(1000)

		val emptyPointToChain = emptyMap<Point, Set<Point>>()
		val pointToChain = relevantPointsByDistance.fold(emptyPointToChain) { acc, path ->

			val existingChainPointA = acc[path.pointA] ?: emptySet()
			val existingChainPointB = acc[path.pointB] ?: emptySet()

			val pointInNewChain = existingChainPointA + existingChainPointB + setOf(path.pointA, path.pointB)
			val pointToChainUpdates = pointInNewChain.associateWith { pointInNewChain }

			return@fold acc + pointToChainUpdates
		}

		val result = pointToChain
			.values
			.toSet() // only unique chains
			.sortedByDescending { it.size }
			.take(3)
			.map { it.size }
			.reduce { acc, i -> acc * i }

		require(result == 131150)
	}

	private fun Point.distanceTo(other: Point): Double {
		val xSq = (x - other.x).pow(2.0)
		val ySq = (y - other.y).pow(2.0)
		val zSq = (z - other.z).pow(2.0)

		return sqrt(xSq + ySq + zSq)
	}

	override fun solvePart2(input: String) {
		val points = input
			.lines()
			.map {
				val (x, y, z) = it.split(",").map { it.toDouble() }
				Point(x, y, z)
			}

		val pointToPointDistanceData = points.flatMapIndexed { index, point ->
			// index+1 to not count dist to self
			points.subList(index + 1, points.size).map { other ->
				val distance = point.distanceTo(other)
				Data(pointA = point, pointB = other, dist = distance)
			}
		}
			.sortedBy { it.dist }

		val pointToChain = points.associateWith { emptySet<Point>() }
		val pointToPointDistanceDataIterator = pointToPointDistanceData.iterator()
		val resultSequence = generateSequence<Pair<Map<Point, Set<Point>>, Data?>>(pointToChain to null) { (pointToChain, _) ->
			if (!pointToPointDistanceDataIterator.hasNext()) return@generateSequence null
			val path = pointToPointDistanceDataIterator.next()

			val existingChainPointA = pointToChain.getValue(path.pointA)
			val existingChainPointB = pointToChain.getValue(path.pointB)

			val pointInNewChain = existingChainPointA + existingChainPointB + setOf(path.pointA, path.pointB)
			val pointToChainUpdates = pointInNewChain.associateWith { pointInNewChain }

			(pointToChain + pointToChainUpdates) to path
		}

		val (_, lastConnectedData) = resultSequence
			// when any chain contains all points, all are connected. Until that we throw away all results
			.dropWhile { (pointToChain, _) -> pointToChain.any { (_, chain) -> chain.size != points.size } }
			.first()

		requireNotNull(lastConnectedData)
		val result = (lastConnectedData.pointA.x * lastConnectedData.pointB.x).toLong()

		require(result == 2497445L)
		logger.info("$result")
	}
}