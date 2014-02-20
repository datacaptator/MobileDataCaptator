package be.mobiledatacaptator.drawing_model;

import org.w3c.dom.Document;

import be.mobiledatacaptator.model.LayerCategory;
import android.graphics.Canvas;
import android.graphics.Point;
public class Line extends BaseFigure {
	private Point startPoint;
	private Point endPoint;
		
	public Line(){};
	
	public Line(LayerCategory layer, Point startPoint, Point endPoint)
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

	@Override
	public Boolean addPoint(Point point) {
		this.endPoint = point;
		return true;
	}

	@Override
	public void appendXml(Document doc) {
		// TODO Auto-generated method stub
		
	}

	
	
	
}
