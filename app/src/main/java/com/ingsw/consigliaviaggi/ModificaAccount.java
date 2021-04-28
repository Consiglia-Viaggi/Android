package com.ingsw.consigliaviaggi;

import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

public class ModificaAccount extends ToolbarActivity {
    HashMap<String, String> map = new HashMap<String, String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_account);
        super.installBackButton();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Modifica email");
    }
    public void setValueForKey(String value, String key) {
        map.put(key, value);
    }
    @Override
    public void apriProfilo(MenuItem item) {
        super.onBackPressed();
    }
    public void updateButtonTapped(MenuItem item) {
        int alertType = 1;
        EditText editTextNew = findViewById(R.id.editTextNuovaEmail);
        EditText editTextConfirmation = findViewById(R.id.editTextConfermaEmail);
        if (editTextNew != null && editTextConfirmation != null) {
            String stringNew = editTextNew.getText().toString();
            String stringConfirmation = editTextConfirmation.getText().toString();
            if (stringNew != null && stringConfirmation != null) {
                if (stringNew.length() > 0 && stringConfirmation.length() > 0 && (stringNew.equals(stringConfirmation))) {
                    CVDownloader downloader = new CVDownloader(this,"https://consigliaviaggi.altervista.org/wp-content/scripts/edit_mail.php", this);
                    setValueForKey(stringNew, "newMail");
                    setValueForKey(CVUser.mail, "oldMail");
                    setValueForKey(CVUser.md5Password, "password");
                    downloader.injectMap(map);
                    downloader.execute();
                    alertType = 0;
                }
                else
                    alertType = 2;
            }
        }
        if (alertType > 0) {
            String title = "I dati inseriti non sono corretti", message;
            switch (alertType) {
                case 5:
                    message = "La mail inserita è già quella associata al tuo account.";
                    break;
                case 4:
                    message = "La password non corrisponde. Riprova.";
                    break;
                case 3:
                    message = "La mail inserita è già utilizzata da un altro utente.";
                    break;
                case 2:
                    message = "Assicurati di aver confermato la stessa mail desiderata e riprova.";
                    break;
                default:
                    message = "Assicurati di aver inserito i dati correttamente e riprova.";
            }
            CVUtility.showAlertWithTitleAndMessageFromContext(title, message, this);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater(); // ottieni la lista dei menu
        inflater.inflate(R.menu.menu, menu); // aggiungi il menu.
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
        if (paramMenuItem.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        updateButtonTapped(paramMenuItem);
        return true;
    }
    @Override
    public void onResponseReceived(JSONObject obj) throws JSONException {
        String value = obj.getString("failed");
        if (value.equals("0")) {
            CVUser.mail = obj.getString("newMail");
            AlertDialog.Builder builder = new AlertDialog.Builder(this); // Costruisci una nuova finestra di dialogo.
            builder.setTitle("Operazione completata con successo!"); // Imposta il titolo.
            final ModificaAccount context = this;
            builder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    context.onBackPressed();
                }
            });
            builder.show(); // Mostra la finestra di dialogo
        }
        else
            CVUtility.showAlertWithTitleAndMessageFromContext("Si è verificato un errore.", value, this);
    }
}
