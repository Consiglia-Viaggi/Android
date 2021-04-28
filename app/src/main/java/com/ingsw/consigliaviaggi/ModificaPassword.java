package com.ingsw.consigliaviaggi;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModificaPassword extends ToolbarActivity {
    public static boolean isValidPassword(String password) {
        /*
             ^                 # start-of-string
            (?=.*[0-9])       # a digit must occur at least once
            (?=.*[a-z])       # a lower case letter must occur at least once
            (?=.*[A-Z])       # an upper case letter must occur at least once
            (?=.*[@#$%^&+=])  # a special character must occur at least once you can replace with your special characters
            (?=\\S+$)          # no whitespace allowed in the entire string
            .{4,}             # anything, at least six places though
            $                 # end-of-string
         */
        if ((password.length() >= 8) && (password.length() <=15)) {
            //String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[@_.]).*$";
            String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$!£&/()%^&+=])(?=\\S+$).{4,}$";
            Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
            Matcher matcher = pattern.matcher(password);
            return matcher.matches();
        }
        return false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_password);
        super.installBackButton();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Modifica password");
    }
    @Override
    public void apriProfilo(MenuItem item) {
        super.onBackPressed();
    }
    public void updateButtonTapped(MenuItem item) {
        String message = null;
        EditText editTextNew = findViewById(R.id.editTextModificaAccountPasswordAttuale2);
        EditText editTextNuovaPassword = findViewById(R.id.editTextNuovaPassword);
        EditText editTextConfirmation = findViewById(R.id.editTextConfermaNuovaPassword);
        if (editTextNew != null && editTextConfirmation != null && editTextNuovaPassword != null) {
            String passwordAttuale = editTextNew.getText().toString();
            String nuovaPassword = editTextNuovaPassword.getText().toString();
            String nuovaPasswordConfermata = editTextConfirmation.getText().toString();
            if (passwordAttuale.equals(CVUser.password)) {
                if (nuovaPasswordConfermata.equals(nuovaPassword)) {
                    if (isValidPassword(nuovaPassword)) {
                        CVDownloader downloader = new CVDownloader(this, "https://consigliaviaggi.altervista.org/wp-content/scripts/edit_password.php", this);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("mail", CVUser.mail);
                        map.put("oldPassword", CVUser.md5Password);
                        map.put("newPassword", CVUser.md5(nuovaPassword));
                        downloader.injectMap(map);
                        downloader.execute();
                    }
                    else
                        message = "La password inserita non è una password abbastanza sicura. Una password sicura deve contenere almeno un carattere maiuscolo, uno minuscolo, un numero ed un simbolo.\nInoltre alcuni simboli, come le parentesi quadre, graffe, virgolette ed apici, non sono supportati.";
                }
                else
                    message = "Le password non corrispondono. Riprova.";
            }
            else
                message = "La vecchia password non è corretta. Riprova.";
        }
        else
            message = "Assicurati di aver compilato tutti i campi richiesti.";
        if (message != null)
            CVUtility.showAlertWithTitleAndMessageFromContext("Si è verificato un errore.", message, this);
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
            EditText text = findViewById(R.id.editTextNuovaPassword);
            CVUser.password = text.getText().toString();
            CVUser.md5Password = CVUser.md5(CVUser.password);
            AlertDialog.Builder builder = new AlertDialog.Builder(this); // Costruisci una nuova finestra di dialogo.
            builder.setTitle("Operazione completata con successo!"); // Imposta il titolo.
            final ModificaPassword context = this;
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
