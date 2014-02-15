package be.mobiledatacaptator.activities;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import be.mobiledatacaptator.R;
import be.mobiledatacaptator.drawing_model.MdcCircle;
import be.mobiledatacaptator.drawing_model.MdcLine;
import be.mobiledatacaptator.drawing_model.MdcRectangle;
import be.mobiledatacaptator.drawing_views.DrawingView;
import be.mobiledatacaptator.model.Fiche;
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
	private DrawingView drawingView;
	private String prefixFicheDrawingName;
	private String dataLocationDrawing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawing);

		unitOfWork = UnitOfWork.getInstance();
		project = unitOfWork.getActiveProject();

		drawingView = (DrawingView) findViewById(R.id.drawingView);

		buttonDrawCircle = (Button) findViewById(R.id.buttonDrawCircle);
		buttonDrawLine = (Button) findViewById(R.id.buttonDrawLine);
		buttonDrawRectangle = (Button) findViewById(R.id.buttonDrawRectangle);
		buttonDrawCircle.setOnClickListener(this);
		buttonDrawLine.setOnClickListener(this);
		buttonDrawRectangle.setOnClickListener(this);

		setTitle(MdcUtil.setActivityTitle(unitOfWork, getApplicationContext()));

		// format prefixFicheDrawingName = PUT3014
		prefixFicheDrawingName = getIntent().getExtras().getString("prefixFicheDrawingName");
		dataLocationDrawing = project.getDataLocation() + prefixFicheDrawingName + ".txt";

		try {
			// if drawing exist - loadDrawing
			if (unitOfWork.getDao().existsFile(dataLocationDrawing)) {
				loadExistingDrawing();
				Log.e("FileExists", dataLocationDrawing);
			}
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		saveDrawing();
	}
	
	private void saveDrawing() {
		// TODO Auto-generated method stub
		try {
			
			
			
			unitOfWork.getDao().saveStringToFile(dataLocationDrawing, "test");

		} catch (Exception e) {
			MdcUtil.showToastShort(e.getMessage(), this);
		}

		
		
		
		
	}

	//
	private void loadExistingDrawing() {
		// TODO Auto-generated method stub

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
