package com.ingsw.consigliaviaggi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ViewFlipper;
import java.util.ArrayList;

public class FotoActivity extends BaseActivity {

    private ViewFlipper viewFlipper;
    private float initialXPoint;
    ArrayList<Bitmap> images = new ArrayList<Bitmap>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto);
        super.installBackButtonIfNeeded();
        Bundle bundle = getIntent().getExtras();
        if ((bundle!= null) && (bundle.containsKey("structure"))) {
            CVStructure structure = (CVStructure) getIntent().getSerializableExtra("structure");
            for (int i = 0; i < CVStructure.static_imageRepresentations.size(); i++) {
                String representation = CVStructure.static_imageRepresentations.get(i);
                byte[] imageAsBytes = Base64.decode(representation.getBytes(), Base64.DEFAULT);
                if (imageAsBytes != null) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                    if (bmp != null)
                        images.add(bmp);
                }
            }
        }
        viewFlipper = (ViewFlipper) findViewById(R.id.flipper);
        for (int i = 0; i < images.size(); i++) {
            ImageView imageView = new ImageView(FotoActivity.this);
            imageView.setImageBitmap(images.get(i));
            viewFlipper.addView(imageView);
        }
        int page = getIntent().getIntExtra("page", 0);
        viewFlipper.setDisplayedChild(page);
        setTitle(page);

    }
    public void setTitle(int current) {
        int page = current + 1;
        getSupportActionBar().setTitle("Foto " + page + " di " + images.size());
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialXPoint = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                float finalx = event.getX();
                int current = viewFlipper.getDisplayedChild();
                if (initialXPoint > finalx) {
                    if (current == images.size()-1)
                        break;
                    viewFlipper.showNext();
                    setTitle(current+1);
                } else {
                    if (current == 0)
                        break;
                    viewFlipper.showPrevious();
                    setTitle(current-1);
                }
                break;
        }
        return false;
    }
}

