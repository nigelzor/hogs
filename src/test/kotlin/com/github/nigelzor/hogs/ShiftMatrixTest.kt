package com.github.nigelzor.hogs

import kotlin.test.assertEquals
import org.junit.Test

public class ShiftMatrixTest {

	class object {
		private fun fill(matrix: ShiftMatrix<String>): ShiftMatrix<String> {
			var value: Char = 'A'
			for (row in 0..(matrix.rows - 1)) {
				for (col in 0..(matrix.cols - 1)) {
					matrix.set(row, col, String(charArray(value++)))
				}
			}
			return matrix
		}
	}

	[Test]
	public fun testShiftingOneRight(): Unit {
		var moving = fill(ShiftMatrix<String>(2, 2))
		moving.set(1, 1, null)
		moving.shift(1, 0)
		var expected = fill(ShiftMatrix<String>(2, 2))
		expected.set(1, 0, null)
		expected.set(1, 1, "C")
		assertEquals(expected, moving)
	}

	[Test]
	public fun testShiftingOneLeft(): Unit {
		var moving = fill(ShiftMatrix<String>(2, 2))
		moving.set(0, 0, null)
		moving.shift(0, 1)
		var expected = fill(ShiftMatrix<String>(2, 2))
		expected.set(0, 0, "B")
		expected.set(0, 1, null)
		assertEquals(expected, moving)
	}

	[Test]
	public fun testShiftingTwoLeft(): Unit {
		var moving = fill(ShiftMatrix<String>(1, 3))
		moving.set(0, 0, null)
		moving.shift(0, 2)
		var expected = ShiftMatrix<String>(1, 3)
		expected.set(0, 0, "B")
		expected.set(0, 1, "C")
		expected.set(0, 2, null)
		assertEquals(expected, moving)
	}

	[Test]
	public fun testShiftingPartOfRowLeft(): Unit {
		var moving = fill(ShiftMatrix<String>(1, 3))
		moving.set(0, 0, null)
		moving.shift(0, 1)
		var expected = ShiftMatrix<String?>(1, 3)
		expected.set(0, 0, "B")
		expected.set(0, 1, null)
		expected.set(0, 2, "C")
		assertEquals(expected, moving)
	}

	[Test]
	public fun testShiftingOneDown(): Unit {
		var moving = fill(ShiftMatrix<String>(2, 2))
		moving.set(1, 1, null)
		moving.shift(0, 1)
		var expected = fill(ShiftMatrix<String>(2, 2))
		expected.set(0, 1, null)
		expected.set(1, 1, "B")
		assertEquals(expected, moving)
	}

	[Test]
	public fun testShiftingOneUp(): Unit {
		var moving = fill(ShiftMatrix<String>(2, 2))
		moving.set(0, 0, null)
		moving.shift(1, 0)
		var expected = fill(ShiftMatrix<String>(2, 2))
		expected.set(1, 0, null)
		expected.set(0, 0, "C")
		assertEquals(expected, moving)
	}

	[Test]
	public fun testShiftingTwoUp(): Unit {
		var moving = fill(ShiftMatrix<String>(3, 1))
		moving.set(0, 0, null)
		moving.shift(2, 0)
		var expected = ShiftMatrix<String>(3, 1)
		expected.set(0, 0, "B")
		expected.set(1, 0, "C")
		expected.set(2, 0, null)
		assertEquals(expected, moving)
	}

	[Test]
	public fun testShiftingPartOfRowUp(): Unit {
		var moving = fill(ShiftMatrix<String>(3, 1))
		moving.set(0, 0, null)
		moving.shift(1, 0)
		var expected = ShiftMatrix<String>(3, 1)
		expected.set(0, 0, "B")
		expected.set(1, 0, null)
		expected.set(2, 0, "C")
		assertEquals(expected, moving)
	}

}
