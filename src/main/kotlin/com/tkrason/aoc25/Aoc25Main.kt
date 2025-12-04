package com.tkrason.aoc25

import com.tkrason.aoc25.solutions.Day1
import com.tkrason.aoc25.solutions.Day2
import com.tkrason.aoc25.solutions.Day3
import com.tkrason.aoc25.solutions.Day4

fun main() {
	Aoc25Days.DAY_4.getSolution().solve(
		warmUpBeforeMeasure = 500,
		measureTimeRepeating = 500,
	)

//	Aoc25Days
//		.entries
//		.forEach { it.solve() }
}

fun Aoc25Days.getSolution() = when (this) {
	Aoc25Days.DAY_1 -> Day1()
	Aoc25Days.DAY_2 -> Day2()
	Aoc25Days.DAY_3 -> Day3()
	Aoc25Days.DAY_4 -> Day4()
}