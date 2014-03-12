package be.mobiledatacaptator.drawing_model;

import org.w3c.dom.Document;

import android.graphics.Canvas;
import android.graphics.Point;

public interface IDrawable {
	void draw(Canvas canvas);

	void setStartPoint(Point p);

	Boolean addPoint(Point p);
	
	void appendXml(Document	doc, float screensize, float drawingsize);
}
