package com.tkrason.aoc25

import com.tkrason.aoc25.solutions.Day1
import com.tkrason.aoc25.solutions.Day2
import com.tkrason.aoc25.solutions.Day3
import com.tkrason.aoc25.solutions.Day4
import com.tkrason.aoc25.solutions.Day5
import com.tkrason.aoc25.solutions.Day6
import com.tkrason.aoc25.solutions.Day7

fun main() {
	Aoc25Days.DAY_7.getSolution().solve()
}

fun Aoc25Days.getSolution() = when (this) {
	Aoc25Days.DAY_1 -> Day1()
	Aoc25Days.DAY_2 -> Day2()
	Aoc25Days.DAY_3 -> Day3()
	Aoc25Days.DAY_4 -> Day4()
	Aoc25Days.DAY_5 -> Day5()
	Aoc25Days.DAY_6 -> Day6()
	Aoc25Days.DAY_7 -> Day7()
}