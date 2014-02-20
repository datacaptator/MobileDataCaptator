package be.mobiledatacaptator.drawing_model;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Point;
import be.mobiledatacaptator.model.LayerCategory;

public class MdcPolyGone extends BaseFigure {

	boolean closedLine;
	private List<Line> lines;

	public MdcPolyGone(LayerCategory layer, boolean closedLine) {
		this.setLayer(layer);
		setClosedLine(closedLine);
	}

	public boolean isClosedLine() {
		return closedLine;
	}

	public void setClosedLine(boolean closedLine) {
		this.closedLine = closedLine;
	}

	public List<Line> getLines() {
		if (isClosedLine()) {
			List<Line> tmpLines = new ArrayList<Line>();

			for (int i = 0; i < lines.size() - 1; i++) {
				if (lines.get(i).getEndPoint() != lines.get(i + 1).getStartPoint()) {

					Line newLine = new Line( this.getLayer(),lines.get(i).getEndPoint(), lines.get(i + 1).getStartPoint());
					tmpLines.add(newLine);
				}
			}

			if (lines.get(0).getStartPoint() != lines.get(lines.size() - 1).getEndPoint()) {
				lines.add(new Line(this.getLayer(), lines.get(0).getStartPoint(), lines.get(lines.size() - 1).getEndPoint()));
			}

			if (tmpLines.size() > 0) {
				for (Line mdcLine : tmpLines) {
					lines.add(mdcLine);
				}
			}

			return lines;
		} else
			return lines;
	}

	public void setLines(List<Line> lines) {
		this.lines = lines;
	}


	@Override
	public void draw(Canvas canvas) {
		for (Line line : getLines()) {
			line.draw(canvas);
		}

	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean addPoint(Point p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStartPoint(Point p) {
		// TODO Auto-generated method stub
		
	}

}
