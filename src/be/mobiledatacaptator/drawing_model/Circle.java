package be.mobiledatacaptator.drawing_model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;
import be.mobiledatacaptator.model.LayerCategory;

public class Circle extends BaseFigure {
	private Point point;
	private float radius;

	public Circle() {
	};

	public Circle(int radius, int x, int y, LayerCategory layer) {
		setPoint(new Point(x, y));
		setRadius(radius);
		setLayer(layer);
	}

	@Override
	public Boolean addPoint(Point addPoint) {
		float a = addPoint.x - point.x;
		float b = addPoint.y - point.y;

		radius = (float) Math.sqrt(a * a + b * b);
		return true;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	@Override
	public String toString() {
		return "Circle";
	}

	@Override
	public void draw(Canvas canvas) {
		try {

			canvas.drawCircle(getPoint().x, getPoint().y, getRadius(), getPaint());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("draw MdcCircle", e.getLocalizedMessage());
		}

	}

	@Override
	public void setStartPoint(Point startPoint) {
		this.point = startPoint;

	}

	@Override
	public void appendXml(Document doc) {
		Element circle = doc.createElement("Element");
		circle.setAttribute("Type", "Cirkel");
		doc.getFirstChild().appendChild(circle);
		
		Element layer = doc.createElement("Layer");
		layer.appendChild(doc.createTextNode(this.getLayer().toString()));
		circle.appendChild(layer);
		
		Element straal = doc.createElement("Straal");
		straal.appendChild(doc.createTextNode(String.valueOf((int)this.getRadius())));
		circle.appendChild(straal);
		
		Element centrum = doc.createElement("Centrum");
		Element x = doc.createElement("X");
		x.appendChild(doc.createTextNode(String.valueOf(this.getPoint().x)));
		Element y = doc.createElement("Y");
		y.appendChild(doc.createTextNode(String.valueOf(this.getPoint().y)));
		circle.appendChild(centrum);
		centrum.appendChild(x);
		centrum.appendChild(y);
		
		
		

	}

	

}
