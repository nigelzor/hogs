package com.github.nigelzor.hogs

import kotlin.test.assertEquals
import org.junit.Test

class ShiftMatrixTest {

	companion object {
		private fun fill(matrix: ShiftMatrix<String>): ShiftMatrix<String> {
			var value: Char = 'A'
			for (row in 0 until matrix.rows) {
				for (col in 0 until matrix.cols) {
					matrix[row, col] = String(charArrayOf(value++))
				}
			}
			return matrix
		}
	}

	@Test fun testShiftingOneRight() {
		val moving = fill(ShiftMatrix.empty<String>(2, 2))
		moving[1, 1] = null
		moving.shift(1, 0)
		val expected = fill(ShiftMatrix.empty<String>(2, 2))
		expected[1, 0] = null
		expected[1, 1] = "C"
		assertEquals(expected, moving)
	}

	@Test fun testShiftingOneLeft() {
		val moving = fill(ShiftMatrix.empty<String>(2, 2))
		moving[0, 0] = null
		moving.shift(0, 1)
		val expected = fill(ShiftMatrix.empty<String>(2, 2))
		expected[0, 0] = "B"
		expected[0, 1] = null
		assertEquals(expected, moving)
	}

	@Test fun testShiftingTwoLeft() {
		val moving = fill(ShiftMatrix.empty<String>(1, 3))
		moving[0, 0] = null
		moving.shift(0, 2)
		val expected = ShiftMatrix.empty<String>(1, 3)
		expected[0, 0] = "B"
		expected[0, 1] = "C"
		expected[0, 2] = null
		assertEquals(expected, moving)
	}

	@Test fun testShiftingPartOfRowLeft() {
		val moving = fill(ShiftMatrix.empty<String>(1, 3))
		moving[0, 0] = null
		moving.shift(0, 1)
		val expected = ShiftMatrix.empty<String>(1, 3)
		expected[0, 0] = "B"
		expected[0, 1] = null
		expected[0, 2] = "C"
		assertEquals(expected, moving)
	}

	@Test fun testShiftingOneDown() {
		val moving = fill(ShiftMatrix.empty<String>(2, 2))
		moving[1, 1] = null
		moving.shift(0, 1)
		val expected = fill(ShiftMatrix.empty<String>(2, 2))
		expected[0, 1] = null
		expected[1, 1] = "B"
		assertEquals(expected, moving)
	}

	@Test fun testShiftingOneUp() {
		val moving = fill(ShiftMatrix.empty<String>(2, 2))
		moving[0, 0] = null
		moving.shift(1, 0)
		val expected = fill(ShiftMatrix.empty<String>(2, 2))
		expected[1, 0] = null
		expected[0, 0] = "C"
		assertEquals(expected, moving)
	}

	@Test fun testShiftingTwoUp() {
		val moving = fill(ShiftMatrix.empty<String>(3, 1))
		moving[0, 0] = null
		moving.shift(2, 0)
		val expected = ShiftMatrix.empty<String>(3, 1)
		expected[0, 0] = "B"
		expected[1, 0] = "C"
		expected[2, 0] = null
		assertEquals(expected, moving)
	}

	@Test fun testShiftingPartOfRowUp() {
		val moving = fill(ShiftMatrix.empty<String>(3, 1))
		moving[0, 0] = null
		moving.shift(1, 0)
		val expected = ShiftMatrix.empty<String>(3, 1)
		expected[0, 0] = "B"
		expected[1, 0] = null
		expected[2, 0] = "C"
		assertEquals(expected, moving)
	}

}
