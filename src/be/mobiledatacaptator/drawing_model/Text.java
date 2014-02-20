package be.mobiledatacaptator.drawing_model;

import org.w3c.dom.Document;

import android.graphics.Canvas;
import android.graphics.Point;
import be.mobiledatacaptator.model.LayerCategory;

public class Text extends BaseFigure {

	private Point point;
	private String text;

	public Text() {
	}

	public Text(String text, int x, int y, LayerCategory layer) {
		setPoint(new Point(x, y));
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
	public void draw(Canvas canvas) {
		canvas.drawText(text, getPoint().x, getPoint().y, getPaint());
	}

	@Override
	public String toString() {
		return text;
	}

	@Override
	public Boolean addPoint(Point point) {
		this.point = point;
		return true;
	}

	@Override
	public void setStartPoint(Point point) {
		this.point = point;

	}

	@Override
	public void appendXml(Document doc) {
		// TODO Auto-generated method stub
		
	}

}
