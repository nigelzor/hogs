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
	}

	@Test fun testRotations() {
		assertEquals(BTiles.NORTH, BTiles.rotate(BTiles.NORTH, Rotation.ZERO_DEGREES))
		assertEquals(BTiles.EAST, BTiles.rotate(BTiles.NORTH, Rotation.NINETY_DEGREES))
		assertEquals(BTiles.SOUTH, BTiles.rotate(BTiles.NORTH, Rotation.ONE_HUNDRED_EIGHTY_DEGREES))
		assertEquals(BTiles.WEST, BTiles.rotate(BTiles.NORTH, Rotation.TWO_HUNDRED_SEVENTY_DEGREES))
	}

	@Test fun testContains() {
		assertTrue(BTiles.contains(BTiles.NORTH, Direction.NORTH))
		assertFalse(BTiles.contains(BTiles.NORTH, Direction.EAST))
		assertFalse(BTiles.contains(BTiles.NORTH, Direction.SOUTH))
		assertFalse(BTiles.contains(BTiles.NORTH, Direction.WEST))

		assertTrue(BTiles.contains(BTiles.EAST, Direction.EAST))
		assertTrue(BTiles.contains(BTiles.SOUTH, Direction.SOUTH))
		assertTrue(BTiles.contains(BTiles.WEST, Direction.WEST))
	}

	@Test fun testDirectionBits() {
		assertEquals(BTiles.NORTH, Direction.NORTH.bits)
		assertEquals(BTiles.EAST, Direction.EAST.bits)
		assertEquals(BTiles.SOUTH, Direction.SOUTH.bits)
		assertEquals(BTiles.WEST, Direction.WEST.bits)
	}

	@Test fun testObjectiveBits() {
		assertEquals(BTiles.OBJECTIVE_ONE, Objective.ONE.bits)
		assertEquals(BTiles.OBJECTIVE_TWO, Objective.TWO.bits)
		assertEquals(BTiles.OBJECTIVE_THREE, Objective.THREE.bits)
		assertEquals(BTiles.OBJECTIVE_FOUR, Objective.FOUR.bits)
	}

	@Test fun testPlayerBits() {
		assertEquals(BTiles.PLAYER_ONE, Colour.BLUE.bits)
		assertEquals(BTiles.PLAYER_TWO, Colour.YELLOW.bits)
		assertEquals(BTiles.PLAYER_THREE, Colour.RED.bits)
		assertEquals(BTiles.PLAYER_FOUR, Colour.GREEN.bits)
	}

	@Test fun testRotateWithContents() {
		val tile = BTiles.OBJECTIVE_THREE or BTiles.PLAYER_TWO or BTiles.NORTH
		Rotation.values().forEach {
			val rotated = BTiles.rotate(tile, it)
			assertTrue(BTiles.contains(rotated, Colour.YELLOW))
			assertTrue(BTiles.contains(rotated, Objective.THREE))
		}
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