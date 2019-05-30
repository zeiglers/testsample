package cs361.battleships.models;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BoardTest {

	private Board board;

	@Before
	public void setUp() {
		board = new Board();
	}

	@Test
	public void testInvalidPlacement() {
		assertFalse(board.placeShip(new Ship("MINESWEEPER"), 11, 'C', true));
	}

	@Test
	public void testPlaceMinesweeper() {
		assertTrue(board.placeShip(new Ship("MINESWEEPER"), 1, 'A', true));
	}

	@Test
	public void testAttackEmptySquare() {
		board.placeShip(new Ship("MINESWEEPER"), 1, 'A', true);
		Result result = board.attack(2, 'E');
		assertEquals(AtackStatus.MISS, result.getResult());
	}

	@Test
	public void testAttackShip() {
		Ship minesweeper = new Ship("BATTLESHIP"); // Originally this was MINESWEEPER and would one shot the CapQuarters
													// and register as a Surrender
		board.placeShip(minesweeper, 1, 'A', true);
		minesweeper = board.getShips().get(0);
		Result result = board.attack(1, 'C'); // Changed to C to attack CapQuarters
		assertEquals(AtackStatus.MISS, result.getResult());
		assertEquals(null, result.getShip());
	}

	@Test
	public void testAttackSameSquareMultipleTimes() {
		Ship minesweeper = new Ship("BATTLESHIP");
		minesweeper.setUnderwater(false);
		board.placeShip(minesweeper, 1, 'A', true);
		board.attack(1, 'A');
		Result result = board.attack(1, 'A');
		assertEquals(AtackStatus.INVALID, result.getResult());
	}

	@Test
	public void testAttackSameEmptySquareMultipleTimes() {
		Result initialResult = board.attack(1, 'A');
		assertEquals(AtackStatus.MISS, initialResult.getResult());
		Result result = board.attack(1, 'A');
		assertEquals(AtackStatus.INVALID, result.getResult());
	}

	@Test
	public void testMiss() {
		board.placeShip(new Ship("MINESWEEPER"), 1, 'A', true);
		board.attack(1, 'A');
		var result = board.attack(1, 'B');
		assertEquals(AtackStatus.MISS, result.getResult());
	}

	@Test
	public void testHit() {
		board.placeShip(new Ship("MINESWEEPER"), 1, 'A', true);
		var result = board.attack(2, 'A');
		assertEquals(AtackStatus.HIT, result.getResult());
	}

	@Test
	public void testPlaceMultipleShipsOfSameType() {
		assertTrue(board.placeShip(new Ship("MINESWEEPER"), 1, 'A', true));
		assertFalse(board.placeShip(new Ship("MINESWEEPER"), 5, 'D', true));

	}

	@Test
	public void testSpaceLaserOffSurface() {
		Ship sub = new Ship("SUBMARINE", false);
		board.placeShip(sub, 1, 'A', true);
		board.setSpaceLaser(false);
		var result = board.attack(1, 'A');
		assertEquals(AtackStatus.HIT, result.getResult());
	}

	@Test
	public void testSpaceLaserOffUnderwater() {
		Ship sub = new Ship("SUBMARINE", true);
		board.placeShip(sub, 1, 'A', true);
		board.setSpaceLaser(false);
		var result = board.attack(1, 'A');
		assertEquals(AtackStatus.MISS, result.getResult());
	}

	@Test
	public void testSpaceLaserOnSurface() {
		Ship sub = new Ship("SUBMARINE", false);
		board.placeShip(sub, 1, 'A', true);
		board.setSpaceLaser(true);
		var result = board.attack(1, 'A');
		assertEquals(AtackStatus.HIT, result.getResult());
	}

	@Test
	public void testSpaceLaserOnUnderwater() {
		Ship sub = new Ship("SUBMARINE", true);
		board.placeShip(sub, 1, 'A', true);
		board.setSpaceLaser(true);
		var result = board.attack(1, 'A');
		assertEquals(AtackStatus.HIT, result.getResult());
	}

	@Test
	public void testSpaceLaserOverlap() {
		Ship sub = new Ship("SUBMARINE", true);
		Ship battleship = new Ship("BATTLESHIP");
		board.placeShip(sub, 1, 'A', true);
		board.placeShip(battleship, 1, 'A', true);
		board.setSpaceLaser(true);
		var result = board.attack(1, 'A');
		assertEquals(AtackStatus.HIT, result.getResult());
	}

	@Test
	public void testCantPlaceMoreThan4Ships() {
		assertTrue(board.placeShip(new Ship("MINESWEEPER"), 1, 'A', true));
		assertTrue(board.placeShip(new Ship("BATTLESHIP"), 5, 'D', true));
		assertTrue(board.placeShip(new Ship("DESTROYER"), 6, 'A', false));
		assertTrue(board.placeShip(new Ship("SUBMARINE", false), 4, 'F', false));
		assertFalse(board.placeShip(new Ship(""), 9, 'A', false));

	}

	@Test
	public void testMoveShipsNorth() {
		assertTrue(board.placeShip(new Ship("MINESWEEPER"), 1, 'A', true));
		assertTrue(board.placeShip(new Ship("BATTLESHIP"), 5, 'D', true));
		assertTrue(board.placeShip(new Ship("DESTROYER"), 6, 'A', false));
		assertTrue(board.placeShip(new Ship("SUBMARINE", false), 4, 'F', false));
		assertTrue(board.moveFleet('N'));
	}

	@Test
	public void testMoveShipsSouth() {
		assertTrue(board.placeShip(new Ship("MINESWEEPER"), 1, 'A', true));
		assertTrue(board.placeShip(new Ship("BATTLESHIP"), 5, 'D', true));
		assertTrue(board.placeShip(new Ship("DESTROYER"), 6, 'A', false));
		assertTrue(board.placeShip(new Ship("SUBMARINE", false), 4, 'F', false));
		assertTrue(board.moveFleet('S'));
	}

	@Test
	public void testMoveShipsEast() {
		assertTrue(board.placeShip(new Ship("MINESWEEPER"), 1, 'A', true));
		assertTrue(board.placeShip(new Ship("BATTLESHIP"), 5, 'D', true));
		assertTrue(board.placeShip(new Ship("DESTROYER"), 6, 'A', false));
		assertTrue(board.placeShip(new Ship("SUBMARINE", false), 4, 'F', false));
		assertTrue(board.moveFleet('E'));
	}

	@Test
	public void testMoveShipsWest() {
		assertTrue(board.placeShip(new Ship("MINESWEEPER"), 1, 'A', true));
		assertTrue(board.placeShip(new Ship("BATTLESHIP"), 5, 'D', true));
		assertTrue(board.placeShip(new Ship("DESTROYER"), 6, 'A', false));
		assertTrue(board.placeShip(new Ship("SUBMARINE", false), 4, 'F', false));
		assertTrue(board.moveFleet('W'));
	}
}
