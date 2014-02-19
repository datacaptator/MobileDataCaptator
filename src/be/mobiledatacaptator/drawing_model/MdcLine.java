package be.mobiledatacaptator.drawing_model;

import be.mobiledatacaptator.model.LayerCategory;
import android.graphics.Canvas;
import android.graphics.Point;
public class MdcLine extends MdcShape {
	private Point startPoint;
	private Point endPoint;
		
	public MdcLine(){};
	
	public MdcLine(LayerCategory layer, Point startPoint, Point endPoint)
	{
		setLayer(layer);
		setStartPoint(startPoint);
		setEndPoint(endPoint);
	}
	

	public Point getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(Point startPoint) {
		this.startPoint = startPoint;
	}

	public Point getEndPoint() {
		return endPoint;
	}
	public void setEndPoint(Point endPoint) {
		this.endPoint = endPoint;
	}

	@Override
	public void draw(Canvas canvas) {
			canvas.drawLine(getStartPoint().x, getStartPoint().y, getEndPoint().x, getEndPoint().y, this.getPaint());
		
	}



	@Override
	public String toString() {
		return "MdcLine";
	}

	
	
	
}
