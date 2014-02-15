package be.mobiledatacaptator.activities;

import be.mobiledatacaptator.R;
import be.mobiledatacaptator.drawing_model.MdcCircle;
import be.mobiledatacaptator.drawing_model.MdcLine;
import be.mobiledatacaptator.drawing_model.MdcRectangle;
import be.mobiledatacaptator.drawing_views.DrawingView;
import be.mobiledatacaptator.model.Project;
import be.mobiledatacaptator.model.UnitOfWork;
import be.mobiledatacaptator.utilities.MdcUtil;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DrawingActivity extends Activity implements OnClickListener {
	private Project project;
	private UnitOfWork unitOfWork;
	private Button buttonDrawCircle, buttonDrawLine, buttonDrawRectangle;
	private DrawingView drawingView ;	
	private String prefixFicheDrawingName;
	private String dataLocationDrawing, dataLocationFiche;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawing);
		
		unitOfWork = UnitOfWork.getInstance();
		project = unitOfWork.getActiveProject();
		
		// format prefixFicheDrawingName = PUT3014
		prefixFicheDrawingName = getIntent().getExtras().getString("prefixFicheDrawingName");
		
		dataLocationDrawing = project.getDataLocation() + prefixFicheDrawingName + ".tek";
		dataLocationFiche = project.getDataLocation() + prefixFicheDrawingName + ".xml";
		
		
		try {
			if (unitOfWork.getDao().existsFile(dataLocationDrawing)) {
				Log.e("FileExists", dataLocationDrawing);
			}
			else
			{
				Log.e("NOT EXISTS", dataLocationDrawing);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			if (unitOfWork.getDao().existsFile(dataLocationFiche)) {
				Log.e("FileExists", dataLocationFiche);
			}
			else
			{
				Log.e("NOT EXISTS", dataLocationFiche);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		drawingView = (DrawingView) findViewById(R.id.drawingView);
		
		buttonDrawCircle = (Button) findViewById(R.id.buttonDrawCircle);
		buttonDrawLine = (Button) findViewById(R.id.buttonDrawLine);
		buttonDrawRectangle = (Button) findViewById(R.id.buttonDrawRectangle);
		buttonDrawCircle.setOnClickListener(this);
		buttonDrawLine.setOnClickListener(this);
		buttonDrawRectangle.setOnClickListener(this);
		
		setTitle(MdcUtil.setActivityTitle(unitOfWork, getApplicationContext()));
		
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
