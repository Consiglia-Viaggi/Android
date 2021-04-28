package com.ingsw.consigliaviaggi;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class CVStructure implements java.io.Serializable, Cloneable {
    static public ArrayList<String> static_imageRepresentations = new ArrayList<String>();
    static public ArrayList<String> static_imageResultsRepresentations = new ArrayList<String>();
    public String id;
    public String name;
    public Double latitude;
    public Double longitude;
    public String description;
    public String phoneNumber;
    public transient Bitmap image;
    public String imageRepresentation;
    public int price_level;
    public double reviews_average;
    public ArrayList<String> imageRepresentations = new ArrayList<String>();
    public ArrayList<CVReview> reviews = new ArrayList<CVReview>();
    static public void initStaticResultsToCapacity(int capacity) {
        static_imageResultsRepresentations = new ArrayList<String>(capacity);
        for (int i = 0; i < capacity; i++) {
            static_imageResultsRepresentations.add("");
        }
    }
    CVStructure(String _id, String _name, String _description, String _phoneNumber, Double _latitude, Double _longitude) {
        this.id = _id;
        this.name = _name;
        this.latitude = _latitude;
        this.description = _description;
        this.phoneNumber = _phoneNumber;
        this.longitude = _longitude;
    }
    public void setImage(Bitmap _image) {
        image = _image;
    }
    public void setImageRepresentation(String str) {
        imageRepresentation = str;
    }
    public void setImageRepresentationInArray(String str) {
            imageRepresentations.add(str);
    }
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
