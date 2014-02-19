package be.mobiledatacaptator.drawing_model;

import be.mobiledatacaptator.model.LayerCategory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

public class MdcText extends MdcShape {

	private Point point;
	private String text;
	
	public MdcText(String text, int x, int y, LayerCategory layer)
	{
		setPoint(new Point(x,y));
		setText(text);
		setLayer(layer);
	}
	
	
	
	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	
	@Override
	public void draw(Canvas canvas, Paint paint) {
		canvas.drawText(text, getPoint().x, getPoint().y, paint);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}




	
	
}
