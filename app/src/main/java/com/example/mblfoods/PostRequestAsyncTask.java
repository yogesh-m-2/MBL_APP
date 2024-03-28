package com.example.mblfoods;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class PostRequestAsyncTask extends AsyncTask<String, Void, String> {

    private OnPostRequestListener listener;
    private Map<String, String> postData;

    public PostRequestAsyncTask(OnPostRequestListener listener, Map<String, String> postData) {
        this.listener = listener;
        this.postData = postData;
    }

    @Override
    protected String doInBackground(String... params) {
        String urlString = params[0];
        String result = "";

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);

            OutputStream outputStream = urlConnection.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);
            writer.write(getPostDataString(postData));
            writer.flush();
            writer.close();

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                result = response.toString();
            } else {
                result = "Error: " + responseCode;
            }

            urlConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            result = "Error: " + e.getMessage();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        if (listener != null) {
            listener.onPostRequestCompleted(result);
        }
    }

    private String getPostDataString(Map<String, String> params) throws Exception {
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(param.getKey()).append('=').append(param.getValue());
        }
        return postData.toString();
    }

    public interface OnPostRequestListener {
        void onPostRequestCompleted(String result);
    }
}
