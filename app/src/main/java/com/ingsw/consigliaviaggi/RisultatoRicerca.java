package com.ingsw.consigliaviaggi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RisultatoRicerca extends ToolbarActivity {
    public CVStructure str = null;
    public ResultsAdapter resultsAdapter;
    private ListView listView;
    public ProgressDialog progressDialog;
    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        MenuItem item = bottomNavigationView.getMenu().findItem(R.id.action_profilo);
        if (CVUser.isLogged)
            item.setTitle("Profilo");
        else
            item.setTitle("Accedi");
    }
    private void updateItemAtPosition(int position) {
        resultsAdapter.notifyDataSetChanged();
        /*int visiblePosition = listView.getFirstVisiblePosition();
        View view = listView.getChildAt(position - visiblePosition);
        resultsAdapter.getView(position, view, listView);*/

    }
    public void fillImages() {
        final ArrayList<CVStructure> places = (ArrayList<CVStructure>) getIntent().getSerializableExtra("places");
        int capacity = places.size();
        CVStructure.initStaticResultsToCapacity(capacity);
        for (int i = 0; i < capacity; i++) {
            CVStructure structure = places.get(i);
            String id = String.valueOf(structure.id);
            CVDownloader downloader = new CVDownloader(this, "https://consigliaviaggi.altervista.org/wp-content/scripts/structures.php", this);
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("getMainImageByID", id);
            map.put("index", String.valueOf(i));
            downloader.injectMap(map);
            downloader.execute();
        }
    }
    static boolean needConstraints = true;
    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        if (needConstraints) {
            needConstraints = false;
            fillImages();
            super.onWindowFocusChanged(hasFocus);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        needConstraints = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_risultato_ricerca);
        super.installBackButtonIfNeeded();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Risultati");
        Bundle bundle = getIntent().getExtras();
        if (bundle!= null) {
            if (bundle.containsKey("places")) {
                final ArrayList<CVStructure> places = (ArrayList<CVStructure>) getIntent().getSerializableExtra("places");
                ArrayList<String> names = new ArrayList<String>();
                for (int i = 0; i < places.size(); i++) {
                    CVStructure structure = places.get(i);
                    names.add(structure.name);
                }
                ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
                for (int i = 0; i < places.size(); i++) {
                    CVStructure structure = places.get(i);
                    Map<String, Object> datum = new HashMap<String, Object>(2);
                    if (structure.imageRepresentation != null) {
                        byte[] imageAsBytes = Base64.decode(structure.imageRepresentation.getBytes(), Base64.DEFAULT);
                        Bitmap bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                        datum.put("thumbnail", bmp);
                    }
                    else {
                        Bitmap bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // this creates a MUTABLE bitmap
                        datum.put("thumbnail", bmp);
                    }
                    datum.put("name", structure.name);
                    data.add(datum);
                }
                /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.row, names);
                ListView listView = (ListView) findViewById(R.id.listview);
                listView.setAdapter(new SimpleAdapter(this, data, R.layout.row, new String[] {"thumbnail","name"}, new int[] {R.id.imageRow, R.id.tableRowTitle}));
                */
                resultsAdapter = new ResultsAdapter(this, places);
                listView = (ListView) findViewById(R.id.listview);
                listView.setAdapter(resultsAdapter);
                //fillImages();
                final RisultatoRicerca context = this;
                progressDialog= new ProgressDialog(RisultatoRicerca.this);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> av, View v, int pos,long id) {
                        str = places.get(pos);
                        progressDialog.setMessage("Carico " + str.name);
                        if (str.imageRepresentations.size() > 1) {
                            Intent activity = new Intent(context, PaginaStruttura.class);
                            activity.putExtra("structure", str);
                            startActivity(activity);
                        }
                        else {
                            progressDialog.show();
                            HashMap<String, String> map = new HashMap<String, String>();
                            CVDownloader downloader = new CVDownloader(context, "https://consigliaviaggi.altervista.org/wp-content/scripts/structures.php", context);
                            map.put("action", "3");
                            map.put("id", str.id);
                            downloader.injectMap(map);
                            downloader.execute();
                        }
                    }
                });
            }
        }
    }
    @Override
    public void onResponseReceived(JSONObject obj) throws JSONException {
        String value = obj.getString("failed");
        if (value.equals("0")) {
            if (obj.has("image")) {
                String encodedImage = obj.getString("image");
                int index = obj.getInt("index");
                CVStructure.static_imageResultsRepresentations.add(index, value);
                ArrayList<CVStructure> places = (ArrayList<CVStructure>) getIntent().getSerializableExtra("places");
                CVStructure structure = places.get(index);
                structure.setImageRepresentation(encodedImage);
                updateItemAtPosition(index);
                /*resultsAdapter = new ResultsAdapter(this, places);
                listView = (ListView) findViewById(R.id.listview);
                listView.setAdapter(resultsAdapter);*/
            } else {
                String id = obj.getString("id");
                JSONArray arr = obj.getJSONArray("images");
                CVStructure.static_imageRepresentations.clear();
                if (arr.length() > 1) {
                    for (int i = 1; i < arr.length(); i++) { // La prima immagine è già caricata.
                        String string = arr.getString(i);
                        if (string == null)
                            continue;
                        CVStructure.static_imageRepresentations.add(string);
                    }
                }
                if (progressDialog != null)
                    progressDialog.cancel();
                Intent activity = new Intent(this, PaginaStruttura.class);
                activity.putExtra("structure", str);
                startActivity(activity);
            }
        }
        else
            CVUtility.showAlertWithTitleAndMessageFromContext("Si è verificato un errore.", value, this);
    }
}
