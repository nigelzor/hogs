package com.github.nigelzor.hogs

import kotlin.test.assertEquals
import org.junit.Test

public class ShiftMatrixTest {

	class object {
		private fun fill(matrix: ShiftMatrix<String>): ShiftMatrix<String> {
			var value: Char = 'A'
			for (row in 0..(matrix.rows - 1)) {
				for (col in 0..(matrix.cols - 1)) {
					matrix[row, col] = String(charArray(value++))
				}
			}
			return matrix
		}
	}

	[Test]
	public fun testShiftingOneRight() {
		var moving = fill(ShiftMatrix<String>(2, 2))
		moving[1, 1] = null
		moving.shift(1, 0)
		var expected = fill(ShiftMatrix<String>(2, 2))
		expected[1, 0] = null
		expected[1, 1] = "C"
		assertEquals(expected, moving)
	}

	[Test]
	public fun testShiftingOneLeft() {
		var moving = fill(ShiftMatrix<String>(2, 2))
		moving[0, 0] = null
		moving.shift(0, 1)
		var expected = fill(ShiftMatrix<String>(2, 2))
		expected[0, 0] = "B"
		expected[0, 1] = null
		assertEquals(expected, moving)
	}

	[Test]
	public fun testShiftingTwoLeft() {
		var moving = fill(ShiftMatrix<String>(1, 3))
		moving[0, 0] = null
		moving.shift(0, 2)
		var expected = ShiftMatrix<String>(1, 3)
		expected[0, 0] = "B"
		expected[0, 1] = "C"
		expected[0, 2] = null
		assertEquals(expected, moving)
	}

	[Test]
	public fun testShiftingPartOfRowLeft() {
		var moving = fill(ShiftMatrix<String>(1, 3))
		moving[0, 0] = null
		moving.shift(0, 1)
		var expected = ShiftMatrix<String>(1, 3)
		expected[0, 0] = "B"
		expected[0, 1] = null
		expected[0, 2] = "C"
		assertEquals(expected, moving)
	}

	[Test]
	public fun testShiftingOneDown() {
		var moving = fill(ShiftMatrix<String>(2, 2))
		moving[1, 1] = null
		moving.shift(0, 1)
		var expected = fill(ShiftMatrix<String>(2, 2))
		expected[0, 1] = null
		expected[1, 1] = "B"
		assertEquals(expected, moving)
	}

	[Test]
	public fun testShiftingOneUp() {
		var moving = fill(ShiftMatrix<String>(2, 2))
		moving[0, 0] = null
		moving.shift(1, 0)
		var expected = fill(ShiftMatrix<String>(2, 2))
		expected[1, 0] = null
		expected[0, 0] = "C"
		assertEquals(expected, moving)
	}

	[Test]
	public fun testShiftingTwoUp() {
		var moving = fill(ShiftMatrix<String>(3, 1))
		moving[0, 0] = null
		moving.shift(2, 0)
		var expected = ShiftMatrix<String>(3, 1)
		expected[0, 0] = "B"
		expected[1, 0] = "C"
		expected[2, 0] = null
		assertEquals(expected, moving)
	}

	[Test]
	public fun testShiftingPartOfRowUp() {
		var moving = fill(ShiftMatrix<String>(3, 1))
		moving[0, 0] = null
		moving.shift(1, 0)
		var expected = ShiftMatrix<String>(3, 1)
		expected[0, 0] = "B"
		expected[1, 0] = null
		expected[2, 0] = "C"
		assertEquals(expected, moving)
	}

}
