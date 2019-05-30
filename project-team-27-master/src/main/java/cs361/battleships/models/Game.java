package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Collections;

import static cs361.battleships.models.AtackStatus.*;

public class Game {

	@JsonProperty
	private Board playersBoard = new Board();
	@JsonProperty
	private Board opponentsBoard = new Board();
	@JsonProperty
	private int numSonarPulsesRemaining = 2;
	@JsonProperty
	private List<Result> sonarPulses1 = new ArrayList<>();
	@JsonProperty
	private List<Result> sonarPulses2 = new ArrayList<>();

	/*
	 * DO NOT change the signature of this method. It is used by the grading
	 * scripts.
	 */
	public boolean placeShip(Ship ship, int x, char y, boolean isVertical) {
		boolean successful = playersBoard.placeShip(ship, x, y, isVertical);
		if (!successful)
			return false;

		boolean opponentPlacedSuccessfully;
		do {
			// AI places random ships, so it might try and place overlapping ships
			// let it try until it gets it right
			opponentPlacedSuccessfully = opponentsBoard.placeShip(ship, randRow(), randCol(), randVertical());
		} while (!opponentPlacedSuccessfully);

		return true;
	}

	public boolean attack(int x, char y, boolean isPulsey) {

		if (isPulsey) { // If the user decided to use a sonar pulse

			if (runSonarPulse(x, y)) return false;

			Result opponentAttackResult;
			do {
				// AI does random attacks, so it might attack the same spot twice
				// let it try until it gets it right
				opponentAttackResult = playersBoard.attack(randRow(), randCol());
			} while (opponentAttackResult.getResult() == INVALID); // Check if placement was valid

			return true;

		}

		else { // Regular attack
			for (int j = 0; j < opponentsBoard.getShips().size(); j++) {
				if(opponentsBoard.getShips().get(j).isSunk())
					playersBoard.setSpaceLaser(true);
			}
			for (int j = 0; j < playersBoard.getShips().size(); j++) {
				if(playersBoard.getShips().get(j).isSunk())
					opponentsBoard.setSpaceLaser(true);
			}
			attack(x,y);
		}

		return true;
	}

	public boolean runSonarPulse(int x, char y) {
		if (numSonarPulsesRemaining != 0) {

			Result playerAttack = opponentsBoard.attack(x, y);
			if (playerAttack.getResult() == INVALID) { // Check if placement was valid
				return true;
			}

			else {
				Square[] coordinatesToCheck = new Square[13]; // declares new array of coordinates to check
				createCoordinatesToCheck(x, y, coordinatesToCheck); // Sets the list of coordinates to check from
																	// passed
																	// in col/row values

				List<Square> validSonarCoordinates = new ArrayList<>();

				int validCount = 0; // This is a counter for the number of valid spots to check

				for (int i = 0; i < 13; i++) { // Loops 13 times because that's how many possible spots there are

					if (!coordinatesToCheck[i].isOutOfBounds()) { // Runs the out of bounds check method in square

						// Add to valid coordinates
						validSonarCoordinates.add(coordinatesToCheck[i]); // Adds the current pair of coordinates to the "valid"
																			// coordinates (coordinates that are in bounds)
						validCount++; // Call this value later to iterate correctly through validSonarCoordinates array
					}

				}

				if (numSonarPulsesRemaining == 2) {
					sonarPulsesCheck(validSonarCoordinates, validCount, sonarPulses1);
				}
				else if (numSonarPulsesRemaining == 1) {
					sonarPulsesCheck(validSonarCoordinates, validCount, sonarPulses2);
				}

			}
		}
		return false;
	}

	private void sonarPulsesCheck(List<Square> validSonarCoordinates, int validCount, List<Result> sonarPulses) {
		for (int i = 0; i < validCount; i++) {

			// Check if current cordinates at index have a ship
			// Check if current square at index exists in occupiedsquares
			for (int j = 0; j < opponentsBoard.getShips().size(); j++) { // for the number of ships on the board...
				// for the size of the ship
				for (int k = 0; k < opponentsBoard.getShips().get(j).getOccupiedSquares().size(); k++) {

					// Check if current square exists in getOccipiedSquare
					int row = validSonarCoordinates.get(i).getRow();
					char column = validSonarCoordinates.get(i).getColumn();
					Square s = new Square(row, column);

					if (opponentsBoard.getShips().get(j).getOccupiedSquares().contains(s)) {
						Result result = new Result(s);
						result.setResult(AtackStatus.OCCUPIED);
						sonarPulses.add(result);

					} else {
						Result result = new Result(s);
						result.setResult(AtackStatus.FREE);
						sonarPulses.add(result);
					}
				}
			}
		}
	}

	/*
	 * DO NOT change the signature of this method. It is used by the grading
	 * scripts.
	 */
	public boolean attack(int x, char y) {
		Result playerAttack = opponentsBoard.attack(x, y);
		if (playerAttack.getResult() == INVALID) {
			return false;
		}

		Result opponentAttackResult;
		do {
			// AI does random attacks, so it might attack the same spot twice
			// let it try until it gets it right
			opponentAttackResult = playersBoard.attack(randRow(), randCol());
		} while (opponentAttackResult.getResult() == INVALID);

		return true;
	}

	private void createCoordinatesToCheck(int x, char y, Square[] coordinatesToCheck) {
		// Hardcode what to check ordered by 'y' coordinates starting from "y-2" and ending at "y+2"

		// y-2
		y -= 2;
		coordinatesToCheck[0] = new Square(x, y);

		// y-1
		y++;
		coordinatesToCheck[1] = new Square(x, y);
		coordinatesToCheck[2] = new Square(x + 1, y);
		coordinatesToCheck[3] = new Square(x - 1, y);

		// y
		y++;
		coordinatesToCheck[4] = new Square(x, y);
		coordinatesToCheck[5] = new Square(x + 1, y);
		coordinatesToCheck[6] = new Square(x + 2, y);
		coordinatesToCheck[7] = new Square(x - 1, y);
		coordinatesToCheck[8] = new Square(x - 2, y);

		// y+1
		y++;
		coordinatesToCheck[9] = new Square(x, y);
		coordinatesToCheck[10] = new Square(x + 1, y);
		coordinatesToCheck[11] = new Square(x - 1, y);

		// y+2
		y++;
		coordinatesToCheck[12] = new Square(x, y);
	}

	private char randCol() {
		int random = new Random().nextInt(10);
		return (char) ('A' + random);
	}

	private int randRow() {
		return new Random().nextInt(10) + 1;
	}

	private boolean randVertical() {
		return new Random().nextBoolean();
	}
}
