package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Board {

	@JsonProperty
	private List<Ship> ships;
	@JsonProperty
	private List<Result> attacks;
	@JsonProperty
	private boolean isSpaceLaser;

	/*
	 * DO NOT change the signature of this method. It is used by the grading
	 * scripts.
	 */
	public Board() {
		ships = new ArrayList<>();
		attacks = new ArrayList<>();
		isSpaceLaser = false;
	}

	/*
	 * DO NOT change the signature of this method. It is used by the grading
	 * scripts.
	 */
	public boolean placeShip(Ship ship, int x, char y, boolean isVertical) {
		if (ships.size() >= 4) {
			return false;
		}
		if (ships.stream().anyMatch(s -> s.getKind().equals(ship.getKind()))) {
			return false;
		}

		final Ship placedShip;
		if (ship.getKind().equals("SUBMARINE")) {
			placedShip = new Ship(ship.getKind(), ship.getUnderwater());
		} else {
			placedShip = new Ship(ship.getKind());
		}

		placedShip.place(y, x, isVertical); // Places the ship

		// Check only for ships on the surface, as underwater subs can overlap
		if (ship.getUnderwater() == false) {
			// Check if it overlaps with any ships on the board (surface only)
			if (ships.stream().anyMatch(s -> s.overlaps(placedShip))) {
				return false;
			}
		}

		if (placedShip.getOccupiedSquares().stream().anyMatch(s -> s.isOutOfBounds())) {
			return false;
		}
		ships.add(placedShip);
		return true;
	}

	public boolean moveFleet(char dir) {
		// Get floating ships and ID sunk Squares
		List<Ship> shipsToMove = new ArrayList<>();
		shipsToMove.addAll(ships);
		List<Square> blockedSquares = new ArrayList<>();
		boolean canMove = true;
		// get get all sunk ship squares--these don't move and will block
		for (Ship Boat : shipsToMove) {
			if (Boat.isSunk()) {
				blockedSquares.addAll(Boat.getOccupiedSquares());
				shipsToMove.remove(Boat);
			}
		}
		switch (dir) {
		case 'N':
			sortNSEWMost(dir, shipsToMove);
			// Try to move each Ship
			for (Ship movingBoat : shipsToMove) {
				// Make sure topmost square is not at edge
				if (movingBoat.getOccupiedSquares().get(0).getRow() > 1) {
					// Edge check passes = sub can move
					if (movingBoat.getUnderwater())
						movingBoat.moveShip(movingBoat, dir);
					// Reason not to move: any square is vertically adjacent to another ship's
					else {
						for (Square trySquare : movingBoat.getOccupiedSquares()) {
							for (Square blockSquare : blockedSquares) {
								if (blockSquare.getColumn() == trySquare.getColumn())
									// == -1 for moving N
									if (blockSquare.getRow() - trySquare.getRow() == -1)
										canMove = false;
							}
						}
						// Passes edge check and adjacency check = ship can move
						if (canMove)
							movingBoat.moveShip(movingBoat, dir);
						blockedSquares.addAll(movingBoat.getOccupiedSquares());
						canMove = true;
					}
				}
			}
			return true;
		case 'S':
			sortNSEWMost(dir, shipsToMove);
			// Try to move each Ship
			for (Ship movingBoat : shipsToMove) {
				// Make sure bottommost square is not at edge
				if (movingBoat.getOccupiedSquares().get(movingBoat.getOccupiedSquares().size() - 1).getRow() < 10) {
					// Edge check passes = sub can move
					if (movingBoat.getUnderwater())
						movingBoat.moveShip(movingBoat, dir);
					// Reason not to move: any square is vertically adjacent to another ship's
					else {
						for (Square trySquare : movingBoat.getOccupiedSquares()) {
							for (Square blockSquare : blockedSquares) {
								if (blockSquare.getColumn() == trySquare.getColumn())
									// == 1 for moving S
									if (blockSquare.getRow() - trySquare.getRow() == 1)
										canMove = false;
							}
						}
						// Passes edge check and adjacency check = ship can move
						if (canMove)
							movingBoat.moveShip(movingBoat, dir);
						blockedSquares.addAll(movingBoat.getOccupiedSquares());
						canMove = true;
					}
				}
			}
			return true;
		case 'E':
			sortNSEWMost(dir, shipsToMove);
			// Try to move each Ship
			for (Ship movingBoat : shipsToMove) {
				// Make sure east-most square is not at edge
				if (movingBoat.getOccupiedSquares().get(movingBoat.getOccupiedSquares().size() - 1).getColumn() < 'J') {
					// Edge check passes = sub can move
					if (movingBoat.getUnderwater())
						movingBoat.moveShip(movingBoat, dir);
					// Reason not to move: any square is vertically adjacent to another ship's
					else {
						for (Square trySquare : movingBoat.getOccupiedSquares()) {
							for (Square blockSquare : blockedSquares) {
								if (blockSquare.getRow() == trySquare.getRow())
									// == 1 for moving E (J > I so positive)
									if (blockSquare.getColumn() - trySquare.getColumn() == 1)
										canMove = false;
							}
						}
						// Passes edge check and adjacency check = ship can move
						if (canMove)
							movingBoat.moveShip(movingBoat, dir);
						blockedSquares.addAll(movingBoat.getOccupiedSquares());
						canMove = true;
					}
				}
			}
			return true;
		case 'W':
			sortNSEWMost(dir, shipsToMove);
			// Try to move each Ship
			for (Ship movingBoat : shipsToMove) {
				// Make sure west-most square is not at edge
				if (movingBoat.getOccupiedSquares().get(0).getColumn() > 'A') {
					// Edge check passes = sub can move
					if (movingBoat.getUnderwater())
						movingBoat.moveShip(movingBoat, dir);
					// Reason not to move: any square is vertically adjacent to another ship's
					else {
						for (Square trySquare : movingBoat.getOccupiedSquares()) {
							for (Square blockSquare : blockedSquares) {
								if (blockSquare.getRow() == trySquare.getRow())
									// == 1 for moving E (J > I so positive)
									if (blockSquare.getColumn() - trySquare.getColumn() == 1)
										canMove = false;
							}
						}
						// Passes edge check and adjacency check = ship can move
						if (canMove)
							movingBoat.moveShip(movingBoat, dir);
						blockedSquares.addAll(movingBoat.getOccupiedSquares());
						canMove = true;
					}
				}
			}
			return true;
		}
		// switch based on dir
		// sort ships by dir to prevent moving ships from blocking other moving ships
		// check
		// If next to edge, can't move
		// Then if Submerged, move
		// Then if not Submerged, if not next to ship in dir of move, can move
		// Move
		return false;
	}

	private List<Ship> sortNSEWMost(char dir, List<Ship> Ships) {
		if (Ships.size() == 1)
			return Ships;
		switch (dir) {
		case 'N':
			// Loop # of ships - 1 times and sort via comparison
			sortNorth(Ships);
			return Ships;
		case 'S':
			sortNorth(Ships);
			Collections.reverse(Ships);
			return Ships;
		case 'E':
			sortWest(Ships);
			Collections.reverse(Ships);
			return Ships;
		case 'W':
			sortWest(Ships);
			return Ships;

		}
		return Ships;
	}

	private void sortWest(List<Ship> Ships) {
		for (int i = 0; i < Ships.size(); i++) {
			for (int j = 0; j < (Ships.size() - 1); j++) {
				if (Ships.get(j).getOccupiedSquares().get(0).getColumn() > Ships.get(j + 1).getOccupiedSquares().get(0)
						.getColumn()) {
					Ship temp = Ships.get(j);
					Ships.set(j, Ships.get(j + 1));
					Ships.set(j + 1, temp);
				}
			}
		}
	}

	private void sortNorth(List<Ship> Ships) {
		for (int i = 0; i < Ships.size(); i++) {
			for (int j = 0; j < (Ships.size() - 1); j++) {
				if (Ships.get(j).getOccupiedSquares().get(0).getRow() < Ships.get(j + 1).getOccupiedSquares().get(0)
						.getRow()) {
					Ship temp = Ships.get(j);
					Ships.set(j, Ships.get(j + 1));
					Ships.set(j + 1, temp);
				}
			}
		}
	}

	public void setSpaceLaser(boolean laser) { // for testing purposes
		isSpaceLaser = laser;
	}

	/*
	 * DO NOT change the signature of this method. It is used by the grading
	 * scripts.
	 */
	public Result attack(int x, char y) {
		Result attackResult = attack(new Square(x, y));
		attacks.add(attackResult);
		return attackResult;
	}

	private Result attack(Square s) {
		var shipsAtLocation = ships.stream().filter(ship -> ship.isAtLocation(s)).collect(Collectors.toList());
		if (attacks.stream().anyMatch(r -> r.getLocation().equals(s))) {
			if (!(shipsAtLocation.size() == 0)) {
				var shipAtLocation = shipsAtLocation.get(0);
				if (!(shipAtLocation.getOccupiedSquares().get(shipAtLocation.getLocationOfCapQuart()).equals(s))) {
					return returnInvalidAtkResult(s);
				}
			} else {
				return returnInvalidAtkResult(s);
			}
		}
		if (shipsAtLocation.size() == 0) {
			var attackResult = new Result(s);
			return attackResult;
		}
		if (!isSpaceLaser) {
			for (int i = 0; i < shipsAtLocation.size(); i++) {
				if (shipsAtLocation.get(i).getUnderwater()) {
					var attackResult = new Result(s);
					return attackResult;
				}
			}
		}
		var hitShip = shipsAtLocation.get(0);
		var attackResult = hitShip.attack(s.getRow(), s.getColumn());
		if (attackResult.getResult() == AtackStatus.SUNK) {
			if (ships.stream().allMatch(ship -> ship.isSunk())) {
				attackResult.setResult(AtackStatus.SURRENDER);
			}
		}
		return attackResult;
	}

	private Result returnInvalidAtkResult(Square s) {
		var attackResult = new Result(s);
		attackResult.setResult(AtackStatus.INVALID);
		return attackResult;
	}

	List<Ship> getShips() {
		return ships;
	}
}
