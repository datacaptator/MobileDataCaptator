package be.mobiledatacaptator.drawing_model;

import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class MdcShape {

	public abstract double area();

	public abstract void drawShape(Canvas canvas, Paint paint);

	public abstract String toString();

}
