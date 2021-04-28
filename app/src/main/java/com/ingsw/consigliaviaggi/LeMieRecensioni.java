package com.ingsw.consigliaviaggi;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class LeMieRecensioni extends ToolbarActivity {
    public ArrayList<CVReview> reviews;
    public ListView listView;
    public ReviewsAdapter reviewsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_le_mie_recensioni);
        super.installBackButtonIfNeeded();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Le mie recensioni");
        reviews = (ArrayList<CVReview>)getIntent().getSerializableExtra("reviews");
        reviewsAdapter = new ReviewsAdapter(this, reviews, true);
        listView = (ListView) findViewById(R.id.listReviewsView);
        listView.setAdapter(reviewsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CVReview review = reviews.get(position);
                final int index = position;
                final int id_review = review.id_review;
                final int id_user = review.id_user;
                if (id_user == CVUser.id) {
                    final Context context = view.getContext();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context); // Costruisci una nuova finestra di dialogo.
                    builder.setTitle(review.title); // Imposta il titolo.
                    builder.setMessage("Questa recensione è stata pubblicata da te. Vuoi eliminarla?"); // Imposta il messaggio.
                    builder.setCancelable(false).setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }).setPositiveButton("Sì",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("id_review", String.valueOf(id_review));
                            map.put("action", "3");
                            map.put("mail", CVUser.mail);
                            map.put("password", CVUser.md5Password);
                            map.put("pos", String.valueOf(index));
                            CVDownloader downloader = new CVDownloader(context,"https://consigliaviaggi.altervista.org/wp-content/scripts/send_review.php", (CVCompactActivity) context);
                            downloader.injectMap(map);
                            downloader.execute();
                            dialog.cancel();
                        }
                    });
                    builder.show(); // Mostra la finestra di dialogo
                }
            }

        });
    }
    @Override
    public void apriProfilo(MenuItem item) {
        super.onBackPressed();
    }
    @Override
    public void onResponseReceived(JSONObject obj) throws JSONException {
        String value = obj.getString("failed");
        if (value.equals("0")) {
            int index = obj.getInt("pos");
            reviews.remove(index);
            reviewsAdapter.updateAdapter(reviews);
            listView.invalidate();
            reviewsAdapter = new ReviewsAdapter(this, reviews, true);
            listView.setAdapter(reviewsAdapter);
            CVUtility.showAlertWithTitleAndMessageFromContext("La recensione è stata eliminata con successo.", null, this);
        }
        else
            CVUtility.showAlertWithTitleAndMessageFromContext("Si è verificato un errore.", value, this);
    }
}
