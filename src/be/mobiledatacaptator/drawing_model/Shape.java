package be.mobiledatacaptator.drawing_model;

import android.graphics.Canvas;
import android.graphics.Point;

public class Shape extends BaseFigure {

	private float l, r, b, o;
	private Point p1, p2;
	private Point centerPoint;
	
	@Override
	public void draw(Canvas canvas) {
		canvas.drawRect(l, b, r, o, getPaint());
	}

	@Override
	public void setStartPoint(Point p) {
		centerPoint = p;
	}

	@Override
	public Boolean addPoint(Point p) {
		p2 = p;

		p1 = new Point();
		p1.x = (centerPoint.x - (p2.x - centerPoint.x));
		p1.y = (centerPoint.y - (p2.y - centerPoint.y));
		bereken();
		return true;
	}

	private void bereken() {
		if (p1.x < p2.x) {
			l = p1.x;
			r = p2.x;
		} else {
			l = p2.x;
			r = p1.x;
		}
		if (p1.y < p2.y) {
			b = p1.y;
			o = p2.y;
		} else {
			b = p2.y;
			o = p1.y;
		}
	}

	@Override
	public String toString() {
		return "Shape";
	}
}
