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
import be.mobiledatacaptator.drawing_model.Text;
import be.mobiledatacaptator.drawing_model.MultiLine;
import be.mobiledatacaptator.drawing_model.Shape;
import be.mobiledatacaptator.model.LayerCategory;
import android.view.View.OnTouchListener;


public class DrawingView extends View implements OnTouchListener {


	private FigureType figureType = FigureType.Line;
	private List<IDrawable> iDrawables = new ArrayList<IDrawable>();
	private LayerCategory layer;
	boolean startNewFigure = true;
	private BaseFigure activeFigure;
	private Boolean fromCenter = false;
	private String inputText;

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnTouchListener(this);
		
	}

	public void setFigureType(FigureType figureType) {
		this.figureType = figureType;
		startNewFigure = true;
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

	@Override
	protected void onDraw(Canvas canvas) {
		try {
			for (IDrawable figure : iDrawables) {
				figure.draw(canvas);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("onDraw DrawingView", e.getLocalizedMessage());
		}
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
			
			if (activeFigure instanceof Text) {
			Log.e("tekst", inputText);
				
				Text textInput = (Text) activeFigure;
				textInput.setText(inputText);
				
				Log.e("tekst", textInput.toString());
			}
			
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
		iDrawables.add(shape);
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
		case Text:
			activeFigure = new Text();
			break;
		default:
			break;
		}

		iDrawables.add(activeFigure);
	}

	public void undo() {
		if (!(iDrawables.isEmpty())) {
			iDrawables.remove(iDrawables.size() - 1);
			invalidate();
		}
	}

	public void setInputText(String inputText) {
		this.inputText = inputText;
	}

	public List<IDrawable> getiDrawables() {
		return iDrawables;
	}

	
}
