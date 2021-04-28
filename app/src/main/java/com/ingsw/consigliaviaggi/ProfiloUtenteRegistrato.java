package com.ingsw.consigliaviaggi;

import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ProfiloUtenteRegistrato extends ToolbarActivity {
    Toast activityToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_utente_registrato);
        super.installBackButton();
        if (CVUser.isLogged)
            Objects.requireNonNull(getSupportActionBar()).setTitle("Profilo di " + CVUser.username);
        else {
            Intent intent = new Intent(ProfiloUtenteRegistrato.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
    @Override
    public void apriProfilo(MenuItem item) { // MenuItem item

    }
    public void deleteAccountButtonTapped(View view) {
        if (CVUser.isLogged) {
            Intent intent = new Intent(this, EliminaAccount.class);
            startActivity(intent);
        }
        else
            CVUtility.showAlertWithTitleAndMessageFromContext("Ooops! Non sei più loggato.", "Questo succede quando cambi credenziali da un altro dispositivo.\nSe vuoi effettuare nuovamente l'accesso, clicca su \"Accedi\" nella barra sottostante.", this);
    }
    public void editAccountButtonTapped(View view) {
        if (CVUser.isLogged) {
            Intent intent = new Intent(this,ModificaAccount.class);
            startActivity(intent);
        }
        else
            CVUtility.showAlertWithTitleAndMessageFromContext("Ooops! Non sei più loggato.", "Questo succede quando cambi credenziali da un altro dispositivo.\nSe vuoi effettuare nuovamente l'accesso, clicca su \"Accedi\" nella barra sottostante.", this);
    }
    public void editPasswordButtonTapped(View view) {
        if (CVUser.isLogged) {
            Intent intent = new Intent(this,ModificaPassword.class);
            startActivity(intent);
        }
        else
            CVUtility.showAlertWithTitleAndMessageFromContext("Ooops! Non sei più loggato.", "Questo succede quando cambi credenziali da un altro dispositivo.\nSe vuoi effettuare nuovamente l'accesso, clicca su \"Accedi\" nella barra sottostante.", this);
    }
    public void myReviewsButtonTapped(View view) {
        HashMap<String, String> map = new HashMap<String, String>();
        CVDownloader downloader = new CVDownloader(this,"https://consigliaviaggi.altervista.org/wp-content/scripts/send_review.php", this);
        map.put("action", "2");
        map.put("mail", CVUser.mail);
        map.put("password", CVUser.md5Password);
        map.put("includeNotApproved", "1");
        downloader.injectMap(map);
        activityToast = Toast.makeText(this, "Caricamento in corso...", Toast.LENGTH_LONG);
        activityToast.show();
        downloader.execute();
    }
    public void termsButtonTapped(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://consigliaviaggi.altervista.org/tos/"));
        startActivity(browserIntent);
    }
    public void logoutButtonTapped(MenuItem item) {
        // esci...
        final ProfiloUtenteRegistrato context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this); // Costruisci una nuova finestra di dialogo.
        builder.setTitle("Vuoi davvero uscire dal tuo account?"); // Imposta il titolo.
        builder.setMessage("Potrai riaccedere successivamente utilizzando le tue credenziali."); // Imposta il messaggio.
        builder.setCancelable(false).setNegativeButton("Annulla",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).setPositiveButton("Esci", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                    CVUser.configureGuestFromContext(context);
                    Intent intent = new Intent(ProfiloUtenteRegistrato.this, MainActivity.class);
                    intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                    startActivity(intent);
            }
        });
        builder.show(); // Mostra la finestra di dialogo
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater(); // ottieni la lista dei menu
        inflater.inflate(R.menu.menu_profilo_utente_registrato, menu); // aggiungi il menu.
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
        if (paramMenuItem.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        logoutButtonTapped(paramMenuItem);
        return true;
    }
    @Override
    public void onResponseReceived(JSONObject obj) throws JSONException {
        if (activityToast != null)
            activityToast.cancel();
        String value = obj.getString("failed");
        if (value.equals("-1")) {
            final ProfiloUtenteRegistrato context = this;
            AlertDialog.Builder builder = new AlertDialog.Builder(this); // Costruisci una nuova finestra di dialogo.
            builder.setTitle("Oops! Non sei più loggato."); // Imposta il titolo.
            builder.setMessage("Questo succede quando modifichi le credenziali da un altro dispositivo.\nSe vuoi effettuare nuovamente l'accesso, clicca su \"Accedi\" nella barra sottostante."); // Imposta il messaggio.
            builder.setCancelable(false).setNegativeButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            CVUser.configureGuestFromContext(context);
                            dialog.cancel();
                            context.onBackPressed();
                        }
                    });
            builder.show(); // Mostra la finestra di dialogo
        }
        else
        if (value.equals("0")) {
            JSONArray arr = obj.getJSONArray("reviews");
            ArrayList<CVReview> reviews = new ArrayList<CVReview>();
            for (int j = 0; j < arr.length(); j++) {
                JSONArray subReviewsArray = arr.getJSONArray(j);
                CVReview review = new CVReview(subReviewsArray.getInt(0), subReviewsArray.getInt(1), subReviewsArray.getString(2), subReviewsArray.getInt(3), subReviewsArray.getString(4), subReviewsArray.getString(5), subReviewsArray.getString(6));
                review.isApproved = subReviewsArray.getInt(7);
                reviews.add(review);
            }
            Intent intent = new Intent(this,LeMieRecensioni.class);
            intent.putExtra("reviews", reviews);
            startActivity(intent);
        }
        else
            CVUtility.showAlertWithTitleAndMessageFromContext("Si è verificato un errore", value, this);
    }
}
