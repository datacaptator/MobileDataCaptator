package be.mobiledatacaptator.activities;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import be.mobiledatacaptator.R;
import be.mobiledatacaptator.drawing_model.MdcCircle;
import be.mobiledatacaptator.drawing_model.MdcLayer;
import be.mobiledatacaptator.drawing_model.MdcLine;
import be.mobiledatacaptator.drawing_model.MdcRectangle;
import be.mobiledatacaptator.drawing_model.MdcShape;
import be.mobiledatacaptator.drawing_views.DrawingView;
import be.mobiledatacaptator.model.Project;
import be.mobiledatacaptator.model.UnitOfWork;
import be.mobiledatacaptator.utilities.MdcUtil;

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

				String xml = unitOfWork.getDao().getFilecontent(dataLocationDrawing);

				readXmlSaxParser(xml);
				
				drawingView.invalidate();
				
				
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void readXmlSaxParser(String xml) {

		Document dom;
		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			dom = db.parse(new ByteArrayInputStream(xml.getBytes()));
			Element root = dom.getDocumentElement();
			NodeList elements = root.getElementsByTagName("Element");
			for (int i = 0; i < elements.getLength(); i++) {

				Node elementNode = elements.item(i);

				if (elementNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) elementNode;

					Log.e("XML", elementNode.getAttributes().getNamedItem("Type").getNodeValue());
					
					String shapeType = elementNode.getAttributes().getNamedItem("Type").getNodeValue();
					
					if (shapeType.equalsIgnoreCase("Cirkel")) {
						
						MdcLayer layer =  new MdcLayer(eElement.getElementsByTagName("Layer").item(0).getTextContent());
						int radius = Integer.valueOf(eElement.getElementsByTagName("Straal").item(0).getTextContent());
						//eElement.getElementsByTagName("Centrum").item(0).getNodeName();
						int x = Integer.valueOf(eElement.getElementsByTagName("X").item(0).getTextContent());
						int y = Integer.valueOf(eElement.getElementsByTagName("Y").item(0).getTextContent());
		
						MdcCircle circle = new MdcCircle(radius, x, y, layer);
						drawingView.addShapeToList(circle);
					}
					else if (shapeType.equalsIgnoreCase("Polygoon")) {
						
						
						
					}
			
					
					
					

				
		
					
					
					
					
					
					
					
					
				
				
				
				
				}
			}

		
		
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		// TODO
		// saveDrawing();
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
