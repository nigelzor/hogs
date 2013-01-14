package com.github.nigelzor.hogs

import com.google.common.base.Preconditions

public data class ShiftMatrix<T>(val rows: Int, val cols: Int, values: Array<T?>? = null) {
	private val values: Array<T?>
	{
		if (values != null) {
			Preconditions.checkArgument(values.size == rows * cols, "values")
			this.values = values
		} else {
			this.values = arrayOfNulls(rows * cols)
		}
	}

	public fun get(row: Int, col: Int): T? {
		Preconditions.checkElementIndex(row, rows, "row")
		Preconditions.checkElementIndex(col, cols, "row")
		return values[row * cols + col]
	}

	public fun set(row: Int, col: Int, value: T?): Unit {
		Preconditions.checkElementIndex(row, rows, "row")
		Preconditions.checkElementIndex(col, cols, "row")
		values[row * cols + col] = value
	}

	// the order that directions are checked is arbitrary; so long as there's only one space
	// free, there won't be any alternatives
	public fun shift(row: Int, col: Int): Unit {
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

	public fun copy(): ShiftMatrix<T> {
		return ShiftMatrix<T>(rows, cols, array(values))
	}

}
