package com.ingsw.consigliaviaggi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registrazione extends ScrollViewActivity {
    HashMap<String, String> map = new HashMap<String, String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrazione);
        super.installBackButton();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Registrati");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); // Sposta la view in alto ogni volta che si passa ad un field successivo.
    }
    public void termsButtonTapped(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://consigliaviaggi.altervista.org/tos/"));
        startActivity(browserIntent);
    }
    public void setValueForKey(String value, String key){
        map.put(key, value);
    }
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
    public boolean isAlpha(String name) {
        return name.matches("[a-zA-Z]+");
    }
    public void createAccountButtonTapped(View view) {
        String message = null;
        EditText editTextEmail = findViewById(R.id.editText4);
        EditText editTextPassword = findViewById(R.id.editText5);
        EditText editTextRepeatPassword = findViewById(R.id.editText6);
        EditText editTextName = findViewById(R.id.editText);
        EditText editTextSurname = findViewById(R.id.editText2);
        EditText editTextNickname = findViewById(R.id.editText3);
        if (editTextEmail != null && editTextPassword != null && editTextRepeatPassword != null && editTextName != null && editTextSurname!= null && editTextNickname != null) {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            String repeatPassword = editTextRepeatPassword.getText().toString();
            String name = editTextName.getText().toString();
            String surname = editTextSurname.getText().toString();
            String nickname = editTextNickname.getText().toString();
            if (email.length() > 5) {
                if (password.length() >= 8 && repeatPassword.length() >= 8 && password.length() < 16 && repeatPassword.length() < 16) {
                    if (name.length() >= 3 && surname.length() >= 3 && isAlpha(name) && isAlpha(surname)) {
                        if (nickname.length() >= 3 && !nickname.contains(" ")) {
                            if (email.contains("@") && email.contains(".") && !email.contains(" ")) {
                                if (isValidPassword(password)) {
                                    if (password.equals(repeatPassword)) {
                                        CVDownloader downloader = new CVDownloader(this, "https://consigliaviaggi.altervista.org/wp-content/scripts/registration.php", this);
                                        setValueForKey(email, "mail");
                                        setValueForKey(password, "password");
                                        setValueForKey(repeatPassword, "repeatPassword");
                                        setValueForKey(name, "name");
                                        setValueForKey(surname, "surname");
                                        setValueForKey(nickname, "username");
                                        downloader.injectMap(map);
                                        downloader.execute();
                                    }
                                    else
                                        message = "Le due password non corrispondono.";
                                }
                                else
                                    message = "La password inserita non è una password abbastanza sicura. Una password sicura deve contenere almeno un carattere maiuscolo, uno minuscolo, un numero ed un simbolo.\nInoltre alcuni simboli, come le parentesi quadre, graffe, virgolette ed apici, non sono supportati.";
                            }
                            else
                                message = "L'indirizzo mail inserito non è nel formato corretto.";
                        }
                        else
                            message = "Il nickname scelto non è nel formato corretto. Deve essere almeno di 3 caratteri e non può contenere spazi.";
                    }
                    else
                        message = "Il nome o il cognome non sono nel formato corretti. Devono essere almeno di 3 caratteri";
                }
                else
                    message = "La password è troppo corta o troppo lunga. Scegline una compresa tra 8 e 15 caratteri (estremi compresi).";
            }
            else
                message = "L'indirizzo mail inserito è troppo corto per poter essere un indirizzo mail valido.";
        }
        if (message != null) {
            String title = "I dati inseriti non sono corretti.";
            CVUtility.showAlertWithTitleAndMessageFromContext(title, message, this);
        }
    }
    @Override
    public void onResponseReceived(JSONObject obj) throws JSONException {
        String value = obj.getString("failed");
        if (value.equals("0")) {
            String id = String.valueOf(obj.getInt("id"));
            setValueForKey(id, "id");
            CVUser.configureLoginFromContext(map, this);
            Intent activity = new Intent(this,MainPosizione.class);
            activity.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
            startActivity(activity);
        }
        else
            CVUtility.showAlertWithTitleAndMessageFromContext("Si è verificato un errore.", value, this);
    }
}
