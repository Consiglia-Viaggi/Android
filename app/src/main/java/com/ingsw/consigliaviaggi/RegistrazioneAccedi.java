package com.ingsw.consigliaviaggi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;


public class RegistrazioneAccedi extends BaseActivity {
    HashMap<String, String> map = new HashMap<String, String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrazione_accedi);
        super.installBackButton();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Accedi");
    }
    public void passwordForgottenButtonTapped(View view) {
        EditText editTextEmail = findViewById(R.id.editTextEmail);
        String mail = editTextEmail.getText().toString();
        if (mail.length() > 0) {
            CVDownloader downloader = new CVDownloader(this, "https://consigliaviaggi.altervista.org/wp-content/website/send_temp_password.php", this);
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("mail", mail);
            map.put("app", "1");
            downloader.injectMap(map);
            downloader.execute();
            Toast toast = Toast.makeText(getApplicationContext(), "Se l'indirizzo inserito è valido ed è associato ad un account, una password temporanea sarà inviata all'indirizzo email specificato in fase di registrazione.\nRicordati di controllare anche la posta indesiderata.", Toast.LENGTH_LONG);
            toast.show();
        }
        else {
            Toast toast = Toast.makeText(getApplicationContext(), "Per favore, inserisci un indirizzo mail nel formato corretto.", Toast.LENGTH_LONG);
            toast.show();
        }
    }
    public void setValueForKey(String value, String key) {
        map.put(key, value);
    }
    public void logInButtonTapped(View view){
        int alertType = 1;
        EditText editTextEmail = findViewById(R.id.editTextEmail);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        if (editTextEmail != null && editTextPassword != null) {
            String stringNew = editTextEmail.getText().toString();
            String stringConfirmation = editTextPassword.getText().toString();
            if (stringNew != null && stringConfirmation != null) {
                if (stringNew.length() > 0 && stringConfirmation.length() > 0 && (stringNew.contains("@")) && (stringNew.contains(".")) && (!stringNew.contains(" "))) {
                    // collegamento server con risposta...
                    // se tutto è andato bene...
                    CVDownloader downloader = new CVDownloader(this,"https://consigliaviaggi.altervista.org/wp-content/scripts/check_login.php", this);
                    setValueForKey(stringNew, "mail");
                    setValueForKey(stringConfirmation, "password");
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
                case 1:
                    message = "Assicurati di aver inserito le tue credenziali e riprova.";
                    break;
                case 2:
                    message = "Assicurati di aver inserito i dati nel formato corretto.";
                    break;
                default:
                    message = "Assicurati di aver inserito i dati correttamente e riprova.";
            }
            CVUtility.showAlertWithTitleAndMessageFromContext(title, message, this);
        }
    }
    public void registerAccountButtonTapped(View view) {
        Intent activity = new Intent(this,Registrazione.class);
        startActivity(activity);
    }
    @Override
    public void onResponseReceived(JSONObject obj) throws JSONException {
        if (obj.has("temp_password")) {
            String temp_password_procedure_string = obj.getString("temp_password");
            if (temp_password_procedure_string.equals("0"))
                return;
        }
        String value = obj.getString("failed");
        if (value.equals("0")) {
            setValueForKey(obj.getString("name"), "name");
            setValueForKey(obj.getString("surname"), "surname");
            setValueForKey(obj.getString("username"), "username");
            setValueForKey(String.valueOf(obj.getInt("id")), "id");
            CVUser.configureLoginFromContext(map, this);
            Intent activity = new Intent(this,MainPosizione.class);
            activity.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
            startActivity(activity);
        }
        else
            CVUtility.showAlertWithTitleAndMessageFromContext("Si è verificato un errore", value, this);
    }
}
