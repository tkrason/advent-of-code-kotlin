package com.tkrason.aoc25.util

object FileLoader {
	fun loadFileAsText(fileName: String) = {}::class.java.getResource(fileName)!!.readText()
	fun loadFileAsLines(fileName: String) = loadFileAsText(fileName).split(System.lineSeparator())
}