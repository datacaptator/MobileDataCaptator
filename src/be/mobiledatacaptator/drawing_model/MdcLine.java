package be.mobiledatacaptator.drawing_model;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
public class MdcLine extends MdcShape {
	private Point startPoint;
	private Point endPoint;
	@Override
	public void drawShape(Canvas canvas, Paint paint) {
		try {
			canvas.drawLine(getStartPoint().x, getStartPoint().y, getEndPoint().x, getEndPoint().y, paint);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("why", e.getLocalizedMessage());
		}
	}
	
	
	public MdcLine(){};
	
	public MdcLine(Point startPoint, Point endPoint)
	{
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
	public String toString() {
		return "MdcLine";
	}


	/* (non-Javadoc)
	 * @see com.example.mydrawingapp.shapes.MdcShape#area()
	 */
	@Override
	public double area() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	
	
	
}