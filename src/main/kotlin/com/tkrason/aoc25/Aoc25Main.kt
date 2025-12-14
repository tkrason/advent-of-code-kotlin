package com.tkrason.aoc25

import com.tkrason.aoc25.solutions.Day1
import com.tkrason.aoc25.solutions.Day10
import com.tkrason.aoc25.solutions.Day2
import com.tkrason.aoc25.solutions.Day3
import com.tkrason.aoc25.solutions.Day4
import com.tkrason.aoc25.solutions.Day5
import com.tkrason.aoc25.solutions.Day6
import com.tkrason.aoc25.solutions.Day7
import com.tkrason.aoc25.solutions.Day8
import com.tkrason.aoc25.solutions.Day9

fun main() {
	Aoc25Days.DAY_10.getSolution().solve(
		warmUpBeforeMeasure = 0,
		measureTimeRepeating = 1
	)
}

fun Aoc25Days.getSolution() = when (this) {
	Aoc25Days.DAY_1 -> Day1()
	Aoc25Days.DAY_2 -> Day2()
	Aoc25Days.DAY_3 -> Day3()
	Aoc25Days.DAY_4 -> Day4()
	Aoc25Days.DAY_5 -> Day5()
	Aoc25Days.DAY_6 -> Day6()
	Aoc25Days.DAY_7 -> Day7()
	Aoc25Days.DAY_8 -> Day8()
	Aoc25Days.DAY_9 -> Day9()
	Aoc25Days.DAY_10 -> Day10()
}