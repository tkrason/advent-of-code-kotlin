package com.tkrason.aoc25.util.extensions

fun <T : Any, R : Any> List<T>.chunkedByCondition(
	chunkCondition: (T) -> Boolean,
	valueTransform: (T) -> R,
): List<List<R>> {
	if (this.isEmpty()) return emptyList()

	val result = mutableListOf<List<R>>()

	var accumulator = mutableListOf<R>()
	val iterator = this.iterator()
	while (iterator.hasNext()) {
		val item = iterator.next()
		val shouldMakeChunk = chunkCondition(item)
		if (shouldMakeChunk) {
			result.add(accumulator)
			accumulator = mutableListOf()
		} else {
			accumulator.add(valueTransform(item))
		}
	}

	if (accumulator.isNotEmpty()) {
		result.add(accumulator)
	}

	return result
}