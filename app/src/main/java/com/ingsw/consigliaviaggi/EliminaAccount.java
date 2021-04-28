package com.ingsw.consigliaviaggi;

import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Objects;

public class EliminaAccount extends ToolbarActivity {
    HashMap<String, String> map = new HashMap<String, String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elimina_account);
        super.installBackButtonIfNeeded();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Elimina account");
    }
    @Override
    public void apriHome(MenuItem item) {

    }
    public void setValueForKey(String value, String key) {
        map.put(key, value);
    }
    @Override
    public void apriProfilo(MenuItem item) { // MenuItem item
        super.onBackPressed();
    }
    public void confirmButtonTapped(MenuItem item) {
        int alertType = 1;
        EditText editText = findViewById(R.id.editText11);
        if (editText != null) {
            String string = editText.getText().toString();
            if (string != null) {
                if (string.length() > 0) {
                    CVDownloader downloader = new CVDownloader(this,"https://consigliaviaggi.altervista.org/wp-content/scripts/delete_account.php", this);
                    setValueForKey(string, "mail");
                    setValueForKey(CVUser.md5(string), "password");
                    downloader.injectMap(map);
                    downloader.execute();
                    alertType = 0;
                }
            }
        }
        if (alertType > 0)
            CVUtility.showAlertWithTitleAndMessageFromContext("La password non è corretta.", "Inserisci la password in un formato corretto.", this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater(); // ottieni la lista dei menu
        inflater.inflate(R.menu.menu_elimina_account, menu); // aggiungi il menu.
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
        if (paramMenuItem.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        confirmButtonTapped(paramMenuItem);
        return true;
    }
    @Override
    public void onResponseReceived(JSONObject obj) throws JSONException {
        String value = obj.getString("failed");
        if (value.equals("0")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this); // Costruisci una nuova finestra di dialogo.
            builder.setTitle("L'account è stato eliminato con successo."); // Imposta il titolo.
            final EliminaAccount context = this;
            builder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
            builder.show(); // Mostra la finestra di dialogo
        }
        else
            CVUtility.showAlertWithTitleAndMessageFromContext("Si è verificato un errore.", value, this);
    }
}
