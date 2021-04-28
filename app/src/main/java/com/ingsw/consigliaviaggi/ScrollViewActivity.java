package com.ingsw.consigliaviaggi;

import android.content.res.TypedArray;
import android.util.DisplayMetrics;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class ScrollViewActivity extends CVCompactActivity {
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    public int getActionVarHeight() {
        int actionBarHeight = 0;
        final TypedArray styledAttributes = getApplicationContext().getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize }
        );
        actionBarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        return actionBarHeight;
    }
    public void installBackButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void installBackButtonIfNeeded() {
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
        if (hasBackKey && hasHomeKey) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        View view = findViewById(getResources().getIdentifier("scrollView", "id", getPackageName()));
        if (view != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = displayMetrics.heightPixels - getStatusBarHeight() - getActionVarHeight();
            view.setLayoutParams(layoutParams);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void onResponseReceived(String value) {

    }
}
