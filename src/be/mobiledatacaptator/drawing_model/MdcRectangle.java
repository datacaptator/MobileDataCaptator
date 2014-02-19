package be.mobiledatacaptator.drawing_model;

import android.graphics.Canvas;
import android.graphics.Point;

public class MdcRectangle extends MdcBaseShape {

	private int left;
	private int top;
	private int right;
	private int bottom;

	public MdcRectangle() {
	}

	public MdcRectangle(int left, int top, int right, int bottom) {
		setLeft(left);
		setTop(top);
		setRight(right);
		setBottom(bottom);
	}

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public int getRight() {
		return right;
	}

	public void setRight(int right) {
		this.right = right;
	}

	public int getBottom() {
		return bottom;
	}

	public void setBottom(int bottom) {
		this.bottom = bottom;
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), getPaint());
	}


	@Override
	public String toString() {
		return null;
	}

	@Override
	public Boolean addPoint(Point p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStartPoint(Point p) {
		// TODO Auto-generated method stub
		
	}

}
