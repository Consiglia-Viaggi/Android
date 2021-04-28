package com.ingsw.consigliaviaggi;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class TabGalleria extends Fragment {
    @SuppressLint("WrongThread")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_galleria, container, false);
        Bundle bundle = getActivity().getIntent().getExtras();
        if ((bundle!= null) && (bundle.containsKey("structure"))) {
            CVStructure structure = (CVStructure) getActivity().getIntent().getSerializableExtra("structure");
            if (CVStructure.static_imageRepresentations.size() == 0)
                return view;
            ArrayList<Bitmap> images = new ArrayList<Bitmap>();

            for (int i = 0; i < CVStructure.static_imageRepresentations.size(); i++) {
                String representation = CVStructure.static_imageRepresentations.get(i);
                byte[] imageAsBytes = Base64.decode(representation.getBytes(), Base64.DEFAULT);
                if (imageAsBytes != null) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                    if (bmp != null) {
                        images.add(bmp);
                    }
                }
            }
            if (images.size() == 0)
                return view;
            ImageAdapter adapter = new ImageAdapter(getContext(), images);
            GridView grid = (GridView) view.findViewById(R.id.grid_view);
            grid.setAdapter(adapter);
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent activity = new Intent(getContext(), FotoActivity.class);
                    CVStructure structure = (CVStructure) getActivity().getIntent().getSerializableExtra("structure");
                    activity.putExtra("structure", structure);
                    activity.putExtra("page", position);
                    startActivity(activity);
                }
            });
            BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);
            int[] location = new int[2];
            bottomNavigationView.getLocationOnScreen(location);
            ViewGroup.LayoutParams layoutParams = grid.getLayoutParams();
            layoutParams.height = grid.getHeight() - bottomNavigationView.getHeight();
            grid.setLayoutParams(layoutParams);
        }
        return view;
    }
}
