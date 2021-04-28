package com.ingsw.consigliaviaggi;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;


public class Servizi extends BaseActivity {
    protected void configureSwitch(int i) {
        String viewName = "switch" + String.valueOf(i);
        Switch switchView = findViewById(getResources().getIdentifier(viewName, "id", getPackageName()));
        if (switchView == null)
            return;
        final int j = i;
        //if (CVUtility.getHasConfiguredSwitches())
            switchView.setChecked(CVUtility.switchValueAtIndex(i));
        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CVUtility.setBoolAtIndex(isChecked, j);
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servizi);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        for (int i = 0; i < 8; i++)
            configureSwitch(i);
        CVUtility.setHasConfiguredSwitches(true);
    }
}
