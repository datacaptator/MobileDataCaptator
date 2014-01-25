package be.mobiledatacaptator.activities;

import android.R.color;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TableLayout;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import be.mobiledatacaptator.R;
import be.mobiledatacaptator.adapters.FichePagerAdapter;
import be.mobiledatacaptator.model.ChoiceItem;
import be.mobiledatacaptator.model.DataField;
import be.mobiledatacaptator.model.Group;
import be.mobiledatacaptator.model.Tab;
import be.mobiledatacaptator.model.UIField;
import be.mobiledatacaptator.model.UnitOfWork;
import be.mobiledatacaptator.model.VeldType;
import be.mobiledatacaptator.utilities.MdcUtil;

public class FicheActivity extends FragmentActivity {

    private UnitOfWork unitOfWork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        unitOfWork = UnitOfWork.getInstance();
        
        setTitle(MdcUtil.setActivityTitle(unitOfWork, getApplicationContext()));
        
        LoadTemplate();
        toonFiche();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return (true);
        }

        return (super.onOptionsItemSelected(item));
    }

    private void LoadTemplate() {
        try {
            String xml = unitOfWork.getDao().getFilecontent(unitOfWork.getActiveProject().getTemplate());

            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document dom = db.parse(new ByteArrayInputStream(xml.getBytes()));

            Element root = dom.getDocumentElement();
            NodeList groups = root.getElementsByTagName("Group");
            for (int i = 0; i < groups.getLength(); i++) {
                Node groupNode = groups.item(i);
                Group group = new Group(groupNode.getAttributes().getNamedItem("Name").getNodeValue());
                NodeList tabs = ((Element) groupNode).getElementsByTagName("Tab");
                for (int j = 0; j < tabs.getLength(); j++) {
                    Node tabNode = tabs.item(j);
                    Tab tab = new Tab(tabNode.getAttributes().getNamedItem("Name").getNodeValue());
                    NodeList fields = ((Element) tabNode).getElementsByTagName("Field");
                    for (int k = 0; k < fields.getLength(); k++) {
                        Node fieldNode = fields.item(k);
                        NamedNodeMap attr = fieldNode.getAttributes();
                        DataField dataField = new DataField();
                        if (attr.getNamedItem("Name") != null)
                            dataField.setNaam(attr.getNamedItem("Name").getNodeValue());
                        if (attr.getNamedItem("Label") != null)
                            dataField.setLabel(attr.getNamedItem("Label").getNodeValue());
                        if (attr.getNamedItem("DefaultValue") != null)
                            dataField.setDefaultValue(attr.getNamedItem("DefaultValue").getNodeValue());
                        if (attr.getNamedItem("Required") != null)
                            if (attr.getNamedItem("Required").getNodeValue().equals("Y"))
                                dataField.setRequired(true);

                        if (attr.getNamedItem("Type") != null) {
                            String strType = attr.getNamedItem("Type").getNodeValue();
                            if (strType.equals("Text"))
                                dataField.setType(VeldType.TEXT);
                            if (strType.equals("Choice"))
                                dataField.setType(VeldType.CHOICE);
                            if (strType.equals("Int"))
                                dataField.setType(VeldType.INT);
                            if (strType.equals("Double"))
                                dataField.setType(VeldType.DOUBLE);
                        }

                        NodeList temp = ((Element) fieldNode).getElementsByTagName("Choice");
                        if (temp.getLength() > 0) {
                            NodeList keuzes = ((Element) temp.item(0)).getElementsByTagName("W");
                            for (int l = 0; l < keuzes.getLength(); l++) {
                                Node keuzeNode = keuzes.item(l);
                                dataField.getChoiceItems().add(
                                        new ChoiceItem(Integer.parseInt(keuzeNode.getAttributes().getNamedItem("idn")
                                                .getNodeValue()), keuzeNode.getTextContent()));
                            }
                        }
                        dataField.setUiField(new UIField(this, dataField));
                        tab.getDataFields().add(dataField);
                    }
                    group.getTabs().add(tab);
                }
                unitOfWork.getActiveFiche().getGroups().add(group);
            }

        } catch (Exception e) {
            toonBoodschap(e.getMessage());
        }

    }

    private void toonFiche() {

        setContentView(R.layout.activity_fiche);
        final Context context = this;

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost_Fiche);
        tabHost.setup();

        for (final Group group : unitOfWork.getActiveFiche().getGroups()) {
            TabSpec spec = tabHost.newTabSpec(group.getName());
            spec.setIndicator(group.getName());
            spec.setContent(new TabContentFactory() {

                @Override
                public View createTabContent(String tag) {

                    ViewPager viewPager = new ViewPager(context);
                    viewPager.setId(getUniqueId());

                    FichePagerAdapter fichePagerAdapter = new FichePagerAdapter(getSupportFragmentManager());
                    viewPager.setAdapter(fichePagerAdapter);

                    PagerTitleStrip strip = new PagerTitleStrip(context);
                    ViewPager.LayoutParams layoutParams = new ViewPager.LayoutParams();
                    layoutParams.height = ViewPager.LayoutParams.WRAP_CONTENT;
                    layoutParams.width = ViewPager.LayoutParams.MATCH_PARENT;
                    layoutParams.gravity = Gravity.TOP;
                    strip.setBackgroundResource(color.darker_gray);
                    viewPager.addView(strip, layoutParams);

                    for (Tab tab : group.getTabs()) {
                        TabFragment fragment = new TabFragment();
                        fragment.setTab(tab);
                        fichePagerAdapter.addItem(fragment);
                    }

                    return viewPager;
                }
            });
            tabHost.addTab(spec);
        }

    }

    private int getUniqueId() {
        int i = 0;
        Boolean isUnique = false;
        do {
            i++;
            if (findViewById(i) == null)
                isUnique = true;
        } while (!(isUnique));

        return i;
    }

    private void toonBoodschap(String boodschap) {

        if (boodschap == null || boodschap.equals("")) {
            boodschap = "Niet nader omschreven fout";
        }
        Toast.makeText(getApplicationContext(), boodschap, Toast.LENGTH_SHORT).show();
    }

    public static class TabFragment extends Fragment {

        private Tab tab;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            ScrollView scrollView = new ScrollView(getActivity());
            TableLayout tableLayout = new TableLayout(getActivity());
            if (tab != null) {
                for (DataField dataField : tab.getDataFields()) {
                    tableLayout.addView(dataField.getUiField());
                }
            }
            tableLayout.setColumnStretchable(1, true);
            scrollView.addView(tableLayout);
            return scrollView;

        }

        public Tab getTab() {
            return tab;
        }

        public void setTab(Tab tab) {
            this.tab = tab;
        }

    }
}
