package ApiCommunicationManager;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Mauri on 07-May-17.
 */

public class ProductApiCommunication {
    private static final String TAG = "myLogMessageTag";

    public static void postProduct(String name, String category, String state){
        Log.i(TAG, "Comenzando post product");
        try{
            HttpURLConnection con = createPostJsonConnection();
            Log.i(TAG, "Coneccion establecida");
            JSONObject product = new JSONObject();
            JSONObject parent=new JSONObject();
            product.put("name", name);
            product.put("category", category);
            product.put("state", state);

            OutputStreamWriter wr= new OutputStreamWriter(con.getOutputStream());
            wr.write(parent.toString());
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
            } else {
                Log.i(TAG, con.getResponseMessage());
            }

        } catch (IOException ex){
            Log.i(TAG, "Error en crear coneccion post" + ex.toString());
        }
        catch (JSONException ex){
            Log.i(TAG, "Error en JSON post" + ex.toString());
        }
    }

    private static HttpURLConnection createPostJsonConnection() throws IOException {
        String url= ApiServerConstant.productPostUri;
        URL object=new URL(url);
        HttpURLConnection con = (HttpURLConnection)object.openConnection();
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestMethod("POST");
        return con;
    }
}
