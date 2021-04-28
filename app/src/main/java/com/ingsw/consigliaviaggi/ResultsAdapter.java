package com.ingsw.consigliaviaggi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ResultsAdapter extends BaseAdapter {
    private Context mContext;
    public ArrayList<CVStructure> structures;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
        //if (convertView == null) {
            view = inflater.inflate(R.layout.row_result, null);
            CVStructure structure = structures.get(position);
            TextView tableRowTitle = view.findViewById(R.id.tableRowTitle);
            tableRowTitle.setText(structure.name);
            TextView tableRowDescription = view.findViewById(R.id.tableRowDescription);
            String description = structure.description.length() > 40 ? structure.description.substring(0, 39) + "..." : structure.description;
            tableRowDescription.setText(description);
            ImageView imageView = (ImageView)view.findViewById(R.id.imageRow);
            if (structure.image != null)
                imageView.setImageBitmap(structure.image);
            else {
                if (structure.imageRepresentation != null) {
                    byte[] imageAsBytes = Base64.decode(structure.imageRepresentation.getBytes(), Base64.DEFAULT);
                    Bitmap bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                    imageView.setImageBitmap(bmp);
                }
            }
        //}
        //else
        //    view = convertView;
        return view;
    }
    public ResultsAdapter(Context c, ArrayList<CVStructure> _structures) {
        mContext = c;
        this.structures = _structures;
    }

    @Override
    public int getCount() {
        return structures.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
