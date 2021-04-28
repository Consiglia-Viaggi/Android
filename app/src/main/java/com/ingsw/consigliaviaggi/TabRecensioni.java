package com.ingsw.consigliaviaggi;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import android.widget.Spinner;
import android.widget.Toast;


import androidx.annotation.Nullable;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class TabRecensioni extends Fragment {
    ArrayList<CVReview>reviews = new ArrayList<CVReview>();
    ListView listView;
    static int check = 0;
    ReviewsAdapter reviewsAdapter;
    public void populateArrayListWithArrayList(ArrayList<CVReview> arrayList) {
        reviews.clear();
        for (int i = 0; i < arrayList.size(); i++)
            reviews.add(arrayList.get(i));
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_recensioni, container, false);
        Bundle bundle = getActivity().getIntent().getExtras();
        if ((bundle!= null) && (bundle.containsKey("structure"))) {
            CVStructure structure = (CVStructure) getActivity().getIntent().getSerializableExtra("structure");
            populateArrayListWithArrayList(structure.reviews);
            reviewsAdapter = new ReviewsAdapter(getContext(), structure.reviews, false);
            listView = (ListView) view.findViewById(R.id.listReviewsView);
            listView.setAdapter(reviewsAdapter);
        }
        return view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //inflater.inflate(R.menu.menu_reviews, menu);
        inflater.inflate(R.menu.spinner_menu, menu);
        MenuItem item = menu.findItem(R.id.spinner);
        final Spinner spinner = (Spinner) item.getActionView();
        spinner.setPrompt("ORDINA PER");
        ArrayList<String> sequence = new ArrayList<String>();
        sequence.add("Recenti");
        sequence.add("Vecchi");
        sequence.add("Migliori");
        sequence.add("Peggiori");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, sequence);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(++check == 0 || reviews.size() == 0)
                    return;
                // TODO Auto-generated method stub
                //int currentPosition = spinner.getSelectedItemPosition();
                if (position == 3) {
                    Collections.sort(reviews, new Comparator<CVReview>() {
                        @Override
                        public int compare(CVReview o1, CVReview o2) {
                            return o1.rating - o2.rating;
                        }
                    });
                }
                else if (position == 2) {
                    Collections.sort(reviews, new Comparator<CVReview>() {
                        @Override
                        public int compare(CVReview o1, CVReview o2) {
                            return o2.rating - o1.rating;
                        }
                    });

                }
                else if (position == 1) {
                    Collections.sort(reviews, new Comparator<CVReview>() {
                        @Override
                        public int compare(CVReview o1, CVReview o2) {
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd h:m:s");
                            Date o1PublishDate = null;
                            Date o2PublishDate = null;
                            try {
                                o1PublishDate = formatter.parse(o1.date);
                                o2PublishDate = formatter.parse(o2.date);

                            } catch (ParseException pe) {
                            }
                            return o1PublishDate.compareTo(o2PublishDate);

                        }
                    });
                }
                else {
                    Collections.sort(reviews, new Comparator<CVReview>() {
                        @Override
                        public int compare(CVReview o1, CVReview o2) {
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd h:m:s");
                            Date o1PublishDate = null;
                            Date o2PublishDate = null;
                            try {
                                o1PublishDate = formatter.parse(o1.date);
                                o2PublishDate = formatter.parse(o2.date);
                            } catch (ParseException pe) {
                            }
                            return o2PublishDate.compareTo(o1PublishDate);
                        }
                    });
                }
                reviewsAdapter.updateAdapter(reviews);
                listView.invalidate();
                reviewsAdapter = new ReviewsAdapter(getContext(), reviews, false);
                listView.setAdapter(reviewsAdapter);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle menu item clicks


        return super.onOptionsItemSelected(item);
    }

}
