package be.mobiledatacaptator.activities;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import be.mobiledatacaptator.R;
import be.mobiledatacaptator.drawing_model.Circle;
import be.mobiledatacaptator.drawing_model.FigureType;
import be.mobiledatacaptator.drawing_model.IDrawable;
import be.mobiledatacaptator.drawing_model.Line;
import be.mobiledatacaptator.drawing_model.MultiLine;
import be.mobiledatacaptator.drawing_model.Shape;
import be.mobiledatacaptator.drawing_model.Text;
import be.mobiledatacaptator.drawing_views.DrawingView;
import be.mobiledatacaptator.model.LayerCategory;
import be.mobiledatacaptator.model.Project;
import be.mobiledatacaptator.model.UnitOfWork;
import be.mobiledatacaptator.utilities.MdcUtil;

public class DrawingActivity extends Activity implements OnClickListener, OnItemSelectedListener,
		OnCheckedChangeListener, TextWatcher {
	private Project project;
	private UnitOfWork unitOfWork;
	private Button buttonDrawCircle, buttonDrawLine, buttonDrawShape, buttonDrawMultiLine, buttonDrawUndo,
			buttonDrawText, buttonNextChar;
	private EditText editTextInputText;
	private CheckBox checkBoxCenter;
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
		ArrayAdapter<LayerCategory> adapter = new ArrayAdapter<LayerCategory>(getBaseContext(),
				android.R.layout.simple_spinner_item, project.getLayerCategories());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerLayerCategory.setAdapter(adapter);
		spinnerLayerCategory.setOnItemSelectedListener(this);

		buttonDrawCircle = (Button) findViewById(R.id.buttonDrawCircle);
		buttonDrawLine = (Button) findViewById(R.id.buttonDrawLine);
		buttonDrawShape = (Button) findViewById(R.id.buttonDrawShape);
		buttonDrawMultiLine = (Button) findViewById(R.id.buttonDrawMultiLine);
		buttonDrawUndo = (Button) findViewById(R.id.buttonDrawUndo);
		buttonDrawText = (Button) findViewById(R.id.buttonDrawText);
		buttonNextChar = (Button) findViewById(R.id.buttonNextChar);
		checkBoxCenter = (CheckBox) findViewById(R.id.checkBoxCenter);
		editTextInputText = (EditText) findViewById(R.id.editTextInputText);

		buttonDrawCircle.setOnClickListener(this);
		buttonDrawLine.setOnClickListener(this);
		buttonDrawShape.setOnClickListener(this);
		buttonDrawMultiLine.setOnClickListener(this);
		buttonDrawUndo.setOnClickListener(this);
		buttonDrawText.setOnClickListener(this);
		buttonNextChar.setOnClickListener(this);
		checkBoxCenter.setOnCheckedChangeListener(this);
		editTextInputText.addTextChangedListener(this);

		setTitle(MdcUtil.setActivityTitle(unitOfWork, getApplicationContext()));

		// format prefixFicheDrawingName = PUT3014
		prefixFicheDrawingName = getIntent().getExtras().getString("prefixFicheDrawingName");
		dataLocationDrawing = project.getDataLocation() + prefixFicheDrawingName + ".txt";
		
		drawingView.setDrawingActivity(this);

		checkBoxCenter.setChecked(true);
		buttonDrawLine.setTextColor(Color.GREEN);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return (true);
		}
		return (super.onOptionsItemSelected(item));
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

	private int getScaledValue(int i) {
		float uit = i / (float) project.getDrawingSize();
		uit *= (float) drawingView.getMeasuredWidth();
		return (int) uit;
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
						LayerCategory layer = returnLayer((eElement.getElementsByTagName("Layer").item(0)
								.getTextContent()));
						int radius = Integer.valueOf(eElement.getElementsByTagName("Straal").item(0).getTextContent());
						int x = Integer.valueOf(eElement.getElementsByTagName("X").item(0).getTextContent());
						int y = Integer.valueOf(eElement.getElementsByTagName("Y").item(0).getTextContent());

						Circle circle = new Circle(getScaledValue(radius), getScaledValue(x), getScaledValue(y), layer);
						drawingView.addShapeToList(circle);
					} else if (drawingType.equalsIgnoreCase("Polygoon")) {

						LayerCategory layer = returnLayer((eElement.getElementsByTagName("Layer").item(0)
								.getTextContent()));
						boolean closedLine = false;
						closedLine = eElement.getElementsByTagName("Gesloten").item(0).getTextContent()
								.equalsIgnoreCase("ja");

						if (closedLine) {

							Shape shape = new Shape();
							shape.setLayer(layer);

							NodeList punten = eElement.getElementsByTagName("Punt");
							Point startPoint = null;
							Point endPoint = null;

							for (int pnt = 0; pnt < punten.getLength(); pnt++) {
								Node puntNode = punten.item(pnt);

								Element ePunt = (Element) puntNode;

								if (pnt == 0) {

									int x = Integer.valueOf(ePunt.getElementsByTagName("X").item(0).getTextContent());
									int y = Integer.valueOf(ePunt.getElementsByTagName("Y").item(0).getTextContent());

									startPoint = new Point(getScaledValue(x), getScaledValue(y));

									shape.setXMLStartPoint(startPoint);

								} else if (pnt == 2) {
									int x = Integer.valueOf(ePunt.getElementsByTagName("X").item(0).getTextContent());
									int y = Integer.valueOf(ePunt.getElementsByTagName("Y").item(0).getTextContent());

									endPoint = new Point(getScaledValue(x), getScaledValue(y));
									shape.setXMLEndPoint(endPoint);

								}
							}

							drawingView.addShapeToList(shape);
							shape = null;

						} else // Line
						{
							NodeList punten = eElement.getElementsByTagName("Punt");
							Point startPoint = null;
							Point endPoint = null;

							Line line = new Line();
							line.setLayer(layer);

							for (int pnt = 0; pnt < punten.getLength(); pnt++) {
								Node puntNode = punten.item(pnt);

								Element ePunt = (Element) puntNode;

								if (pnt % 2 == 0) {

									int x = Integer.valueOf(ePunt.getElementsByTagName("X").item(0).getTextContent());
									int y = Integer.valueOf(ePunt.getElementsByTagName("Y").item(0).getTextContent());
									startPoint = new Point(getScaledValue(x), getScaledValue(y));
									line.setStartPoint(startPoint);
								} else {
									int x = Integer.valueOf(ePunt.getElementsByTagName("X").item(0).getTextContent());
									int y = Integer.valueOf(ePunt.getElementsByTagName("Y").item(0).getTextContent());

									endPoint = new Point(getScaledValue(x), getScaledValue(y));
									line.setEndPoint(endPoint);
								}
							}

							drawingView.addShapeToList(line);

						}

					} else if (drawingType.equalsIgnoreCase("MultiLine")) {
						LayerCategory layer = returnLayer((eElement.getElementsByTagName("Layer").item(0)
								.getTextContent()));

						MultiLine multiLine = new MultiLine();
						multiLine.setLayer(layer);

						NodeList punten = eElement.getElementsByTagName("Punt");

						for (int pnt = 0; pnt < punten.getLength(); pnt++) {
							Node puntNode = punten.item(pnt);

							Element ePunt = (Element) puntNode;

							int x = Integer.valueOf(ePunt.getElementsByTagName("X").item(0).getTextContent());
							int y = Integer.valueOf(ePunt.getElementsByTagName("Y").item(0).getTextContent());

							multiLine.addPoint(new Point(getScaledValue(x), getScaledValue(y)));
						}

						drawingView.addShapeToList(multiLine);
						multiLine = null;

					} else if (drawingType.equalsIgnoreCase("Tekst")) {
						LayerCategory layer = returnLayer((eElement.getElementsByTagName("Layer").item(0)
								.getTextContent()));
						String text = eElement.getElementsByTagName("Tekst").item(0).getTextContent();
						int x = Integer.valueOf(eElement.getElementsByTagName("X").item(0).getTextContent());
						int y = Integer.valueOf(eElement.getElementsByTagName("Y").item(0).getTextContent());

						Text mdcText = new Text(text, getScaledValue(x), getScaledValue(y), layer);
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
			Log.e("readXmlSaxParser_IOException", e.getLocalizedMessage());
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		saveDrawing();
	}

	public void loadDrawing() {
		try {
			// if drawing exist - loadDrawing
			if (unitOfWork.getDao().existsFile(dataLocationDrawing)) {

				String xml = unitOfWork.getDao().getFilecontent(dataLocationDrawing);

				readXmlSaxParser(xml);
				drawingView.setDrawingActivity(null);

				// invoke onDraw method
				drawingView.invalidate();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("onCreate_DrawingActivity", e.getLocalizedMessage());
		}
	}

	private void saveDrawing() {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			List<IDrawable> iDrawables = drawingView.getiDrawables();
			if (iDrawables.size() > 0) {
				Element rootElement = doc.createElement("ConfiguratieSchets");
				doc.appendChild(rootElement);
				for (IDrawable iDrawable : iDrawables) {
					iDrawable.appendXml(doc, drawingView.getMeasuredWidth(), project.getDrawingSize());
				}

				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer transformer = tf.newTransformer();
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				StringWriter writer = new StringWriter();
				transformer.transform(new DOMSource(doc), new StreamResult(writer));
				String output = writer.getBuffer().toString();
				unitOfWork.getDao().saveStringToFile(dataLocationDrawing, output);

			} else {
				if (unitOfWork.getDao().existsFile(dataLocationDrawing))
					unitOfWork.getDao().delete(dataLocationDrawing);
			}

		} catch (Exception e) {
			MdcUtil.showToastShort(e.getMessage(), this);
		}

	}

	@Override
	public void onClick(View view) {

		try {

			drawingView.setLayer((LayerCategory) spinnerLayerCategory.getSelectedItem());

			if (view.getId() != R.id.buttonDrawUndo) {
				buttonDrawCircle.setTextColor(Color.WHITE);
				buttonDrawLine.setTextColor(Color.WHITE);
				buttonDrawShape.setTextColor(Color.WHITE);
				buttonDrawMultiLine.setTextColor(Color.WHITE);
				buttonDrawText.setTextColor(Color.WHITE);
			}

			switch (view.getId()) {
			case R.id.buttonDrawCircle:
				drawingView.setFigureType(FigureType.Circle);
				buttonDrawCircle.setTextColor(Color.GREEN);
				break;
			case R.id.buttonDrawLine:
				drawingView.setFigureType(FigureType.Line);
				buttonDrawLine.setTextColor(Color.GREEN);
				break;
			case R.id.buttonDrawShape:
				drawingView.setFigureType(FigureType.Shape);
				buttonDrawShape.setTextColor(Color.GREEN);
				break;
			case R.id.buttonDrawMultiLine:
				drawingView.setFigureType(FigureType.Multiline);
				buttonDrawMultiLine.setTextColor(Color.GREEN);
				break;
			case R.id.buttonDrawUndo:
				drawingView.undo();
				break;
			case R.id.buttonDrawText:
				drawingView.setFigureType(FigureType.Text);
				buttonDrawText.setTextColor(Color.GREEN);
				break;
			case R.id.buttonNextChar:
				nextChar();
				drawingView.setFigureType(FigureType.Text);
				buttonDrawText.setTextColor(Color.GREEN);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("ButttonClick", e.getLocalizedMessage());
		}

	}

	private void nextChar() {
		String s = editTextInputText.getText().toString();
		if (s.length() == 1) {
			int i = s.charAt(0);
			i++;
			if (i == 91)
				i = 65;
			editTextInputText.setText(String.valueOf((char) i));
		} else {
			editTextInputText.setText("X");
		}

	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int selelectctedItem, long arg3) {
		drawingView.setLayer((LayerCategory) spinnerLayerCategory.getItemAtPosition(selelectctedItem));
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		drawingView.setFromCenter(false);
		if (isChecked)
			drawingView.setFromCenter(true);

	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		drawingView.setInputText(editTextInputText.getText().toString());
	}

}
