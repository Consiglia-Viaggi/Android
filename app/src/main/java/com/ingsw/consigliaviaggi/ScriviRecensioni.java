package com.ingsw.consigliaviaggi;

import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

public class ScriviRecensioni extends ToolbarActivity {
    Toast activityToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrivi_recensioni);
        super.installBackButtonIfNeeded();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Recensione");
        RatingBar ratingBar = findViewById(R.id.rating);
        ratingBar.setRating(1);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingBar.setRating((int) rating);
            }
        });
    }
    public void sendButtonTapped(MenuItem item) {
        EditText titleView = findViewById(R.id.editText8);
        if (titleView == null) {
            return;
        }
        EditText descriptionView = findViewById(R.id.editText9);
        String title = titleView.getText().toString();
        String description = descriptionView.getText().toString();
        if (title.length() < 1 || description.length() < 1) {
            CVUtility.showAlertWithTitleAndMessageFromContext("Per favore inserisci una recensione valida.", null, this);
            return;
        }
        RatingBar ratingBar = (RatingBar) findViewById(R.id.rating);
        int rating = (int)ratingBar.getRating();
        final CVStructure structure = (CVStructure) getIntent().getSerializableExtra("structure");
        String id_structure = String.valueOf(structure.id);
        HashMap<String, String> map = new HashMap<String, String>();
        CVDownloader downloader = new CVDownloader(this,"https://consigliaviaggi.altervista.org/wp-content/scripts/send_review.php", this);
        map.put("action", "1");
        map.put("id_structure", id_structure);
        map.put("mail", CVUser.mail);
        map.put("password", CVUser.md5Password);
        map.put("title", title);
        map.put("rating", String.valueOf(rating));
        map.put("description", description);
        downloader.injectMap(map);
        activityToast = Toast.makeText(this, "Invio in corso...", Toast.LENGTH_LONG);
        activityToast.show();
        downloader.execute();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater(); // ottieni la lista dei menu
        inflater.inflate(R.menu.menu_scrivi_recensioni, menu); // aggiungi il menu.
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
        if (paramMenuItem.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        sendButtonTapped(paramMenuItem);
        return true;
    }
    @Override
    public void onResponseReceived(JSONObject obj) throws JSONException {
        activityToast.cancel();
        String value = obj.getString("failed");
        if (value.equals("-1")) {
            final ScriviRecensioni context = this;
            AlertDialog.Builder builder = new AlertDialog.Builder(this); // Costruisci una nuova finestra di dialogo.
            builder.setTitle("Ooops! Non sei più loggato.\""); // Imposta il titolo.
            builder.setMessage("Questo succede quando cambi credenziali da un altro dispositivo.\nSe vuoi effettuare nuovamente l'accesso, clicca su \"Accedi\" nella barra sottostante."); // Imposta il messaggio.
            builder.setCancelable(false).setNegativeButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            CVUser.configureGuestFromContext(context);
                            dialog.cancel();
                            context.goBack();
                        }
                    });
            builder.show(); // Mostra la finestra di dialogo
        }
        else
        if (value.equals("0")) {
            final ScriviRecensioni context = this;
            AlertDialog.Builder builder = new AlertDialog.Builder(this); // Costruisci una nuova finestra di dialogo.
            builder.setTitle("La recensione è stata inviata in moderazione."); // Imposta il titolo.
            builder.setMessage("Quando la recensione sarà approvata, apparirà immediatamente nella lista delle recensioni di questa struttura."); // Imposta il messaggio.
            builder.setCancelable(false).setNegativeButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            context.goBack();
                }
            });
            builder.show(); // Mostra la finestra di dialogo
        }
        else
            CVUtility.showAlertWithTitleAndMessageFromContext("Si è verificato un errore", value, this);
    }
    public void goBack() {
        super.onBackPressed();
    }
}
