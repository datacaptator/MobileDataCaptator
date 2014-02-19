package be.mobiledatacaptator.drawing_model;

import be.mobiledatacaptator.model.LayerCategory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

public class MdcCircle extends MdcShape {
	private Point point;
	private int radius;

	public MdcCircle() {
		setPoint(new Point(100, 100));
		setRadius(40);
	}

	public MdcCircle(int radius, int x, int y, LayerCategory layer) {
		setPoint(new Point(x, y));
		setRadius(radius);
		setLayer(layer);
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	@Override
	public String toString() {
		return "MdcCircle";
	}

	@Override
	public void draw(Canvas canvas, Paint paint) {
		try {
			if (this.getLayer() != null) {

				if (this.getLayer().getLayer().equalsIgnoreCase("Algemeen")) {
					paint.setColor(Color.BLACK);
				} else if (this.getLayer().getLayer().equalsIgnoreCase("Drempel")) {
					paint.setColor(Color.RED);
				} else if (this.getLayer().getLayer().equalsIgnoreCase("Deksel")) {
					paint.setColor(Color.GREEN);
				}
			}
			canvas.drawCircle(getPoint().x, getPoint().y, getRadius(), paint);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("draw MdcCircle", e.getLocalizedMessage());
		}

	}

}
