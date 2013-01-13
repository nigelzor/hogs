package com.github.nigelzor.hogs;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

public class DirectionTest {

	@Test
	public void testRotateNorth() {
		assertThat(Direction.NORTH.rotate(Rotation.ZERO_DEGREES), is(Direction.NORTH));
		assertThat(Direction.NORTH.rotate(Rotation.NINETY_DEGREES), is(Direction.EAST));
		assertThat(Direction.NORTH.rotate(Rotation.ONE_HUNDRED_EIGHTY_DEGREES), is(Direction.SOUTH));
		assertThat(Direction.NORTH.rotate(Rotation.TWO_HUNDRED_SEVENTY_DEGREES), is(Direction.WEST));
	}

	@Test
	public void testRotateSouth() {
		assertThat(Direction.SOUTH.rotate(Rotation.ZERO_DEGREES), is(Direction.SOUTH));
		assertThat(Direction.SOUTH.rotate(Rotation.NINETY_DEGREES), is(Direction.WEST));
		assertThat(Direction.SOUTH.rotate(Rotation.ONE_HUNDRED_EIGHTY_DEGREES), is(Direction.NORTH));
		assertThat(Direction.SOUTH.rotate(Rotation.TWO_HUNDRED_SEVENTY_DEGREES), is(Direction.EAST));
	}

}
