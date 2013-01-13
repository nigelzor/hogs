package com.github.nigelzor.hogs;

import static org.junit.Assert.*;

import org.junit.Test;

public class ShiftMatrixTest {

	@Test
	public void testShiftingOneRight() {
		ShiftMatrix<String> moving = fill(new ShiftMatrix<String>(2, 2));
		moving.set(1, 1, null);
		moving.shift(1, 0);

		ShiftMatrix<String> expected = fill(new ShiftMatrix<String>(2, 2));
		expected.set(1, 0, null);
		expected.set(1, 1, "C");

		assertEquals(expected, moving);
	}

	@Test
	public void testShiftingOneLeft() {
		ShiftMatrix<String> moving = fill(new ShiftMatrix<String>(2, 2));
		moving.set(0, 0, null);
		moving.shift(0, 1);

		ShiftMatrix<String> expected = fill(new ShiftMatrix<String>(2, 2));
		expected.set(0, 0, "B");
		expected.set(0, 1, null);

		assertEquals(expected, moving);
	}

	@Test
	public void testShiftingTwoLeft() {
		ShiftMatrix<String> moving = fill(new ShiftMatrix<String>(1, 3));
		moving.set(0, 0, null);
		moving.shift(0, 2);

		ShiftMatrix<String> expected = new ShiftMatrix<String>(1, 3);
		expected.set(0, 0, "B");
		expected.set(0, 1, "C");
		expected.set(0, 2, null);

		assertEquals(expected, moving);
	}

	@Test
	public void testShiftingPartOfRowLeft() {
		ShiftMatrix<String> moving = fill(new ShiftMatrix<String>(1, 3));
		moving.set(0, 0, null);
		moving.shift(0, 1);

		ShiftMatrix<String> expected = new ShiftMatrix<String>(1, 3);
		expected.set(0, 0, "B");
		expected.set(0, 1, null);
		expected.set(0, 2, "C");

		assertEquals(expected, moving);
	}

	@Test
	public void testShiftingOneDown() {
		ShiftMatrix<String> moving = fill(new ShiftMatrix<String>(2, 2));
		moving.set(1, 1, null);
		moving.shift(0, 1);

		ShiftMatrix<String> expected = fill(new ShiftMatrix<String>(2, 2));
		expected.set(0, 1, null);
		expected.set(1, 1, "B");

		assertEquals(expected, moving);
	}

	@Test
	public void testShiftingOneUp() {
		ShiftMatrix<String> moving = fill(new ShiftMatrix<String>(2, 2));
		moving.set(0, 0, null);
		moving.shift(1, 0);

		ShiftMatrix<String> expected = fill(new ShiftMatrix<String>(2, 2));
		expected.set(1, 0, null);
		expected.set(0, 0, "C");

		assertEquals(expected, moving);
	}

	@Test
	public void testShiftingTwoUp() {
		ShiftMatrix<String> moving = fill(new ShiftMatrix<String>(3, 1));
		moving.set(0, 0, null);
		moving.shift(2, 0);

		ShiftMatrix<String> expected = new ShiftMatrix<String>(3, 1);
		expected.set(0, 0, "B");
		expected.set(1, 0, "C");
		expected.set(2, 0, null);

		assertEquals(expected, moving);
	}

	@Test
	public void testShiftingPartOfRowUp() {
		ShiftMatrix<String> moving = fill(new ShiftMatrix<String>(3, 1));
		moving.set(0, 0, null);
		moving.shift(1, 0);

		ShiftMatrix<String> expected = new ShiftMatrix<String>(3, 1);
		expected.set(0, 0, "B");
		expected.set(1, 0, null);
		expected.set(2, 0, "C");

		assertEquals(expected, moving);
	}

	// A B
	// C D
	private static ShiftMatrix<String> fill(ShiftMatrix<String> matrix) {
		char value = 'A';
		for (int row = 0; row < matrix.getRows(); row++) {
			for (int col = 0; col < matrix.getCols(); col++) {
				matrix.set(row, col, String.valueOf(value++));
			}
		}
		return matrix;
	}

}
