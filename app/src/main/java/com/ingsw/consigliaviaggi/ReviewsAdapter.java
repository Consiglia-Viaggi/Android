package com.ingsw.consigliaviaggi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ReviewsAdapter extends BaseAdapter {
    private Context mContext;
    public ArrayList<CVReview> reviews;
    public boolean areMyReviews = false;
    public void updateAdapter(ArrayList<CVReview> _reviews) {
        this.reviews= _reviews;
        notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
        if (convertView == null) {
            view = inflater.inflate(R.layout.row_review, null);
            CVReview review = reviews.get(position);
            TextView usernameReviewView = view.findViewById(R.id.usernameReview);
            if (areMyReviews) {
                if (review.isApproved == 1)
                    usernameReviewView.setText("Recensione approvata");
                else
                    usernameReviewView.setText("In attesa di moderazione");
            }
            else
                usernameReviewView.setText(review.username);
            TextView starReviewView = view.findViewById(R.id.starReview);
            String stars = "";
            for (int j = 0; j < review.rating; j++)
                stars = stars + "★";
            starReviewView.setText(stars);
            TextView dateReviewView = view.findViewById(R.id.dateReview);
            dateReviewView.setText(review.date);
            TextView titleReviewView = view.findViewById(R.id.titleReview);
            titleReviewView.setText(review.title);
            TextView descriptionReviewView = view.findViewById(R.id.descriptionReview);
            descriptionReviewView.setText(review.description);
        }
        else
            view = convertView;
        return view;
    }
    public ReviewsAdapter(Context c, ArrayList<CVReview> _reviews, boolean _areMyReviews) {
        mContext = c;
        this.reviews = _reviews;
        this.areMyReviews = _areMyReviews;
    }

    @Override
    public int getCount() {
        return reviews.size();
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
