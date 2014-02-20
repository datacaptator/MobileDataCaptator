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
import be.mobiledatacaptator.drawing_model.IDrawable;
import be.mobiledatacaptator.drawing_model.Circle;
import be.mobiledatacaptator.drawing_model.Line;
import be.mobiledatacaptator.drawing_model.BaseFigure;
import be.mobiledatacaptator.drawing_model.MultiLine;
import be.mobiledatacaptator.drawing_model.Shape;
import be.mobiledatacaptator.model.LayerCategory;
import android.view.View.OnTouchListener;

public class DrawingView extends View implements OnTouchListener {

	private FigureType figureType = FigureType.Line;
	private List<IDrawable> listFigures = new ArrayList<IDrawable>();
	private LayerCategory layer;
	boolean startNewFigure = true;
	private BaseFigure activeFigure;
	private Boolean fromCenter = false;

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnTouchListener(this);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		try {
			for (IDrawable shape : listFigures) {
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
			activeFigure.setLayer(getLayer());

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

	public void addShapeToList(BaseFigure shape) {
		listFigures.add(shape);
	}

	private void makeNewFigure() {
		switch (figureType) {
		case Line:
			activeFigure = new Line();
			break;
		case Circle:
			activeFigure = new Circle();
			break;
		case Shape:
			activeFigure = new Shape();
			break;
		case Multiline:
			activeFigure = new MultiLine();
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

	public LayerCategory getLayer() {
		return layer;
	}

	public void setLayer(LayerCategory layer) {
		this.layer = layer;
	}
	
	public void undo() {
		if (!(listFigures.isEmpty())) {
			listFigures.remove(listFigures.size() - 1);
			invalidate();
		}
	}

}
