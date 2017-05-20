package ApiCommunicationManager;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
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

    public void controlConnectionsAvaiable(Context activityContext) {
        //controlInternetAvailable(activityContext); : HARDCODED: commented for testing
        controlApiBackendAvailable(activityContext);
    }

    private void controlInternetAvailable(Context context) {
        Object[] objs = new Object[4];
        objs[0] = ApiServerConstant.serverUrl;
        objs[1] = context;
        objs[2] = "Finish application";
        objs[3] = "Sorry but our server is not avaiable, try later.";
        new CheckConnection().execute(objs);
    }

    private void controlApiBackendAvailable(Context context) {
        Object[] objs = new Object[4];
        objs[0] = ApiServerConstant.serverUrl;
        objs[1] = context;
        objs[2] = "Finish application";
        objs[3] = "Sorry but our server is not avaiable, try later.";
        new CheckConnection().execute(objs);
    }
    private class CheckConnection extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            String url = (String)params[0];
            final Context context = (Context) params[1];
            final String title = (String)params[2];
            final String message = (String)params[3];
            try {
                SocketAddress socketAddress = new InetSocketAddress("192.168.0.114", 51339);
                Socket socket = new Socket();
                socket.connect(socketAddress, 900000);
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
}
