package be.mobiledatacaptator.drawing_model;

import be.mobiledatacaptator.model.LayerCategory;
import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class MdcShape  {

	private  LayerCategory layer;
	
	public abstract void draw(Canvas canvas, Paint paint);

	public abstract String toString();

	public LayerCategory getLayer() {
		return layer;
	}

	public void setLayer(LayerCategory layer) {
		this.layer = layer;
	}

}
