package com.github.nigelzor.hogs

import org.junit.Test

import kotlin.Int as BTile
import kotlin.test.assertEquals
import java.util.Random
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class BTilesTest {
	companion object {
		val RANDOM = Random()

		val north = 1 shl 0
		val east = 1 shl 1
		val south = 1 shl 2
		val west = 1 shl 3
	}

	@Test fun testRotations() {
		assertEquals(north, BTiles.rotate(north, Rotation.ZERO_DEGREES))
		assertEquals(east, BTiles.rotate(north, Rotation.NINETY_DEGREES))
		assertEquals(south, BTiles.rotate(north, Rotation.ONE_HUNDRED_EIGHTY_DEGREES))
		assertEquals(west, BTiles.rotate(north, Rotation.TWO_HUNDRED_SEVENTY_DEGREES))
	}

	@Test fun testContains() {
		assertTrue(BTiles.contains(north, Direction.NORTH))
		assertFalse(BTiles.contains(north, Direction.EAST))
		assertFalse(BTiles.contains(north, Direction.SOUTH))
		assertFalse(BTiles.contains(north, Direction.WEST))

		assertTrue(BTiles.contains(east, Direction.EAST))
		assertTrue(BTiles.contains(south, Direction.SOUTH))
		assertTrue(BTiles.contains(west, Direction.WEST))
	}

	@Test fun testBits() {
		assertEquals(north, Direction.NORTH.bits)
		assertEquals(east, Direction.EAST.bits)
		assertEquals(south, Direction.SOUTH.bits)
		assertEquals(west, Direction.WEST.bits)
	}

	@Test fun testManyRotationsOfEmpty() {
		(0..100).forEach {
			val empty: BTile = RANDOM.nextInt() and 0x0F.inv()

			Rotation.values().forEach {
				assertEquals(empty, BTiles.rotate(empty, it))
			}
		}
	}
}