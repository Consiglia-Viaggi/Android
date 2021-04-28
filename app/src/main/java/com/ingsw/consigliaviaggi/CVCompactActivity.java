package com.ingsw.consigliaviaggi;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import org.json.JSONException;
import org.json.JSONObject;


public class CVCompactActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c_v_compact);
    }
    public void onResponseReceived(JSONObject obj) throws JSONException {

    }
}
