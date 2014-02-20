package be.mobiledatacaptator.drawing_model;

import android.graphics.Canvas;
import android.graphics.Point;

public interface IDrawable {
	void draw(Canvas canvas);

	void setStartPoint(Point p);

	Boolean addPoint(Point p);
}
