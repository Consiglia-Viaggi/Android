package com.ingsw.consigliaviaggi;

public class CVReview implements java.io.Serializable, Cloneable {
    public int id_review;
    public int id_user;
    public String username;
    public int rating;
    public String date;
    public String title;
    public String description;
    public int isApproved = 0;
    public boolean isMyReview = false;
    CVReview(int _id_review, int _id_user, String _username, int _rating, String _date, String _title, String _description) {
        this.id_review = _id_review;
        this.id_user = _id_user;
        this.username = _username;
        this.rating = _rating;
        this.date = _date;
        this.title = _title;
        this.description = _description;
        if (CVUser.isLogged)
            this.isMyReview = _id_user == CVUser.id;
    }
    static public boolean isValidRating(int rating , int numberOfReviews) {
        if (numberOfReviews == 0) {
            if (rating == 0)
                return true; // E' valido perché non essendoci recensioni su cui basarsi, il rating della struttura è 0 (incompleto).
            return false; // Non posso avere un rating minore di 0, e non posso avere un rating maggiore di 0 se non ci sono recensioni su cui basarsi.
        }
        if (numberOfReviews < 0)
            return false; // non posso avere un numero negativo di reviews.
        // qui entro se numberOfReviews > 0.
        if (rating <= 0)
            return false; // non posso avere un numero negativo oppure 0 di rating.
        if (rating > 5)
            return false; // il massimo numero di rating supportato è 5.
        return true; // il rating è giusto.
    }
}
