package be.mobiledatacaptator.drawing_model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
		Element line = doc.createElement("Element");
		line.setAttribute("Type", "Polygoon");
		doc.getFirstChild().appendChild(line);
		
		Element layer = doc.createElement("Layer");
		layer.appendChild(doc.createTextNode(this.getLayer().toString()));
		line.appendChild(layer);
		
		Element closed = doc.createElement("Gesloten");
		closed.appendChild(doc.createTextNode("NEEN"));
		line.appendChild(closed);
		
		Element startPoint = doc.createElement("Punt");
		Element sX = doc.createElement("X");
		sX.appendChild(doc.createTextNode(String.valueOf(this.getStartPoint().x)));
		Element sY = doc.createElement("Y");
		sY.appendChild(doc.createTextNode(String.valueOf(this.getStartPoint().y)));
		line.appendChild(startPoint);
		startPoint.appendChild(sX);
		startPoint.appendChild(sY);
		
		Element endPoint = doc.createElement("Punt");
		Element eX = doc.createElement("X");
		eX.appendChild(doc.createTextNode(String.valueOf(this.getEndPoint().x)));
		Element eY = doc.createElement("Y");
		eY.appendChild(doc.createTextNode(String.valueOf(this.getEndPoint().y)));
		line.appendChild(endPoint);
		endPoint.appendChild(eX);
		endPoint.appendChild(eY);
		
	}

	
	
	
}
