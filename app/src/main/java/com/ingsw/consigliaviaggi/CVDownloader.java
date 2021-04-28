package com.ingsw.consigliaviaggi;

import android.content.Context;
import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

public class CVDownloader extends AsyncTask<URL, Integer, Long> {
    String response = "";
    String urlString = "";
    Context myContext;
    CVCompactActivity delegate;
    HashMap<String, String> map;
    CVDownloader(Context context, String _url, CVCompactActivity _delegate) {
        this.myContext = context;
        urlString = _url;
        delegate = _delegate;
        if (map != null)
            map.clear();
    }
    public void injectMap(HashMap<String, String> _map) {
        map = _map;
    }
    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder feedback = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                feedback.append("&");

            feedback.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            feedback.append("=");
            feedback.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return feedback.toString();
    }
    public void getData() throws IOException {
        HashMap<String, String> params;

        //URL url = new URL("https://eddn.usgs.gov/cgi-bin/fieldtest.pl");
        URL url = new URL(urlString);
        HttpURLConnection client = null;
        try {
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
            client.setRequestProperty( "charset", "utf-8");
            client.setDoInput(true);
            client.setDoOutput(true);
            client.setReadTimeout(15*1000);
            client.setRequestMethod("GET");
            params = map;
            OutputStream os = client.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(params));
            writer.flush();
            writer.close();
            os.close();
            int responseCode = client.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            }
            else {
                response = "";
            }
            //client.disconnect();
            //JSONObject obj = new JSONObject(response);
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        finally {
            if(client != null) // Make sure the connection is not null.
                client.disconnect();
        }
    }
    @Override
    protected Long doInBackground(URL... params) {
        try {
            getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // This counts how many bytes were downloaded
        final byte[] result = response.getBytes();
        Long numOfBytes = Long.valueOf(result.length);
        return numOfBytes;
    }
    protected void onPostExecute(Long result) {
        try {
            delegate.onResponseReceived(new JSONObject(response));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
