package be.mobiledatacaptator.drawing_model;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;

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
		// TODO Auto-generated method stub
		
	}

}
