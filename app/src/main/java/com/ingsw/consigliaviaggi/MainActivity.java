package com.ingsw.consigliaviaggi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends CVCompactActivity {
    static public boolean isFirstLaunch = true;
    HashMap<String, String> map = new HashMap<String, String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //CVUser.configureGuestFromContext(this);
        if (isFirstLaunch) {
            isFirstLaunch = false;
            boolean hasData = CVUser.initPreviousDataFromContext(this);
            if (hasData) {
                map.put("mail", CVUser.mail);
                map.put("password", CVUser.password);
                CVDownloader downloader = new CVDownloader(this,"https://consigliaviaggi.altervista.org/wp-content/scripts/check_login.php", this);
                downloader.injectMap(map);
                downloader.execute();
            }
            else {
                setContentView(R.layout.activity_main);
                Objects.requireNonNull(getSupportActionBar()).setTitle("Consiglia Viaggi");
            }
        }
        else {
            setContentView(R.layout.activity_main);
            Objects.requireNonNull(getSupportActionBar()).setTitle("Consiglia Viaggi");
        }
    }
    public void skipButtonTapped(View view) {
        CVUser.configureGuestFromContext(this);
        Intent activity = new Intent(this,MainPosizione.class);
        activity.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.putExtra("shouldAskLocation", true);
        startActivity(activity);
    }
    public void loginButtonTapped(View view) {
        Intent activity = new Intent(this,RegistrazioneAccedi.class);
        startActivity(activity);
    }
    @Override
    public void onResponseReceived(JSONObject obj) throws JSONException {
        String value = obj.getString("failed");
        if (value.equals("0")) {
            CVUser.isLogged = true;
            Intent activity = new Intent(this,MainPosizione.class);
            activity.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
            startActivity(activity);
        }
        else {
            CVUser.configureGuestFromContext(this);
            setContentView(R.layout.activity_main);
            CVUtility.showAlertWithTitleAndMessageFromContext(value, "Questo si verifica quando le credenziali vengono modificate da un altro dispositivo.", this);
        }
    }
}
