package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;
import com.mchange.v1.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Ship {

	@JsonProperty
	private String kind;
	@JsonProperty
	private List<Square> occupiedSquares;
	@JsonProperty
	private int size;
	@JsonProperty
	private int locationOfCapQuart;
	@JsonProperty
	private boolean isUnderwater;

	public Ship() {
		occupiedSquares = new ArrayList<>();
	}

	public Ship(String kind) {
		this();
		this.kind = kind;

		isUnderwater = false;

		switch (kind) {
		case "MINESWEEPER":
			size = 2;
			locationOfCapQuart = 0;
			break;
		case "DESTROYER":
			size = 3;
			locationOfCapQuart = 1;
			break;
		case "BATTLESHIP":
			size = 4;
			locationOfCapQuart = 2;
			break;
		}
	}

	public Ship(String kind, Boolean isUnderwater) { // Specifically for Submarine
		this();

		if (kind.equals("SUBMARINE")) { // Extra checking because why not.
			this.kind = kind;

			this.isUnderwater = isUnderwater;

			size = 4;
			locationOfCapQuart = 4;
		}

		// We only send in the flag if it is a submarine which is why there is no else
		// statement.
		// The other calls would go to the normal constructor.

	}

	public List<Square> getOccupiedSquares() {
		return occupiedSquares;
	}

	public void place(char col, int row, boolean isVertical) {
		for (int i = 0; i < size; i++) {
			if (isVertical) {

				if (i == 2 && this.kind.equals("SUBMARINE")) {
					occupiedSquares.add(new Square(row + i, col));
					occupiedSquares.add(new Square(row + i, (char) (col + 1))); // Adds the little blip (TO THE RIGHT)
				}

				else { // This should be default
					occupiedSquares.add(new Square(row + i, col));
				}

			} else { // Horizontal

				if (i == 2 && this.kind.equals("SUBMARINE")) {
					occupiedSquares.add(new Square(row, (char) (col + i)));
					occupiedSquares.add(new Square(row - 1, (char) (col + i))); // Should add the little blip
				}

				else {
					occupiedSquares.add(new Square(row, (char) (col + i)));
				}
			}
		}
		if (size > 2)
			occupiedSquares.get(locationOfCapQuart).setArmor(true);
	}

	public boolean overlaps(Ship other) {
		Set<Square> thisSquares = Set.copyOf(getOccupiedSquares());
		Set<Square> otherSquares = Set.copyOf(other.getOccupiedSquares());
		Sets.SetView<Square> intersection = Sets.intersection(thisSquares, otherSquares);
		return intersection.size() != 0;
	}

	public boolean isAtLocation(Square location) {
		return getOccupiedSquares().stream().anyMatch(s -> s.equals(location));
	}

	public String getKind() {
		return kind;
	}

	public boolean getUnderwater() {
		return isUnderwater;
	}

	public void setUnderwater(boolean underwater) { // for testing
		isUnderwater = underwater;
	}

	public int getLocationOfCapQuart() {
		return locationOfCapQuart;
	}

	public Result attack(int x, char y) {
		var attackedLocation = new Square(x, y);
		var square = getOccupiedSquares().stream().filter(s -> s.equals(attackedLocation)).findFirst();
		var result = new Result(attackedLocation);
		if (square.isPresent()) {
			attackShip(square, result);
		}
		return result;
	}

	private void attackShip(Optional<Square> square, Result result) {
		if (square.get().getArmor())
			square.get().setArmor(false);
		else {
			if (square.get().isHit()) {
				result.setResult(AtackStatus.INVALID);
			} else {
				hitShip(square, result);
			}
		}
	}

	private void hitShip(Optional<Square> square, Result result) {
		square.get().hit();
		result.setShip(this);
		if (isSunk()) {
			result.setResult(AtackStatus.SUNK);
		} else {
			result.setResult(AtackStatus.HIT);
		}
	}

	public boolean moveShip(Ship Boat, char dir){
		switch(dir) {
			case 'N':
					for (Square boatSquare : Boat.getOccupiedSquares())
						boatSquare.setRow(boatSquare.getRow() + 1);
					return true;
			case 'S':
				for (Square boatSquare : Boat.getOccupiedSquares())
					boatSquare.setRow(boatSquare.getRow() - 1);
				return true;
			case 'E':
				for (Square boatSquare : Boat.getOccupiedSquares())
					boatSquare.setColumn((char) (boatSquare.getColumn() + 1));
				return true;
			case 'W':
				for (Square boatSquare : Boat.getOccupiedSquares())
					boatSquare.setColumn((char) (boatSquare.getColumn() - 1));
				return true;
		}
		return false;
	}

	@JsonIgnore
	public boolean isSunk() {
		return occupiedSquares.get(locationOfCapQuart).isHit();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Ship)) {
			return false;
		}
		var otherShip = (Ship) other;

		return this.kind.equals(otherShip.kind) && this.size == otherShip.size
				&& this.occupiedSquares.equals(otherShip.occupiedSquares);
	}

	@Override
	public int hashCode() {
		return 33 * kind.hashCode() + 23 * size + 17 * occupiedSquares.hashCode();
	}

	@Override
	public String toString() {
		return kind + occupiedSquares.toString();
	}
}