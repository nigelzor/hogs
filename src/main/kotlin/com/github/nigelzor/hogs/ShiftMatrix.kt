package com.github.nigelzor.hogs

import com.google.common.base.Preconditions
import java.util.*

class ShiftMatrix<T: Any>(val rows: Int, val cols: Int, val values: Array<T?>) {
	companion object {
		inline fun <reified T: Any> empty(rows: Int, cols: Int): ShiftMatrix<T> {
			return ShiftMatrix(rows, cols, arrayOfNulls(rows * cols))
		}
	}

	val indicies: Collection<Index>

	init {
		Preconditions.checkArgument(values.size == rows * cols, "values")
		indicies = ArrayList<Index>()
		for (x in 0 until rows )
			for (y in 0 until cols)
				indicies.add(Index(x, y))
	}

	fun contains(index: Index): Boolean {
		return index.row >= 0 && index.row < rows && index.col >= 0 && index.col < cols
	}

	operator fun get(index: Index): T? {
		return get(index.row, index.col)
	}

	operator fun get(row: Int, col: Int): T? {
		Preconditions.checkElementIndex(row, rows, "row")
		Preconditions.checkElementIndex(col, cols, "row")
		return values[row * cols + col]
	}

	operator fun set(index: Index, value: T?) {
		return set(index.row, index.col, value)
	}

	operator fun set(row: Int, col: Int, value: T?) {
		Preconditions.checkElementIndex(row, rows, "row")
		Preconditions.checkElementIndex(col, cols, "row")
		values[row * cols + col] = value
	}

	// the order that directions are checked is arbitrary; so long as there's only one space
	// free, there won't be any alternatives
	fun shift(row: Int, col: Int) {
		Preconditions.checkElementIndex(row, rows, "row")
		Preconditions.checkElementIndex(col, cols, "row")
		var i: Int

		// looking right
		i = col
		while (++i < cols) {
			if (get(row, i) == null) {
				System.arraycopy(values, row * cols + col, values, row * cols + col + 1, i - col)
				set(row, col, null)
				return
			}
		}

		// looking left
		i = col
		while (--i >= 0) {
			if (get(row, i) == null) {
				System.arraycopy(values, row * cols + i + 1, values, row * cols + i, col - i)
				set(row, col, null)
				return
			}
		}

		// looking down
		i = row
		while (++i < rows) {
			if (get(i, col) == null) {
				var j: Int = i
				while (j > row) {
					set(j, col, get(j - 1, col))
					j--
				}
				set(row, col, null)
				return
			}
		}

		// looking up
		i = row
		while (--i >= 0) {
			if (get(i, col) == null) {
				for (j in i..row - 1) {
					set(j, col, get(j + 1, col))
				}
				set(row, col, null)
				return
			}
		}
	}

	fun clone(cloner: (T?) -> T? = { it }): ShiftMatrix<T> {
		val newValues = values.copyOf()
		for (i in newValues.indices) {
			newValues[i] = cloner(newValues[i])
		}
		return ShiftMatrix(rows, cols, newValues)
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other?.javaClass != javaClass) return false

		other as ShiftMatrix<Any> // ShiftMatrix<*> gets confused, and thinks values could be an Array<Nothing>

		if (rows != other.rows) return false
		if (cols != other.cols) return false
		if (!Arrays.equals(values, other.values)) return false

		return true
	}

	override fun hashCode(): Int {
		var result = rows
		result = 31 * result + cols
		result = 31 * result + Arrays.hashCode(values)
		return result
	}

	override fun toString(): String {
		return "ShiftMatrix(rows=$rows, cols=$cols, values=${Arrays.toString(values)})"
	}

}
