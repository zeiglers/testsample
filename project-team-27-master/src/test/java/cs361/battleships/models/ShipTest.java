package cs361.battleships.models;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ShipTest {

	@Test
	public void testPlaceMinesweeperHorizontaly() {
		Ship minesweeper = new Ship("MINESWEEPER");
		minesweeper.place('A', 1, false);
		List<Square> occupiedSquares = minesweeper.getOccupiedSquares();
		ArrayList<Object> expected = new ArrayList<>();
		expected.add(new Square(1, 'A'));
		expected.add(new Square(1, 'B'));
		assertEquals(expected, occupiedSquares);
	}

	@Test
	public void testPlaceMinesweeperVertically() {
		Ship minesweeper = new Ship("MINESWEEPER");
		minesweeper.place('A', 1, true);
		List<Square> occupiedSquares = minesweeper.getOccupiedSquares();
		ArrayList<Object> expected = new ArrayList<>();
		expected.add(new Square(1, 'A'));
		expected.add(new Square(2, 'A'));
		assertEquals(expected, occupiedSquares);
	}

	@Test
	public void testPlaceDestroyerHorizontaly() {
		Ship minesweeper = new Ship("DESTROYER");
		minesweeper.place('A', 1, false);
		List<Square> occupiedSquares = minesweeper.getOccupiedSquares();
		ArrayList<Object> expected = new ArrayList<>();
		expected.add(new Square(1, 'A'));
		expected.add(new Square(1, 'B'));
		expected.add(new Square(1, 'C'));
		assertEquals(expected, occupiedSquares);
	}

	@Test
	public void testPlaceDestroyerVertically() {
		Ship minesweeper = new Ship("DESTROYER");
		minesweeper.place('A', 1, true);
		List<Square> occupiedSquares = minesweeper.getOccupiedSquares();
		ArrayList<Object> expected = new ArrayList<>();
		expected.add(new Square(1, 'A'));
		expected.add(new Square(2, 'A'));
		expected.add(new Square(3, 'A'));
		assertEquals(expected, occupiedSquares);
	}

	@Test
	public void testPlaceBattleshipHorizontaly() {
		Ship minesweeper = new Ship("BATTLESHIP");
		minesweeper.place('A', 1, false);
		List<Square> occupiedSquares = minesweeper.getOccupiedSquares();
		ArrayList<Object> expected = new ArrayList<>();
		expected.add(new Square(1, 'A'));
		expected.add(new Square(1, 'B'));
		expected.add(new Square(1, 'C'));
		expected.add(new Square(1, 'D'));
		assertEquals(expected, occupiedSquares);
	}

	@Test
	public void testPlaceBattleshipVertically() {
		Ship minesweeper = new Ship("BATTLESHIP");
		minesweeper.place('A', 1, true);
		List<Square> occupiedSquares = minesweeper.getOccupiedSquares();
		ArrayList<Object> expected = new ArrayList<>();
		expected.add(new Square(1, 'A'));
		expected.add(new Square(2, 'A'));
		expected.add(new Square(3, 'A'));
		expected.add(new Square(4, 'A'));
		assertEquals(expected, occupiedSquares);
	}

	@Test
	public void testPlaceSubmarineVerticallyNotUnderwater() {
		Ship minesweeper = new Ship("SUBMARINE", false);
		minesweeper.place('A', 1, true);
		List<Square> occupiedSquares = minesweeper.getOccupiedSquares();
		ArrayList<Object> expected = new ArrayList<>();
		expected.add(new Square(1, 'A'));
		expected.add(new Square(2, 'A'));
		expected.add(new Square(3, 'A'));
		expected.add(new Square(3, 'B')); // Blip
		expected.add(new Square(4, 'A'));
		assertEquals(expected, occupiedSquares);
	}

	@Test
	public void testPlaceSubmarineVerticallyUnderwater() {
		Ship minesweeper = new Ship("SUBMARINE", true);
		minesweeper.place('A', 1, true);
		List<Square> occupiedSquares = minesweeper.getOccupiedSquares();
		ArrayList<Object> expected = new ArrayList<>();
		expected.add(new Square(1, 'A'));
		expected.add(new Square(2, 'A'));
		expected.add(new Square(3, 'A'));
		expected.add(new Square(3, 'B')); // Blip
		expected.add(new Square(4, 'A'));
		assertEquals(expected, occupiedSquares);
	}

	@Test
	public void testPlaceSubmarineHorizontalyUnderwater() {
		Ship minesweeper = new Ship("SUBMARINE", true);
		minesweeper.place('A', 2, false);
		List<Square> occupiedSquares = minesweeper.getOccupiedSquares();
		ArrayList<Object> expected = new ArrayList<>();
		expected.add(new Square(2, 'A'));
		expected.add(new Square(2, 'B'));
		expected.add(new Square(2, 'C'));
		expected.add(new Square(1, 'C')); // Blip
		expected.add(new Square(2, 'D'));
		assertEquals(expected, occupiedSquares);
	}

	@Test
	public void testShipOverlaps() {
		Ship minesweeper1 = new Ship("MINESWEEPER");
		minesweeper1.place('A', 1, true);

		Ship minesweeper2 = new Ship("MINESWEEPER");
		minesweeper2.place('A', 1, true);

		assertTrue(minesweeper1.overlaps(minesweeper2));
	}

	@Test
	public void testShipsDontOverlap() {
		Ship minesweeper1 = new Ship("MINESWEEPER");
		minesweeper1.place('A', 1, true);

		Ship minesweeper2 = new Ship("MINESWEEPER");
		minesweeper2.place('C', 2, true);

		assertFalse(minesweeper1.overlaps(minesweeper2));
	}

	@Test
	public void testIsAtLocation() {
		Ship minesweeper = new Ship("BATTLESHIP");
		minesweeper.place('A', 1, true);

		assertTrue(minesweeper.isAtLocation(new Square(1, 'A')));
		assertTrue(minesweeper.isAtLocation(new Square(2, 'A')));
	}

	@Test
	public void testHit() {
		Ship minesweeper = new Ship("BATTLESHIP");
		minesweeper.place('A', 1, true);

		Result result = minesweeper.attack(1, 'A');
		assertEquals(AtackStatus.HIT, result.getResult());
		assertEquals(minesweeper, result.getShip());
		assertEquals(new Square(1, 'A'), result.getLocation());
	}

	@Test
	public void testSink() {
		Ship minesweeper = new Ship("MINESWEEPER");
		minesweeper.place('A', 1, true);

		minesweeper.attack(1, 'A');
		Result result = minesweeper.attack(2, 'A');

		assertEquals(AtackStatus.SUNK, result.getResult());
		assertEquals(minesweeper, result.getShip());
		assertEquals(new Square(2, 'A'), result.getLocation());
	}

	@Test
	public void testOverlapsBug() {
		Ship minesweeper = new Ship("MINESWEEPER");
		Ship destroyer = new Ship("DESTROYER");
		minesweeper.place('C', 5, false);
		destroyer.place('C', 5, false);
		assertTrue(minesweeper.overlaps(destroyer));
	}

	@Test
	public void testAttackSameSquareTwice() {
		Ship minesweeper = new Ship("MINESWEEPER");
		minesweeper.place('A', 1, true);
		var result = minesweeper.attack(1, 'A');
		assertEquals(AtackStatus.SUNK, result.getResult());
		result = minesweeper.attack(1, 'A');
		assertEquals(AtackStatus.INVALID, result.getResult());
	}

	@Test
	public void testEquals() {
		Ship minesweeper1 = new Ship("MINESWEEPER");
		minesweeper1.place('A', 1, true);
		Ship minesweeper2 = new Ship("MINESWEEPER");
		minesweeper2.place('A', 1, true);
		assertTrue(minesweeper1.equals(minesweeper2));
		assertEquals(minesweeper1.hashCode(), minesweeper2.hashCode());
	}

	@Test
	public void testAttackCaptainsQuarters() {
		Ship minesweeper = new Ship("MINESWEEPER");
		minesweeper.place('A', 1, true);
		var result = minesweeper.attack(1, 'A');
		assertEquals(AtackStatus.SUNK, result.getResult());
	}

	@Test
	public void testArmorDestroyer() {
		Ship destroyer = new Ship("DESTROYER");
		destroyer.place('A', 1, true);
		var result = destroyer.attack(2, 'A');
		assertEquals(AtackStatus.MISS, result.getResult());
		result = destroyer.attack(2, 'A');
		assertEquals(AtackStatus.SUNK, result.getResult());
	}

	@Test
	public void testArmorBattleship() {
		Ship battleship = new Ship("BATTLESHIP");
		battleship.place('A', 1, true);
		var result = battleship.attack(3, 'A');
		assertEquals(AtackStatus.MISS, result.getResult());
		result = battleship.attack(3, 'A');
		assertEquals(AtackStatus.SUNK, result.getResult());
	}

	@Test
	public void testArmorSubmarine() {
		Ship submarine = new Ship("SUBMARINE", false);
		submarine.place('A', 1, true);

		var result = submarine.attack(1, 'A');
		assertEquals(AtackStatus.HIT, result.getResult());
		result = submarine.attack(2, 'A');
		assertEquals(AtackStatus.HIT, result.getResult());
		result = submarine.attack(3, 'A');
		assertEquals(AtackStatus.HIT, result.getResult());
		result = submarine.attack(3, 'B');
		assertEquals(AtackStatus.HIT, result.getResult());

		result = submarine.attack(4, 'A');
		assertEquals(AtackStatus.MISS, result.getResult());
		result = submarine.attack(4, 'A');
		assertEquals(AtackStatus.SUNK, result.getResult());
	}
}
