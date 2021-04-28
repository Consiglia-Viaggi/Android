package com.ingsw.consigliaviaggi;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ToolbarActivity extends CVCompactActivity {
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(ToolbarActivity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
            CVUtility.statusBarHeight = result;
        }
        return result;
    }
    public int getActionBarHeight() {
        int actionBarHeight = 0;
        final TypedArray styledAttributes = getApplicationContext().getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize }
        );
        actionBarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        CVUtility.actionBarHeight = actionBarHeight;
        return actionBarHeight;
    }
    public void installBackButtonIfNeeded() throws NullPointerException {
        installBackButton();
        /*
        if (CVUtility.shouldAskToAddBack()) {
            boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
            if (hasBackKey && hasHomeKey) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    CVUtility.setShouldAddBack(true);
                }
            }
            else
                CVUtility.setShouldAddBack(false);
        }
        else
            if (CVUtility.getShouldAddBack() && getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

         */
    }
    public void installBackButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar);
        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.bringToFront();
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_home:
                            apriHome(item);
                            break;
                        case R.id.action_profilo:
                            apriProfilo(item);
                    }
                    return true;
                }
            });
        }
    }
    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        View view = findViewById(getResources().getIdentifier("scrollView", "id", getPackageName()));
        if (view != null) {
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
            int[] location = new int[2];
            bottomNavigationView.getLocationOnScreen(location);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = location[1] - getStatusBarHeight() - getActionBarHeight();
            view.setLayoutParams(layoutParams);
        }
        View mapView = findViewById(getResources().getIdentifier("mapView", "id", getPackageName()));
        if (mapView != null) {
            View labelView = findViewById(getResources().getIdentifier("textView26", "id", getPackageName()));
            if (labelView != null) {
                int[] locationLabelView = new int[2];
                labelView.getLocationOnScreen(locationLabelView);
                BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
                int[] location = new int[2];
                bottomNavigationView.getLocationOnScreen(location);
                ViewGroup.LayoutParams layoutParams = mapView.getLayoutParams();
                layoutParams.height = location[1] - locationLabelView[1] - getActionBarHeight() - getStatusBarHeight();
                mapView.setLayoutParams(layoutParams);
            }
        }
        ListView listView = findViewById(getResources().getIdentifier("listview", "id", getPackageName()));
        if (listView == null)
            listView = findViewById(getResources().getIdentifier("listReviewsView", "id", getPackageName()));
        if (listView != null) {
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
            int[] location = new int[2];
            bottomNavigationView.getLocationOnScreen(location);
            ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
            layoutParams.height = listView.getHeight() - bottomNavigationView.getHeight();
            listView.setLayoutParams(layoutParams);
        }
    }
    public void apriHome(MenuItem item) {
        Intent activity = new Intent(this,MainPosizione.class);
        activity.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity(activity);
        //finish();
    }
    public void apriProfilo(MenuItem item) { // MenuItem item
        Intent activity = new Intent(this,ProfiloUtenteRegistrato.class);
        startActivity(activity);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
