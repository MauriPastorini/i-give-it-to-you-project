package ApiCommunicationManager;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;

import Domain.ResponseHttp;
import MyStaticElements.DialogCloseDueToConnection;

/**
 * Created by Mauri on 08-May-17.
 */

public class ConnectionHandler {
    public enum Verb {
        POST, GET, DELETE, PUT
    }

    public enum Content_Type {
        JSON, URL_ENCODED, XML
    }

    public void controlConnectionsAvaiable(Context activityContext) {
        if(hasInternetConnection(activityContext)){
            controlApiBackendAvailable(activityContext);
        }
    }

    private boolean hasInternetConnection(Context context) {
        String title = "Finish application";
        String message = "Turn on your internet please.";
        if(!isNetworkAvailable(context)){
            new DialogCloseDueToConnection().ShowCloseDialog(title, message, context);
            return false;
        }
        return true;
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void controlApiBackendAvailable(Context context) {
        Object[] objs = new Object[4];
        objs[0] = context;
        objs[1] = "Finish application";
        objs[2] = "Sorry but our server is not avaiable, try later.";
        new checkApiConnection().execute(objs);
    }

    private class checkApiConnection extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            final Context context = (Context) params[0];
            final String title = (String)params[1];
            final String message = (String)params[2];
            try {
                SocketAddress socketAddress = new InetSocketAddress(ApiServerConstant.ip, Integer.parseInt(ApiServerConstant.port));
                Socket socket = new Socket();
                socket.connect(socketAddress, ApiServerConstant.timeOutConnectionApi);
                socket.close();
            } catch (Exception e) {
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new DialogCloseDueToConnection().ShowCloseDialog(title, message, context);
                    }
                });
            }
            return null;
        }
    }

    private static HttpURLConnection createConnectionForUrlVerbAndContentType(String url, Verb verb, Content_Type content_type, String token) throws IOException {
        URL object=new URL(url);
        HttpURLConnection con = (HttpURLConnection)object.openConnection();
        if(verb != Verb.GET){
            con.setDoOutput(true);
        }
        con.setDoInput(true);
        if (content_type == Content_Type.JSON){
            if (token != null){
                con.setRequestProperty("Authorization", "bearer " + token);
            }
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
        } else if(content_type == Content_Type.URL_ENCODED){
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        }
        String verb2 = verb.name();
        con.setRequestMethod(verb2);
        con.setConnectTimeout(ApiServerConstant.timeOutConnectionApi);
        return con;
    }

    public ResponseHttp postData(String url, Content_Type content_type, String data, String token) throws IOException, JSONException{
        HttpURLConnection con = createConnectionForUrlVerbAndContentType(url, Verb.POST, content_type, token);
        return connectAndGetResponse(data, con);
    }

    public ResponseHttp putData(String url, Content_Type content_type, String data, String token) throws IOException, JSONException{
        HttpURLConnection con = createConnectionForUrlVerbAndContentType(url, Verb.PUT, content_type, token);
        return connectAndGetResponse(data, con);
    }

    public ResponseHttp getData(String url, Content_Type content_type, String token) throws IOException, JSONException{
        HttpURLConnection con = createConnectionForUrlVerbAndContentType(url, Verb.GET, content_type, token);
        return connectAndGetResponse(null, con);
    }

    public ResponseHttp deleteData(String url, Content_Type content_type, String data, String token) throws IOException, JSONException{
        HttpURLConnection con = createConnectionForUrlVerbAndContentType(url, Verb.DELETE, content_type, token);
        return connectAndGetResponse(data, con);
    }

    @NonNull
    private ResponseHttp connectAndGetResponse(String data, HttpURLConnection con) throws IOException, JSONException {
        if(data != null){
            con.connect();
            OutputStream os = con.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            osw.write(data);
            osw.flush();
            osw.close();
        }
        //Read response
        int httpResponseCode = con.getResponseCode();
        ResponseHttp response = new ResponseHttp(httpResponseCode);
        String bodyRes = readFromConnectionResponse2(con, response);
        response.setMessage(bodyRes);
        return response;
    }

    @NonNull
    private String readFromConnectionResponse2(HttpURLConnection con, ResponseHttp response) throws IOException, JSONException{
        InputStream inputStream;
        StringBuilder stringBuilder = new StringBuilder();
        if (response.getTypeCode() == ResponseHttp.CategoryCodeResponse.CLIENT_ERROR){
            inputStream = con.getErrorStream();
        } else{
            inputStream = con.getInputStream();
        }
        BufferedReader br = new BufferedReader(
                new InputStreamReader(inputStream,"utf-8"));
        String line;
        while ((line = br.readLine()) != null) {
            stringBuilder.append(line + "\n");
        }
        br.close();
        return stringBuilder.toString();
    }
}
