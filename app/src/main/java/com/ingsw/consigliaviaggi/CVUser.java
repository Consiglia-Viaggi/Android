package com.ingsw.consigliaviaggi;

import android.content.Context;
import android.content.SharedPreferences;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class CVUser {
    static public int id;
    static public String username;
    static public String password;
    static public String md5Password;
    static public String name;
    static public String surname;
    static public String mail;
    static public boolean isLogged = false;
    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    static public void configureLoginFromContext(HashMap<String, String> map, Context context)  {
        username = map.get("username");
        password = map.get("password");
        md5Password = md5(password);
        name = map.get("name");
        surname = map.get("surname");
        mail = map.get("mail");
        String id_string_representation = map.get("id");
        id = Integer. parseInt(id_string_representation);
        isLogged = true;
        SharedPreferences.Editor editor = context.getSharedPreferences("consiglia_viaggi", 0).edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putString("md5Password", md5Password);
        editor.putString("name", name);
        editor.putString("surname", surname);
        editor.putString("mail", mail);
        editor.putInt("id", id);
        editor.apply();
    }
    static public void configureGuestFromContext(Context context) {
        isLogged = false;
        username = null;
        password = null;
        md5Password = null;
        name = null;
        surname = null;
        mail = null;
        id = 0;
        // I dati vengono salvati nella sandbox dell'app, alla quale solo l'app in questione ha accesso. Di conseguenza è sicuro salvare i dati come segue:
        SharedPreferences.Editor editor = context.getSharedPreferences("consiglia_viaggi", 0).edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putString("md5Password", md5Password);
        editor.putString("name", name);
        editor.putString("surname", surname);
        editor.putString("mail", mail);
        editor.putInt("id", id);
        editor.apply();
    }
    static public boolean initPreviousDataFromContext(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("consiglia_viaggi", 0);
        username = prefs.getString("username", null);
        if (username != null) {
            password = prefs.getString("password", null);
            md5Password = prefs.getString("md5Password", null);
            name = prefs.getString("name", null);
            surname = prefs.getString("surname", null);
            mail = prefs.getString("mail", null);
            id = prefs.getInt("id", 0);
            return true;
        }
        return false;
    }
}
