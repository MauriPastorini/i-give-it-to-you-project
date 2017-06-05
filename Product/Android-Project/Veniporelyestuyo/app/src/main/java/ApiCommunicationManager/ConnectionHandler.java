package ApiCommunicationManager;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

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
import MyExceptions.PostReturnFunctionException;
import MyStaticElements.DialogCloseDueToConnection;

import static android.content.ContentValues.TAG;

/**
 * Created by Mauri on 08-May-17.
 */

public class ConnectionHandler {

    //GET verb methods
    public ResponseHttp getDataInJson(String url) throws IOException{
        HttpURLConnection con = createGetJsonConnection(url);
        return getHttpRequestData(con);
    }

    private static HttpURLConnection createGetJsonConnection(String url) throws IOException {
        URL object=new URL(url);
        HttpURLConnection con = (HttpURLConnection)object.openConnection();
        con.setDoInput(true);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestMethod("GET");
        return con;
    }

    private ResponseHttp getHttpRequestData(HttpURLConnection con) throws IOException{
        StringBuilder stringBuilder = new StringBuilder();
        int httpResult = con.getResponseCode();
        ResponseHttp response = new ResponseHttp(httpResult);

        return readFromConnectionResponse(con, stringBuilder, response);
    }

    //POST verb methods
    public void postDataJson(IHttpApiPostCommunication httpHandler, String url, JSONObject data) throws IOException{
        Object[] objs = new Object[3];
        HttpURLConnection con = createPostJsonConnection(url);
        objs[0] = con;
        objs[1] = data;
        objs[2] = httpHandler;
        new PostProductTask().execute(objs);
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
        protected void onPostExecute(Object result){
            if(result != null){
                Pair pairRes = (Pair)result;
                IHttpApiPostCommunication httpHandler = (IHttpApiPostCommunication)pairRes.first;
                try{
                    httpHandler.postFunctionReturn(pairRes.second);
                }catch (PostReturnFunctionException ex){
                    Log.i(TAG, ex.getMessage());
                }
            }
        }

        @Override
        protected Object doInBackground(Object[] params) {
            HttpURLConnection con = (HttpURLConnection)params[0];
            JSONObject dataJson = (JSONObject)params[1];
            try{
                con.connect();
                OutputStream os = con.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                String json = dataJson.toString();
                osw.write(json);
                osw.flush();
                osw.close();
                //Read response
                StringBuilder stringBuilder = new StringBuilder();
                int httpResult = con.getResponseCode();
                ResponseHttp response = new ResponseHttp(httpResult);
                response = readFromConnectionResponse(con, stringBuilder, response);
                Pair<IHttpApiPostCommunication,ResponseHttp> result = Pair.create((IHttpApiPostCommunication)params[2],response);
                return result;
            }
            catch (IOException ex){
                Log.i(TAG, "Error en comunicacion post");
            }
            return null;
        }
    }
    @NonNull
    private ResponseHttp readFromConnectionResponse(HttpURLConnection con, StringBuilder stringBuilder, ResponseHttp response) throws IOException {
        BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"));
        String line;
        while ((line = br.readLine()) != null) {
            stringBuilder.append(line + "\n");
        }
        br.close();
        response.setMessage(stringBuilder.toString());
        return response;
    }

    private class PostProductPhotoTask extends AsyncTask {
        protected void onPostExecute(Object result){
            if(result != null){
                Pair pairRes = (Pair)result;
                Log.i(TAG, "Post realizado");
                IHttpApiPostCommunication httpHandler = (IHttpApiPostCommunication)pairRes.first;
                try{
                    httpHandler.postFunctionReturn(pairRes.second);
                }catch (PostReturnFunctionException ex){
                    Log.i(TAG, ex.getMessage());
                }
            }
        }

        @Override
        protected Object doInBackground(Object[] params) {
            HttpURLConnection con = (HttpURLConnection)params[0];
            String dataJson = (String)params[1];
            try{
                con.connect();
                OutputStream os = con.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                String json = dataJson.toString();
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
                    Pair<IHttpApiPostCommunication,String> result = Pair.create((IHttpApiPostCommunication)params[2],sb.toString());
                    return result;
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

    // NEW
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

    public enum Verb {
        POST, GET, DELETE, PUT
    }
    public enum Content_Type {
        JSON, URL_ENCODED, XML
    }


    private static HttpURLConnection createFormatConnection(String url, Verb verb, Content_Type content_type) throws IOException {
        URL object=new URL(url);
        HttpURLConnection con = (HttpURLConnection)object.openConnection();
        con.setDoOutput(true);
        con.setDoInput(true);
        if (content_type == Content_Type.JSON){
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
        } else if(content_type == Content_Type.URL_ENCODED){
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        }
        String verb2 = verb.name();
        con.setRequestMethod(verb2);
        return con;
    }

    public ResponseHttp postData2(String url, Content_Type content_type, String data) throws IOException, JSONException{
        HttpURLConnection con = createFormatConnection(url, Verb.POST, content_type);
        return connectAndGetResponse(data, con);
    }

    public ResponseHttp getDataInJson2(String url, Content_Type content_type) throws IOException, JSONException{
        HttpURLConnection con = createFormatConnection(url, Verb.GET, content_type);
        return connectAndGetResponse(null, con);
    }


    @NonNull
    ResponseHttp connectAndGetResponse(String data, HttpURLConnection con) throws IOException, JSONException {
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
