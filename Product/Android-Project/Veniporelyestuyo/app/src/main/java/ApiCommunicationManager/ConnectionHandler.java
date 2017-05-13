package ApiCommunicationManager;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.R.attr.data;
import static android.content.ContentValues.TAG;

/**
 * Created by Mauri on 08-May-17.
 */

public class ConnectionHandler {

    public String getDataInJson(String url) throws IOException{
        Object[] objs = new Object[2];
        HttpURLConnection con = createGetJsonConnection(url);
        return getHttpRequestData(con);
    }

    private String getHttpRequestData(HttpURLConnection con) throws IOException{
        StringBuilder sb = new StringBuilder();
        int HttpResult = con.getResponseCode();
        if (HttpResult == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            Log.i(TAG, "" + sb.toString());
            return sb.toString();
        } else {
            Log.i(TAG, con.getResponseMessage());
        }
        return "";
    }

    public void PostDataJson(String url, JSONObject data) throws IOException{
        Object[] objs = new Object[2];
        HttpURLConnection con = createPostJsonConnection(url);
        objs[0] = con;
        objs[1] = data;
        new PostProductTask().execute(objs);
    }

    private static HttpURLConnection createGetJsonConnection(String url) throws IOException {
        URL object=new URL(url);
        HttpURLConnection con = (HttpURLConnection)object.openConnection();
        con.setDoInput(true);
        //con.setRequestProperty("Content-Type", "application/json");
        //con.setRequestProperty("Accept", "application/json");
        con.setRequestMethod("GET");
        return con;
    }

    private static HttpURLConnection createPostJsonConnection(String url) throws IOException {
        URL object=new URL(url);
        HttpURLConnection con = (HttpURLConnection)object.openConnection();
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestMethod("POST");
        return con;
    }

    private class PostProductTask extends AsyncTask {
        protected void onPostExecute(Object result) {
            Log.i(TAG, "Post realizado");
        }

        @Override
        protected Object doInBackground(Object[] params) {
            HttpURLConnection con = (HttpURLConnection)params[0];
            JSONObject product = (JSONObject)params[1];
            try{
                con.connect();
                OutputStream os = con.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                String json = product.toString();
                osw.write(json);
                osw.flush();
                osw.close();
                //Read response
                StringBuilder sb = new StringBuilder();
                int HttpResult = con.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(con.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    Log.i(TAG, "" + sb.toString());
                    return sb.toString();
                } else {
                    Log.i(TAG, con.getResponseMessage());
                }
            }
            catch (IOException ex){
                Log.i(TAG, "Error en comunicacion post");
            }
            return null;
        }
    }
}
