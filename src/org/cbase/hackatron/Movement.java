package org.cbase.hackatron;

enum Movement {
	NONE, UP, DOWN, LEFT, RIGHT;

	Movement right() {
		switch (this) {
			case UP: return RIGHT;
			case DOWN: return LEFT;
			case LEFT: return UP;
			case RIGHT: return DOWN;
		}
		return NONE;
	}

	Movement left() {
		switch (this) {
			case UP: return LEFT;
			case DOWN: return RIGHT;
			case LEFT: return DOWN;
			case RIGHT: return UP;
		}
		return NONE;
	}
}
