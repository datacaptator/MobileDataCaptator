package be.mobiledatacaptator.activities;

import be.mobiledatacaptator.R;
import be.mobiledatacaptator.drawing_model.MdcCircle;
import be.mobiledatacaptator.drawing_model.MdcLine;
import be.mobiledatacaptator.drawing_model.MdcRectangle;
import be.mobiledatacaptator.drawing_views.DrawingView;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DrawingActivity extends Activity implements OnClickListener {
	
	Button buttonDrawCircle, buttonDrawLine, buttonDrawRectangle;
	DrawingView drawingView ;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawing);
		
		drawingView = (DrawingView) findViewById(R.id.drawingView);
		
		buttonDrawCircle = (Button) findViewById(R.id.buttonDrawCircle);
		buttonDrawLine = (Button) findViewById(R.id.buttonDrawLine);
		buttonDrawRectangle = (Button) findViewById(R.id.buttonDrawRectangle);
		buttonDrawCircle.setOnClickListener(this);
		buttonDrawLine.setOnClickListener(this);
		buttonDrawRectangle.setOnClickListener(this);
		
		
	}

	
	@Override
	public void onClick(View view) {
	
		switch (view.getId()) {
		case R.id.buttonDrawCircle:
			drawingView.setCurrentMdcShape(new MdcCircle());
			break;
		case R.id.buttonDrawLine:
			drawingView.setCurrentMdcShape(new MdcLine());
			break;
		case R.id.buttonDrawRectangle:
			drawingView.setCurrentMdcShape(new MdcRectangle());
			break;			
			
		default:
			break;
		}	
		
		
		
	}

	

	
       

	
	

}
