package com.ingsw.consigliaviaggi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MainPosizione extends ToolbarActivity implements LocationListener {
    static boolean isLocationServiceOn = false;
    private GoogleMap map;
    private static final int PERMISSION_CODE = 101;
    String[] permissions_all = {Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};
    LocationManager locationManager;
    boolean isGpsLocation;
    static Location loc;
    boolean isNetworklocation;
    ProgressDialog progressDialog;
    Toast activityToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_posizione);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Home");
        final BottomNavigationView bottomNavigationView1 = findViewById(R.id.bottom_row1);
        if (bottomNavigationView1 != null) {
            bottomNavigationView1.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_hotel:
                            openHotel(item);
                            break;
                        case R.id.action_camping:
                            openCamping(item);
                            break;
                        case R.id.action_restaurant:
                            openRestaurant(item);
                            break;
                    }
                    return true;
                }
            });
        }
        final BottomNavigationView bottomNavigationView2 = findViewById(R.id.bottom_row2);
        if (bottomNavigationView2 != null) {
            bottomNavigationView2.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_village:
                            openVillage(item);
                            break;
                        case R.id.action_bb:
                            openBB(item);
                            break;
                        case R.id.action_relax:
                            openRelax(item);
                            break;
                    }
                    return true;
                }
            });
        }
        progressDialog=new ProgressDialog(MainPosizione.this);
        progressDialog.setMessage("Fetching location...");
        MapView mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.getUiSettings().setAllGesturesEnabled(true);
                map.getUiSettings().setZoomControlsEnabled(true);
                map.getUiSettings().setTiltGesturesEnabled(true);
                map.getUiSettings().setScrollGesturesEnabled(true);
                map.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(true);
                getLocation();
            }
        });
    }
    public void searchButtonTapped(MenuItem item) {
        CVUtility.destruct();
        CVUtility.setHasConfiguredSwitches(false);
        Intent activity = new Intent(this,Ricerca.class);
        startActivity(activity);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater(); // ottieni la lista dei menu
        inflater.inflate(R.menu.menu_main_posizione, menu); // aggiungi il menu.
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
        searchButtonTapped(paramMenuItem);
        //super.onBackPressed();
        return true;
    }
    public void openStructuresByType(int cat) {
        HashMap<String, String> map = new HashMap<String, String>();
        CVDownloader downloader = new CVDownloader(this,"https://consigliaviaggi.altervista.org/wp-content/scripts/structures.php", this);
        map.put("action", "1"); // Ritorna nome, latitudine e longitudine struttura per una data categoria.
        map.put("cat", String.valueOf(cat));
        boolean isNewSearchEnabled = true;
        if (isNewSearchEnabled)
            map.put("getAllReviews", "1");
        //map.put("includeImages", "1");
        map.put("getReviews", "1");
        downloader.injectMap(map);
        activityToast = Toast.makeText(this, "Caricamento in corso...", Toast.LENGTH_LONG);
        activityToast.show();
        downloader.execute();
    }
    public void openHotel(MenuItem item) {
        openStructuresByType(1);
    }
    public void openRestaurant(MenuItem item) {
        openStructuresByType(3);
    }
    public void openCamping(MenuItem item) {
        openStructuresByType(2);
    }
    public void openBB(MenuItem item) {
        openStructuresByType(5);
    }
    public void openVillage(MenuItem item) {
        openStructuresByType(4);
    }
    public void openRelax(MenuItem item) {
        openStructuresByType(6);
    }
    @Override
    public void onBackPressed() {

    }
    @Override
    public void apriHome(MenuItem item) {

    }
    private void getLocation() {
        progressDialog.show();
        if(Build.VERSION.SDK_INT>=23){
            if(checkPermission()){
                getDeviceLocation();
            }
            else{
                requestPermission();
            }
        }
        else{
            getDeviceLocation();
        }
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(MainPosizione.this,permissions_all,PERMISSION_CODE);
    }

    private boolean checkPermission() {
        for(int i = 0; i < permissions_all.length; i++) {
            int result = ContextCompat.checkSelfPermission(MainPosizione.this,permissions_all[i]);
            if (result == PackageManager.PERMISSION_GRANTED)
                continue;
            return false;
        }
        return true;
    }
    private void showSettingForLocation() {
        AlertDialog.Builder al=new AlertDialog.Builder(MainPosizione.this);
        al.setTitle("I servizi di posizione non sono attivi");
        al.setMessage("Vuoi attivare i servizi di posizione?");
        al.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        al.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        al.show();
    }

    private void getDeviceLocation() {
        locationManager = (LocationManager)getSystemService(Service.LOCATION_SERVICE);
        isGpsLocation = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworklocation = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(!isGpsLocation && !isNetworklocation) {
            showSettingForLocation();
            getLastlocation();
        }
        else
            getFinalLocation();
    }
    private void getLastlocation() {
        if (locationManager != null) {
            try {
                Criteria criteria = new Criteria();
                String provider = locationManager.getBestProvider(criteria,false);
                Location location=locationManager.getLastKnownLocation(provider);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    showMap();
                    getFinalLocation();
                }
                else {
                    Toast.makeText(this, "Permessi falliti", Toast.LENGTH_SHORT).show();
                    progressDialog.cancel();
                    hideMap();
                }
                break;
        }
    }
    static boolean shouldGetLocation = true;
    private void getFinalLocation() {
        if (shouldGetLocation) {
            shouldGetLocation = false;
            getDeviceLocation();
        }
        try {
            if (isNetworklocation) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000*60*1,10,MainPosizione.this);
                if (locationManager!=null) {
                    loc=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (loc!=null) {
                        updateUi(loc);
                    }
                }
            }
            else
            if (isGpsLocation) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000*60*1,10,MainPosizione.this);
                if (locationManager!=null) {
                    loc=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(loc!=null) {
                        updateUi(loc);
                    }
                }
            }
            if (!isGpsLocation && !isNetworklocation) {
                Toast.makeText(this, "Impossibile ottenere la posizione", Toast.LENGTH_SHORT).show();
                hideMap();
            }
        }catch (SecurityException e){
            Toast.makeText(this, "Impossibile ottenere la posizione", Toast.LENGTH_SHORT).show();
            hideMap();
        }

    }
    private void showMap() {
        progressDialog.cancel();
        TextView textView = findViewById(R.id.textView26);
        textView.setText("Scopri i luoghi nelle vicinanze");
        MapView mapView = findViewById(R.id.mapView);
        mapView.setVisibility(View.VISIBLE);
    }
    private void hideMap() {
        progressDialog.cancel();
        TextView textView = findViewById(R.id.textView26);
        textView.setText("Per attivare i servizi di posizione\nrecati nelle impostazioni.");
        MapView mapView = findViewById(R.id.mapView);
        mapView.setVisibility(View.GONE);
    }
    private void updateUi(Location loc) {
        if (loc.getLatitude()==0 && loc.getLongitude()==0) {
            getDeviceLocation();
        }
        else {
            progressDialog.dismiss();
            LatLng latLng = new LatLng(loc.getLatitude(),loc.getLongitude());
            //map.addMarker(new MarkerOptions().position(latLng).title("Posizione attuale"));
            map.setMyLocationEnabled(true);
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f));
        }
        showMap();
        HashMap<String, String> map = new HashMap<String, String>();
        CVDownloader downloader = new CVDownloader(this,"https://consigliaviaggi.altervista.org/wp-content/scripts/structures.php", this);
        map.put("action", "2"); // Ritorna nome, latitudine e longitudine struttura.
        downloader.injectMap(map);
        downloader.execute();
    }
    @Override
    public void onResume() {
        MapView mapView = findViewById(R.id.mapView);
        mapView.onResume();
        CVUtility.hasConfiguredSwitches = false;
        CVUtility.hasConfiguredSeekBar = false;
        super.onResume();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        MenuItem item = bottomNavigationView.getMenu().findItem(R.id.action_profilo);
        if (CVUser.isLogged)
            item.setTitle("Profilo");
        else
            item.setTitle("Accedi");
    }
    @Override
    public void onLocationChanged(Location location) {
        updateUi(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    @Override
    public void onResponseReceived(JSONObject obj) throws JSONException {
        String value = obj.getString("failed");
        if (value.equals("0")) {
            String action = obj.getString("action");
            if (action.equals("2")) {
                JSONArray arr = obj.getJSONArray("places");
                for (int i = 0; i < arr.length(); i++) {
                    JSONArray subArray = arr.getJSONArray(i);
                    String id = subArray.getString(0);
                    String name = subArray.getString(1);
                    Double latitude = subArray.getDouble(2);
                    Double longitude = subArray.getDouble(3);
                    map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(name));
                }
            }
            else
            if (action.equals("1")) {
                JSONArray arr = obj.getJSONArray("places");
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
                    CVStructure structure = new CVStructure(id, name, description, phoneNumber, latitude, longitude);
                    JSONArray imageArray = subArray.getJSONArray(6);
                    if (imageArray.length() > 0) {
                        String encodedImage = imageArray.getString(0);
                        byte[] imageAsBytes = Base64.decode(encodedImage.getBytes(), Base64.DEFAULT);
                        Bitmap bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                        structure.setImage(bmp);
                        structure.setImageRepresentation(encodedImage);
                    }
                    structure.price_level = price_level;
                    structure.reviews_average = reviews_average;
                    for (int j = 0; j < imageArray.length(); j++) {
                        if (j == 4) // ???????????????????????????????????
                            continue;
                        structure.setImageRepresentationInArray(imageArray.getString(j));
                    }
                    if (subArray.length() > 7) {
                        JSONArray reviewsArray = subArray.getJSONArray(7);
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
