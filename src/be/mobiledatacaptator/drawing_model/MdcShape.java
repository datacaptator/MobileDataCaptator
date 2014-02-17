package be.mobiledatacaptator.drawing_model;

import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class MdcShape {

	private  MdcLayer layer;
	
	public abstract double area();

	public abstract void drawShape(Canvas canvas, Paint paint);

	public abstract String toString();

	public MdcLayer getLayer() {
		return layer;
	}

	public void setLayer(MdcLayer layer) {
		this.layer = layer;
	}

}
