package org.cbase.hackatron;

import android.graphics.Paint;
import android.graphics.Point;

public class Player {

	private String name;
	private Paint paint = new Paint();
	private Point point = new Point(0, 0);
	private int color;
	private Movement movement = Movement.NONE;
	private boolean active;

	public void setMovement(Movement movement) {
		this.movement = movement;
	}

	public void setPosition(int x, int y) {
		point.x = x;
		point.y = y;
	}

	public void turnRight() {
		movement = movement.right();
	}

	public void turnLeft() {
		movement = movement.left();
	}

	public void moveX(int i) {
		point.x += i;
	}

	public void moveY(int i) {
		point.y += i;
	}

	// simple getters and setters

	public int getColor() {
		return paint.getColor();
	}

	public void setColor(int color) {
		paint.setColor(color);
	}

	public Paint getPaint() {
		return paint;
	}

	public void setPaint(Paint paint) {
		this.paint = paint;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Movement getMovement() {
		return movement;
	}

}


