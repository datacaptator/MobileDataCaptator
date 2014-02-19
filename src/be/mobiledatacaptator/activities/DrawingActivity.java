package be.mobiledatacaptator.activities;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
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
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import be.mobiledatacaptator.R;
import be.mobiledatacaptator.drawing_model.MdcCircle;
import be.mobiledatacaptator.drawing_model.MdcLine;
import be.mobiledatacaptator.drawing_model.MdcPolyGone;
import be.mobiledatacaptator.drawing_model.MdcRectangle;
import be.mobiledatacaptator.drawing_model.MdcText;
import be.mobiledatacaptator.drawing_views.DrawingView;
import be.mobiledatacaptator.model.LayerCategory;
import be.mobiledatacaptator.model.Project;
import be.mobiledatacaptator.model.UnitOfWork;
import be.mobiledatacaptator.utilities.MdcUtil;

public class DrawingActivity extends Activity implements OnClickListener, OnItemSelectedListener {
	private Project project;
	private UnitOfWork unitOfWork;
	private Button buttonDrawCircle, buttonDrawLine, buttonDrawRectangle;
	private Spinner spinnerLayerCategory;
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

		spinnerLayerCategory = (Spinner) findViewById(R.id.spinnerLayerCategory);
		ArrayAdapter<LayerCategory> adapter = new ArrayAdapter<LayerCategory>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item,
				project.getLayerCategories());
		spinnerLayerCategory.setAdapter(adapter);
		spinnerLayerCategory.setOnItemSelectedListener(this);

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

				String xml = unitOfWork.getDao().getFilecontent(dataLocationDrawing);

				readXmlSaxParser(xml);

				// invoke onDraw method
				drawingView.invalidate();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("onCreate_DrawingActivity", e.getLocalizedMessage());
		}

	}

	private LayerCategory returnLayer(String layerFromXml) {
		List<LayerCategory> layerCategories = project.getLayerCategories();

		for (LayerCategory layerCategory : layerCategories) {
			if (layerCategory.getLayer().equalsIgnoreCase(layerFromXml)) {
				return layerCategory;
			}
		}
		return null;
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

					String drawingType = elementNode.getAttributes().getNamedItem("Type").getNodeValue();

					if (drawingType.equalsIgnoreCase("Cirkel")) {
						LayerCategory layer = returnLayer((eElement.getElementsByTagName("Layer").item(0).getTextContent()));
						int radius = Integer.valueOf(eElement.getElementsByTagName("Straal").item(0).getTextContent());
						int x = Integer.valueOf(eElement.getElementsByTagName("X").item(0).getTextContent());
						int y = Integer.valueOf(eElement.getElementsByTagName("Y").item(0).getTextContent());

						MdcCircle circle = new MdcCircle(radius, x, y, layer);
						drawingView.addShapeToList(circle);
					} else if (drawingType.equalsIgnoreCase("Polygoon")) {
						LayerCategory layer = returnLayer((eElement.getElementsByTagName("Layer").item(0).getTextContent()));
						boolean closedLine = false;
						closedLine = eElement.getElementsByTagName("Gesloten").item(0).getTextContent().equalsIgnoreCase("ja");

						MdcPolyGone polyGone = new MdcPolyGone(layer, closedLine);
						MdcLine line = null;
						NodeList punten = eElement.getElementsByTagName("Punt");
						Point startPoint = null;
						Point endPoint = null;
						List<MdcLine> lines = new ArrayList<MdcLine>();

						for (int pnt = 0; pnt < punten.getLength(); pnt++) {
							Node puntNode = punten.item(pnt);

							Element ePunt = (Element) puntNode;

							if (pnt % 2 == 0) {

								int x = Integer.valueOf(ePunt.getElementsByTagName("X").item(0).getTextContent());
								int y = Integer.valueOf(ePunt.getElementsByTagName("Y").item(0).getTextContent());

								startPoint = new Point(x, y);
							} else {
								int x = Integer.valueOf(ePunt.getElementsByTagName("X").item(0).getTextContent());
								int y = Integer.valueOf(ePunt.getElementsByTagName("Y").item(0).getTextContent());

								endPoint = new Point(x, y);

								line = new MdcLine(layer, startPoint, endPoint);
								lines.add(line);
							}
						}

						polyGone.setLines(lines);
						lines = null;
						drawingView.addShapeToList(polyGone);
						polyGone = null;

					} else if (drawingType.equalsIgnoreCase("Tekst")) {
						LayerCategory layer = returnLayer((eElement.getElementsByTagName("Layer").item(0).getTextContent()));
						String text = eElement.getElementsByTagName("Tekst").item(0).getTextContent();
						int x = Integer.valueOf(eElement.getElementsByTagName("X").item(0).getTextContent());
						int y = Integer.valueOf(eElement.getElementsByTagName("Y").item(0).getTextContent());

						MdcText mdcText = new MdcText(text, x, y, layer);
						drawingView.addShapeToList(mdcText);
					}

				}
			}

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			Log.e("readXmlSaxParser_ParserConfigurationException", e.getLocalizedMessage());
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			Log.e("readXmlSaxParser_SAXException", e.getLocalizedMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("readXmlSaxParser_SAXException", e.getLocalizedMessage());
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		// TODO
		saveDrawing();
	}

	private void saveDrawing() {
		// TODO schrijft tekening weg - nog uit te werken
		try {

			// unitOfWork.getDao().saveStringToFile(dataLocationDrawing, "test");

		} catch (Exception e) {
			MdcUtil.showToastShort(e.getMessage(), this);
		}

	}

	@Override
	public void onClick(View view) {

		try {
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("ButttonClick", e.getLocalizedMessage());
		}

	}

	
	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO  -> layer bepalen door item waarop geklikt
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

}
