package be.mobiledatacaptator.drawing_model;

import be.mobiledatacaptator.model.LayerCategory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public abstract class MdcBaseShape {

	private LayerCategory layer;
	
	private Paint paint;
		
	public MdcBaseShape()
	{
		paint = new Paint();
		
		paint.setAntiAlias(true);
		paint.setStrokeWidth(2);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
	}
	
	public abstract void draw(Canvas canvas);
	
	public abstract String toString();

	public abstract Boolean addPoint(Point addPoint);
	
	public abstract void setStartPoint(Point startPoint);
	
	public LayerCategory getLayer() {
		return layer;
	}

	public void setLayer(LayerCategory layer) {
		this.layer = layer;
		this.getPaint().setColor(layer.getColorValue());
	}

	public Paint getPaint() {
		return paint;
	}

	public void setPaint(Paint paint) {
		this.paint = paint;
	}

}
