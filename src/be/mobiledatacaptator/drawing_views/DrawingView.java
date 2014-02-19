package be.mobiledatacaptator.drawing_views;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import be.mobiledatacaptator.drawing_model.FigureType;
import be.mobiledatacaptator.drawing_model.MdcCircle;
import be.mobiledatacaptator.drawing_model.MdcLine;
import be.mobiledatacaptator.drawing_model.MdcRectangle;
import be.mobiledatacaptator.drawing_model.MdcBaseShape;
import be.mobiledatacaptator.model.LayerCategory;

public class DrawingView extends View {

	private MdcBaseShape currentMdcShape;
	private FigureType figureType = FigureType.Line;
	private List<MdcBaseShape> listFigures = new ArrayList<MdcBaseShape>();
	private float startPointX, startPointY, endPointX, endPointY;
	boolean startNewFigure = true;
	private MdcBaseShape activeFigure;
	private Boolean fromCenter = false;
	
	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		currentMdcShape = null;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		try {
			for (MdcBaseShape shape : listFigures) {
				shape.draw(canvas);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("onDraw DrawingView", e.getLocalizedMessage());
		}

	}
	
	public void setFigureType(FigureType figureType) {
		this.figureType = figureType;
		startNewFigure = true;
	}


	
	public boolean onTouch(View view, MotionEvent event) {

		int x = (int) event.getX();
		int y = (int) event.getY();

		Point p = new Point(x, y);
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (startNewFigure) {
				makeNewFigure();
			}
			activeFigure.setStartPoint(p);
			if (fromCenter) {
				p.x = view.getWidth() / 2;
				p.y = view.getHeight() / 2;
				activeFigure.setStartPoint(p);
			}
			break;

		case MotionEvent.ACTION_MOVE:
			activeFigure.addPoint(p);
			invalidate();
			break;

		case MotionEvent.ACTION_UP:
			startNewFigure = activeFigure.addPoint(p);
			invalidate();
			break;

		default:
			return super.onTouchEvent(event);
		}
		return true;
	}

	public void addShapeToList(MdcBaseShape shape) {
		listFigures.add(shape);
	}


	
	private void makeNewFigure() {
		switch (figureType) {
		case Line:
			activeFigure = new MdcLine();
			break;
		case Circle:
			activeFigure = new MdcCircle();
			break;
		case Shape:
			//activeFigure = new MLdcShape();
			break;
		case Multiline:
			//activeFigure = new MdcMultiLine();
			break;

		default:
			break;
		}
		
		listFigures.add(activeFigure);
	}

	public Boolean getFromCenter() {
		return fromCenter;
	}

	public void setFromCenter(Boolean fromCenter) {
		this.fromCenter = fromCenter;
	}

}
