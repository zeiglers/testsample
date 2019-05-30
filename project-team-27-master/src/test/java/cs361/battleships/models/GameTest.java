package cs361.battleships.models;

import org.junit.Test;

import controllers.AttackGameAction;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class GameTest {
	private Game game;
	private Ship minesweeper;

	@Before
	public void setUp() {
		game = new Game();
		minesweeper = new Ship("MINESWEEPER");
		minesweeper.place('A', 1, false);
	}

	@Test
	public void testPulse() {
		game.placeShip(minesweeper, 1, 'A', false);
		assertTrue(game.attack(2,'A',true));
		assertFalse(game.attack(2,'A',true));
	}

	@Test
	public void testPulseRegular() {
		game.placeShip(minesweeper, 1, 'A', false);
		assertTrue(game.attack(2,'A'));
	}
}