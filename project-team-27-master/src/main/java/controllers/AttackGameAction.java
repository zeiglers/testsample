package controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import cs361.battleships.models.Game;

public class AttackGameAction {

	@JsonProperty
	private Game game;
	@JsonProperty
	private int x;
	@JsonProperty
	private char y;
	@JsonProperty
	private boolean isPulsey;

	public Game getGame() {
		return game;
	}

	public int getActionRow() {
		return x;
	}

	public char getActionColumn() {
		return y;
	}

	public boolean getSonarPulse() {
		return isPulsey;
	}
}
