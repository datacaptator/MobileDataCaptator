package be.mobiledatacaptator.activities;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
				
				//ArrayList<Project> projects = new ArrayList<Project>();

				String xml = unitOfWork.getDao().getFilecontent(dataLocationDrawing);
				DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document dom = db.parse(new ByteArrayInputStream(xml.getBytes()));
				Element root = dom.getDocumentElement();
				NodeList elements = root.getElementsByTagName("Element");
				for (int i = 0; i < elements.getLength(); i++) {
					Node elementNode = elements.item(i);
					
					if (elementNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element)elementNode;
					
						Log.e("XML", elementNode.getAttributes().getNamedItem("Type").getNodeValue());
						Log.e("Layer", eElement.getElementsByTagName("Layer").item(0).getTextContent());
						Log.e("Layer", eElement.getElementsByTagName("Straal").item(0).getTextContent());
						Log.e("Layer", eElement.getElementsByTagName("Centrum").item(0).getTextContent());
						Log.e("Layer", eElement.getElementsByTagName("Layer").item(0).getTextContent());
						
						
						
					}
					
					
					
					
					
					
					
					
//					myProject.setName(projectNode.getAttributes().getNamedItem("Name").getNodeValue());
//					myProject.setTemplate(projectNode.getAttributes().getNamedItem("Template").getNodeValue());
//
//					projects.add(myProject);
				}
			
			

				
				
				
				
			}
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		//TODO
		//saveDrawing();
	}
	
	private void saveDrawing() {
		// TODO schrijft tekening weg - nog uit te werken
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
