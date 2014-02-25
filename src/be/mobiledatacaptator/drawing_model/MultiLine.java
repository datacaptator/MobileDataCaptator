package be.mobiledatacaptator.drawing_model;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;

public class MultiLine extends BaseFigure {
	List<Point> punten = new ArrayList<Point>();

	@Override
	public void draw(Canvas canvas) {
		Path path = new Path();
		path.moveTo(punten.get(0).x, punten.get(0).y);
		for (Point p : punten) {
			path.lineTo(p.x, p.y);
		}

		canvas.drawPath(path, getPaint());
	}

	@Override
	public void setStartPoint(Point p) {
		punten.add(p);
	}

	@Override
	public Boolean addPoint(Point p) {
		punten.add(p);
		return false;
	}

	@Override
	public String toString() {
		return null;
	}

	@Override
	public void appendXml(Document doc) {
		Element element = doc.createElement("Element");
		element.setAttribute("Type", "MultiLine");
		doc.getFirstChild().appendChild(element);

		Element layer = doc.createElement("Layer");
		layer.appendChild(doc.createTextNode(this.getLayer().toString()));
		element.appendChild(layer);
	
		for (int i = 1; i < punten.size(); i++) {

			Element point = doc.createElement("Punt");
			Element x = doc.createElement("X");
			x.appendChild(doc.createTextNode(String.valueOf(punten.get(i - 1).x)));
			Element y = doc.createElement("Y");
			y.appendChild(doc.createTextNode(String.valueOf(punten.get(i - 1).y)));
			element.appendChild(point);
			point.appendChild(x);
			point.appendChild(y);

		}

	}

}
