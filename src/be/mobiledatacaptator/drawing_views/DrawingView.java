package be.mobiledatacaptator.drawing_views;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import be.mobiledatacaptator.drawing_model.MdcCircle;
import be.mobiledatacaptator.drawing_model.MdcLine;
import be.mobiledatacaptator.drawing_model.MdcRectangle;
import be.mobiledatacaptator.drawing_model.MdcShape;

public class DrawingView extends View {

	private MdcShape currentMdcShape;
	private Paint paint = new Paint();
	private List<MdcShape> listShapes = new ArrayList<MdcShape>();
	private float startPointX, startPointY, endPointX, endPointY;

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(2);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		currentMdcShape = null;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		try {
			for (MdcShape shape : listShapes) {
				shape.draw(canvas, paint);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("onDraw DrawingView", e.getLocalizedMessage());
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		float eventX = event.getX();
		float eventY = event.getY();

		if (currentMdcShape != null) {
			if (currentMdcShape instanceof MdcCircle) {
				MdcCircle myCircle = new MdcCircle();
				myCircle.setPoint(new Point((int) eventX, (int) eventY));
				myCircle.setRadius(40);

				listShapes.add(myCircle);

				Log.e("shape", currentMdcShape.toString());
			} else if (currentMdcShape instanceof MdcLine) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startPointX = eventX;
					startPointY = eventY;
					break;
				case MotionEvent.ACTION_UP:
					endPointX = eventX;
					endPointY = eventY;

					MdcLine myLine = new MdcLine(new Point((int) startPointX, (int) startPointY), new Point((int) endPointX,
							(int) endPointY));
					listShapes.add(myLine);
					// startPointX = startPointY = endPointX = endPointY = 0;
					break;
				default:
					return false;
				}
			} else if (currentMdcShape instanceof MdcRectangle) {
				motionEventChecker(event);

				Log.e("x-coordinate start", String.valueOf(startPointX));
				Log.e("y-coordinate start", String.valueOf(startPointY));
				Log.e("x-coordinate end", String.valueOf(endPointX));
				Log.e("y-coordinate end", String.valueOf(endPointY));

				MdcRectangle myRectangle = new MdcRectangle((int) startPointX, (int) startPointY, (int) endPointX,
						(int) endPointY);
				listShapes.add(myRectangle);
				// startPointX = startPointY = endPointX = endPointY = 0;

			}
		}

		invalidate();
		return true;
	}


	public void addShapeToList(MdcShape shape)
	{
		listShapes.add(shape);
	}
	
	private void motionEventChecker(MotionEvent event) {
		float eventX = event.getX();
		float eventY = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startPointX = eventX;
			startPointY = eventY;

			Log.e("x-coordinate start", String.valueOf(eventX));
			Log.e("y-coordinate start", String.valueOf(eventY));

			break;
		case MotionEvent.ACTION_UP:

			endPointX = eventX;
			endPointY = eventY;

			Log.e("x-coordinate end", String.valueOf(eventX));
			Log.e("y-coordinate end", String.valueOf(eventY));

			break;
		default:
			break;
		}
	}

	public MdcShape getCurrentMdcShape() {
		return currentMdcShape;
	}

	public void setCurrentMdcShape(MdcShape currentMdcShape) {
		this.currentMdcShape = currentMdcShape;
	}

}
