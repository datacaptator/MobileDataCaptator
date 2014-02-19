package be.mobiledatacaptator.drawing_model;

import java.util.ArrayList;
import java.util.List;

import be.mobiledatacaptator.model.LayerCategory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class MdcPolyGone extends MdcShape {

	boolean closedLine;
	private List<MdcLine> lines;

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

	public List<MdcLine> getLines() {
		if (isClosedLine()) {
			List<MdcLine> tmpLines = new ArrayList<MdcLine>();

			for (int i = 0; i < lines.size() - 1; i++) {
				if (lines.get(i).getEndPoint() != lines.get(i + 1).getStartPoint()) {

					MdcLine newLine = new MdcLine(lines.get(i).getEndPoint(), lines.get(i + 1).getStartPoint());
					tmpLines.add(newLine);
				}
			}

			if (lines.get(0).getStartPoint() != lines.get(lines.size() - 1).getEndPoint()) {
				lines.add(new MdcLine(lines.get(0).getStartPoint(), lines.get(lines.size() - 1).getEndPoint()));
			}

			if (tmpLines.size() > 0) {
				for (MdcLine mdcLine : tmpLines) {
					lines.add(mdcLine);
				}
			}

			return lines;
		} else
			return lines;
	}

	public void setLines(List<MdcLine> lines) {
		this.lines = lines;
	}


	@Override
	public void draw(Canvas canvas, Paint paint) {

		paint.setColor(Color.BLACK);

		for (MdcLine line : getLines()) {
			line.draw(canvas, paint);
		}

	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
