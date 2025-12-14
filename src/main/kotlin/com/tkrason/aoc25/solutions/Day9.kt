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

		require(largest.area == 4750297200)
		logger.info("${largest.area}")
	}

	private fun areaOf(a: Point, b: Point): Long {
		return (maxOf(a.x, b.x) - minOf(a.x, b.x) + 1) * (maxOf(a.y, b.y) - minOf(a.y, b.y) + 1)
	}

	enum class Axis {
		X,
		Y;
	}

	enum class Movement {
		UP,
		DOWN,
		LEFT,
		RIGHT,
	}

	data class RectangleCandidate(
		val redPointA: Point, // definitely inside, on path
		val redPointB: Point, // definitely inside, on path
		val area: Long,

		val pointC: Point, // inside / outside / on path
		val pointD: Point, // inside / outside / on path
	)

	/*
	 * ..#XXXX#..
	 * Path segment only contains values as seen here by X
	 */
	data class Path(
		val startsAt: Point,
		val endsAt: Point,

		val stableAxis: Axis,
		val axisStableAt: Long,

		val pathSegment: LongRange,
		val fullPath: LongRange,
		val movement: Movement,
	)

	override fun solvePart2(input: String) {
		val points = input
			.lines()
			.map {
				val (x, y) = it.split(",")
				Point(x = x.toLong(), y = y.toLong())
			}

		val paths = (points + points.first()).zipWithNext { point, nextPoint ->
			val stableAxis = point.determineStableAxis(other = nextPoint)
			val movement = point.determineMovement(other = nextPoint)

			val pathSegment = when (movement) {
				Movement.UP -> nextPoint.y + 1 until point.y
				Movement.DOWN -> point.y + 1 until nextPoint.y
				Movement.LEFT -> nextPoint.x + 1 until point.x
				Movement.RIGHT -> point.x + 1 until nextPoint.x
			}

			val fullPath = when (movement) {
				Movement.UP -> nextPoint.y..point.y
				Movement.DOWN -> point.y..nextPoint.y
				Movement.LEFT -> nextPoint.x..point.x
				Movement.RIGHT -> point.x..nextPoint.x
			}

			val axisStableAt = when (stableAxis) {
				Axis.X -> point.x
				Axis.Y -> point.y
			}

			Path(
				startsAt = point,
				endsAt = nextPoint,
				stableAxis = stableAxis,
				axisStableAt = axisStableAt,
				pathSegment = pathSegment,
				fullPath = fullPath,
				movement = movement,
			)
		}

		val pathToIndex = paths.mapIndexed { index, path -> path to index }.toMap()

		val rectanglesData = points.flatMapIndexed { index, point ->
			points.subList(index + 1, points.size).map { other ->

				val pointC = Point(x = point.x, other.y)
				val pointD = Point(x = other.x, point.y)

				RectangleCandidate(
					redPointA = point,
					redPointB = other,
					area = areaOf(point, other),
					pointC = pointC,
					pointD = pointD,
				)
			}
		}
			.sortedByDescending { it.area }

		val axisToPathSegments = paths
			.groupBy { it.stableAxis }
			.mapValues { (_, values) -> values.sortedBy { it.axisStableAt } }

		val raysHorizontal = (0..100_000L).map { y ->
			val point = Point(x = 0, y = y)
			point.constructRay(
				rayStableAxis = Axis.Y,
				axisToPathSegment = axisToPathSegments,
				pathToIndex = pathToIndex,
				paths = paths,
			)
		}

		val raysVertical = (0..100_000L).map { x ->
			val point = Point(x = x, y = 0)
			point.constructRay(
				rayStableAxis = Axis.X,
				axisToPathSegment = axisToPathSegments,
				pathToIndex = pathToIndex,
				paths = paths,
			)
		}

		val helpMap = mapOf(
			Axis.X to raysVertical,
			Axis.Y to raysHorizontal
		)

		val found = rectanglesData.first { rectangle ->

			val isInsidePointC = raysHorizontal[rectangle.pointC.y.toInt()].isInsideAt(rectangle.pointC.x)
			if (!isInsidePointC) return@first false

			val isInsidePointD = raysHorizontal[rectangle.pointD.y.toInt()].isInsideAt(rectangle.pointD.x)
			if (!isInsidePointD) return@first false

			val isInsideAC = rectangle.redPointA.isPathBetweenTwoPointsIn(rectangle.pointC, helpMap)
			if (!isInsideAC) return@first false

			val isInsideAD = rectangle.redPointA.isPathBetweenTwoPointsIn(rectangle.pointD, helpMap)
			if (!isInsideAD) return@first false

			val isInsideBC = rectangle.redPointB.isPathBetweenTwoPointsIn(rectangle.pointC, helpMap)
			if (!isInsideBC) return@first false

			val isInsideDB = rectangle.redPointB.isPathBetweenTwoPointsIn(rectangle.pointD, helpMap)
			if (!isInsideDB) return@first false

			true
		}

		require(found.area == 1578115935L)
	}

	private fun Point.determineMovement(other: Point): Movement {
		val stableAxis = determineStableAxis(other)
		return when (stableAxis) {
			// If X is stable, we must be moving UP / DOWN
			Axis.X -> {
				if (y > other.y) Movement.UP else Movement.DOWN
			}
			// If Y is stable, we must be moving LEFT / RIGHT
			Axis.Y -> {
				if (x > other.x) Movement.LEFT else Movement.RIGHT
			}
		}
	}

	private fun Point.determineStableAxis(other: Point): Axis {
		if (x == other.x) return Axis.X
		if (y == other.y) return Axis.Y

		error("Can't happen")
	}

	private fun Point.constructRay(
		rayStableAxis: Axis,
		axisToPathSegment: Map<Axis, List<Path>>,
		pathToIndex: Map<Path, Int>,
		paths: List<Path>,
	): List<Pair<LongRange, Boolean>> {
		val ray = (0..Long.MAX_VALUE)

		val rayStableAxisValue = when (rayStableAxis) {
			Axis.X -> x
			Axis.Y -> y
		}

		val rayMovingAxis = when (rayStableAxis) {
			Axis.X -> Axis.Y
			Axis.Y -> Axis.X
		}
		val rayCanBeBlockedBy = axisToPathSegment.getValue(rayMovingAxis)
		val blocks = rayCanBeBlockedBy
			.filter { it.isIntersectedByRay(ray, rayStableAxisValue) }

		val flipPoints = blocks.map { it.axisStableAt..it.axisStableAt to true }

		val rayCanSlideOn = axisToPathSegment.getValue(rayStableAxis)
		val slides = rayCanSlideOn
			.filter { it.isRaySliding(rayStableAxisValue) }
			.map {
				val indexOfPath = pathToIndex.getValue(it)
				val previous = paths.cyclicGet(indexOfPath - 1)
				val next = paths.cyclicGet(indexOfPath + 1)
				val isAreaFlip = previous.movement == next.movement
				it to isAreaFlip
			}

		val slidesFlipPoints = slides.map { (path, willFlip) -> path.fullPath to willFlip }
		val flipPointsAll = (flipPoints + slidesFlipPoints).sortedBy { (path, _) -> path.first }

		return flipPointsAll
	}

	private fun Path.isIntersectedByRay(ray: LongRange, rayStableAxisValue: Long): Boolean {
		return pathSegment.contains(rayStableAxisValue) && ray.contains(axisStableAt)
	}

	private fun Path.isRaySliding(rayStableAxisValue: Long): Boolean {
		return axisStableAt == rayStableAxisValue
	}

	private fun List<Path>.cyclicGet(index: Int): Path {
		val safeIndex = Math.floorMod(index, size)
		return this[safeIndex]
	}

	private fun List<Pair<LongRange, Boolean>>.isInsideAt(index: Long): Boolean {
		if (isEmpty()) return false
		if (this.any { (range, _) -> range.contains(index) }) return true // is part of path
		val relevant = this.takeWhile { (range, _) -> range.last < index }
		val numberOfFlips = relevant.count { (_, flip) -> flip }

		return numberOfFlips % 2 != 0
	}

	private fun Point.isPathBetweenTwoPointsIn(
		other: Point,
		helpMap: Map<Axis, List<List<Pair<LongRange, Boolean>>>>,
	): Boolean {
		val (sideAC, fixedAxis) = if (x == other.x) {
			minOf(y, other.y)..maxOf(y, other.y) to Axis.X
		} else if (y == other.y) {
			minOf(x, other.x)..maxOf(x, other.x) to Axis.Y
		} else {
			error("can't")
		}

		val rayIndex = if (fixedAxis == Axis.X) x else y
		return helpMap.getValue(fixedAxis)[rayIndex.toInt()].isRangeFullyIn(myRange = sideAC)
	}

	private fun List<Pair<LongRange, Boolean>>.isRangeFullyIn(myRange: LongRange): Boolean {
		if (isEmpty()) return false
		if (this.any { (range, _) -> range.containsFully(myRange) }) return true

		val relevant = this
			.filterNot { (range, willFlip) -> range.contains(myRange.first) && willFlip }
			.filterNot { (range, willFlip) -> range.contains(myRange.last) && willFlip }
			.filter { (range, _) -> myRange.containsFully(range) }

		val numberOfFlips = relevant.count()

		val isInside = numberOfFlips == 0
		return isInside
	}

	private fun LongRange.containsFully(inner: LongRange): Boolean {
		return this.first <= inner.first && this.last >= inner.last
	}
}
