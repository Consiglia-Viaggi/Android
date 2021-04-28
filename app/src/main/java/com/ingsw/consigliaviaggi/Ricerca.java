package com.ingsw.consigliaviaggi;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Ricerca extends ToolbarActivity {
    Toast activityToast;
    static public boolean shouldAllowEmptySearchTerm = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ricerca);
        super.installBackButtonIfNeeded();

    }
    @Override
    public void onResume() {
        super.onResume();
        if (CVUtility.hasConfiguredSwitches || CVUtility.hasConfiguredSeekBar)
            Objects.requireNonNull(getSupportActionBar()).setTitle("Ricerca con filtri attivi");
        else
            Objects.requireNonNull(getSupportActionBar()).setTitle("Ricerca");
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        MenuItem item = bottomNavigationView.getMenu().findItem(R.id.action_profilo);
        if (CVUser.isLogged)
            item.setTitle("Profilo");
        else
            item.setTitle("Accedi");
    }
    public void goButtonTapped(View view){
        super.hideKeyboard();
        boolean shouldShowAlert = true;
        EditText editText = findViewById(R.id.editText7);
        if (shouldAllowEmptySearchTerm || editText != null) {
            String text = editText.getText().toString();
            if (shouldAllowEmptySearchTerm || text != null && text.length() > 0) {
                final HashMap<String, String> map = new HashMap<String, String>();
                final CVDownloader downloader = new CVDownloader(this,"https://consigliaviaggi.altervista.org/wp-content/scripts/structures.php", this);
                map.put("action", "1");
                //map.put("includeImages", "1");
                map.put("getReviews", "1");
                map.put("getAllReviews", "1");
                if (text.length() > 0)
                    map.put("search", text); // Ritorna nome, latitudine e longitudine struttura per un dato nome.
                boolean hasServices = false, hasSeekBarValues = false;
                if (CVUtility.hasConfiguredSeekBar) {
                    for (int i = 0; i < CVUtility.progresses.length; i++) {
                        if (CVUtility.progresses[i] > 0) {
                            map.put(CVUtility.progressNames[i], String.valueOf(CVUtility.progresses[i]));
                            hasSeekBarValues = true;
                        }
                    }
                    if (MainPosizione.loc != null) {
                        map.put("lat", String.valueOf(MainPosizione.loc.getLatitude()));
                        map.put("lon", String.valueOf(MainPosizione.loc.getLongitude()));
                    }
                }
                if (CVUtility.hasConfiguredSwitches) {
                    for (int i = 0; i < CVUtility.switchNames.length; i++) {
                        if (CVUtility.switches[i]) {
                            map.put(CVUtility.switchNames[i], "1");
                            hasServices = true;
                        }
                    }
                    if (hasServices)
                        map.put("services", "1");
                }
                if ((text.length() == 0 && shouldAllowEmptySearchTerm) && (hasServices || hasSeekBarValues)) {
                    final Ricerca context = this;
                    shouldShowAlert = false;
                    AlertDialog.Builder builder = new AlertDialog.Builder(this); // Costruisci una nuova finestra di dialogo.
                    builder.setTitle("Non hai inserito nulla da cercare."); // Imposta il titolo.
                    builder.setMessage("In questo caso saranno mostrate tutte le strutture esistenti, che rispettano i filtri impostati. Continuare?"); // Imposta il messaggio.
                    builder.setCancelable(false).setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    downloader.injectMap(map);
                                    activityToast = Toast.makeText(context, "Caricamento in corso...", Toast.LENGTH_LONG);
                                    activityToast.show();
                                    downloader.execute();
                                }
                            }).setNegativeButton("NO",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                }
                            });
                    builder.show(); // Mostra la finestra di dialogo
                }
                else if (text.length() > 0) {
                    downloader.injectMap(map);
                    activityToast = Toast.makeText(this, "Caricamento in corso...", Toast.LENGTH_LONG);
                    activityToast.show();
                    downloader.execute();
                    shouldShowAlert = false;
                }
            }
        }
        if (shouldShowAlert)
            CVUtility.showAlertWithTitleAndMessageFromContext("Campo di ricerca non valido", "Inserisci un nome di una struttura da ricercare e riprova.", this);
    }
    public void advancedSearchButtonTapped(View view){
        CVUtility.hasNewFilters = false;
        Intent activity = new Intent(this,RicercaAvanzata.class);
        startActivity(activity);
    }
    @Override
    public void onResponseReceived(JSONObject obj) throws JSONException {
        String value = obj.getString("failed");
        if (value.equals("0")) {
            String action = obj.getString("action");
            if (action.equals("1")) {
                JSONArray arr = obj.getJSONArray("places");
                if (arr.length() == 0) {
                    CVUtility.showAlertWithTitleAndMessageFromContext("Nessun risultato trovato.", "Prova ad eseguire una nuova ricerca.", this);
                    return;
                }
                ArrayList<CVStructure> places = new ArrayList<CVStructure>();
                for (int i = 0; i < arr.length(); i++) {
                    JSONArray subArray = arr.getJSONArray(i);
                    String id = subArray.getString(0);
                    String name = subArray.getString(1);
                    Double latitude = subArray.getDouble(2);
                    Double longitude = subArray.getDouble(3);
                    int price_level = subArray.getInt(4);
                    double reviews_average = subArray.getDouble(5);
                    String description = subArray.getString(8);
                    String phoneNumber = subArray.getString(9);
                    JSONArray imageArray = subArray.getJSONArray(6);
                    CVStructure structure = new CVStructure(id, name, description, phoneNumber, latitude, longitude);
                    if (imageArray != null && imageArray.length() > 0) {
                        String encodedImage = imageArray.getString(0);
                        if (encodedImage != null) {
                            byte[] imageAsBytes = Base64.decode(encodedImage.getBytes(), Base64.DEFAULT);
                            if (imageAsBytes != null) {
                                Bitmap bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                                if (bmp != null) {
                                    structure.setImage(bmp);
                                    structure.setImageRepresentation(encodedImage);
                                }
                            }
                        }
                    }
                    structure.price_level = price_level;
                    structure.reviews_average = reviews_average;
                    for (int j = 0; j < imageArray.length(); j++) {
                        structure.setImageRepresentationInArray(imageArray.getString(j));
                    }
                    if (subArray.length() > 7) {
                        JSONArray reviewsArray = subArray.getJSONArray(7);
                        ArrayList<CVReview> reviews = new ArrayList<CVReview>();
                        for (int j = 0; j < reviewsArray.length(); j++) {
                            JSONArray subReviewsArray = reviewsArray.getJSONArray(j);
                            CVReview review = new CVReview(subReviewsArray.getInt(0), subReviewsArray.getInt(1), subReviewsArray.getString(2), subReviewsArray.getInt(3), subReviewsArray.getString(4), subReviewsArray.getString(5), subReviewsArray.getString(6));
                            review.isApproved = subReviewsArray.getInt(7);
                            structure.reviews.add(review);
                        }
                    }
                    places.add(structure);
                }
                activityToast.cancel();
                Intent activity = new Intent(this, RisultatoRicerca.class);
                activity.putExtra("places", places);
                startActivity(activity);
            }
        }
        else
            CVUtility.showAlertWithTitleAndMessageFromContext("Si è verificato un errore.", value, this);
    }
}
