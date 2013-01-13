package com.github.nigelzor.hogs;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;

import java.util.Arrays;

public class ShiftMatrix<T> {
	private int rows;
	private int cols;
	private Object[] values;

	public ShiftMatrix(int rows, int cols) {
		this(rows, cols, new Object[rows * cols]);
	}

	private ShiftMatrix(int rows, int cols, Object[] values) {
		checkArgument(values.length == rows * cols, "values");
		this.rows = rows;
		this.cols = cols;
		this.values = values;
	}

	@SuppressWarnings("unchecked")
	public T get(int row, int col) {
		checkElementIndex(row, rows, "row");
		checkElementIndex(col, cols, "row");
		return (T) values[row * cols + col];
	}

	public void set(int row, int col, T value) {
		checkElementIndex(row, rows, "row");
		checkElementIndex(col, cols, "row");
		values[row * cols + col] = value;
	}

	// the order that directions are checked is arbitrary; so long as there's only one space
	// free, there won't be any alternatives
	public void shift(final int row, final int col) {
		checkElementIndex(row, rows, "row");
		checkElementIndex(col, cols, "row");

		// looking right
		for (int i = col + 1; i < cols; i++) {
			if (get(row, i) == null) {
				System.arraycopy(values, row * cols + col, values, row * cols + col + 1, i - col);
				set(row, col, null);
				return;
			}
		}

		// looking left
		for (int i = col - 1; i >= 0; i--) {
			if (get(row, i) == null) {
				System.arraycopy(values, row * cols + i + 1, values, row * cols + i, col - i);
				set(row, col, null);
				return;
			}
		}

		// looking down
		for (int i = row + 1; i < rows; i++) {
			if (get(i, col) == null) {
				for (int j = i; j > row; j--) {
					set(j, col, get(j - 1, col));
				}
				set(row, col, null);
				return;
			}
		}

		// looking up
		for (int i = row - 1; i >= 0; i--) {
			if (get(i, col) == null) {
				for (int j = i; j < row; j++) {
					set(j, col, get(j + 1, col));
				}
				set(row, col, null);
				return;
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cols;
		result = prime * result + rows;
		result = prime * result + Arrays.hashCode(values);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		@SuppressWarnings("rawtypes")
		ShiftMatrix other = (ShiftMatrix) obj;
		return (cols == other.cols)
				&& (rows == other.rows)
				&& Arrays.equals(values, other.values);
	}

	public ShiftMatrix<T> copy() {
		return new ShiftMatrix<T>(rows, cols, values.clone());
	}

	public int getRows() {
		return rows;
	}

	public int getCols() {
		return cols;
	}

}
