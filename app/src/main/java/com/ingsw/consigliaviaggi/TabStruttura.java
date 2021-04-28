package com.ingsw.consigliaviaggi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TabStruttura extends Fragment {
    static public void configureView(TextView view, String label, String character, int tot) {
        for (int i = 0; i < tot; i++)
            label = label + character;
        view.setText(label);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_struttura, container, false);
        setHasOptionsMenu(true);
        Bundle bundle = getActivity().getIntent().getExtras();
        if ((bundle!= null) && (bundle.containsKey("structure"))) {
            final CVStructure structure = (CVStructure) getActivity().getIntent().getSerializableExtra("structure");
            TextView title = view.findViewById(R.id.structureTitle);
            TextView description = view.findViewById(R.id.structureDescription);
            TextView structureDescription = view.findViewById(R.id.structureFullDescription);
            TextView callTextView = view.findViewById(R.id.callButton);
            callTextView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (structure.phoneNumber != null)
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", structure.phoneNumber, null)));
                }
            });
            TextView mapButtonView = view.findViewById(R.id.viewOnMap);
            mapButtonView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    String uri = "geo:" + structure.latitude + "," + structure.longitude;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                }
            });
            structureDescription.setText(structure.description);
            if (CVReview.isValidRating((int)structure.reviews_average, structure.reviews.size()))
                configureView(title, "Valutazione: ", "★", (int)structure.reviews_average);
            else
                title.setText("Valutazione incompleta.");
            configureView(description, "Prezzo: ", "$", structure.price_level);
            ImageView imageView = view.findViewById(R.id.structureImage);
            if (structure.image != null)
                imageView.setImageBitmap(structure.image);
            else {
                if (structure.imageRepresentation != null) {
                    byte[] imageAsBytes = Base64.decode(structure.imageRepresentation.getBytes(), Base64.DEFAULT);
                    Bitmap bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                    imageView.setImageBitmap(bmp);
                }
            }
        }
        return view;
    }
}
