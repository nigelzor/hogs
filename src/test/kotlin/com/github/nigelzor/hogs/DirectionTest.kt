package com.github.nigelzor.hogs

import kotlin.test.assertEquals
import org.junit.Test

class DirectionTest {

	@Test fun testRotateNorth() {
		assertEquals(Direction.NORTH, Direction.NORTH.rotate(Rotation.ZERO_DEGREES))
		assertEquals(Direction.EAST, Direction.NORTH.rotate(Rotation.NINETY_DEGREES))
		assertEquals(Direction.SOUTH, Direction.NORTH.rotate(Rotation.ONE_HUNDRED_EIGHTY_DEGREES))
		assertEquals(Direction.WEST, Direction.NORTH.rotate(Rotation.TWO_HUNDRED_SEVENTY_DEGREES))
	}

	@Test fun testRotateSouth() {
		assertEquals(Direction.SOUTH, Direction.SOUTH.rotate(Rotation.ZERO_DEGREES))
		assertEquals(Direction.WEST, Direction.SOUTH.rotate(Rotation.NINETY_DEGREES))
		assertEquals(Direction.NORTH, Direction.SOUTH.rotate(Rotation.ONE_HUNDRED_EIGHTY_DEGREES))
		assertEquals(Direction.EAST, Direction.SOUTH.rotate(Rotation.TWO_HUNDRED_SEVENTY_DEGREES))
	}

}
