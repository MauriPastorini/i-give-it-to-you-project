package ApiCommunicationManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import Domain.Account;
import Domain.ResponseHttp;

/**
 * Created by Mauri on 28-May-17.
 */

public class AccountApiCommunication {

    public ResponseHttp postAccount(Account account) throws JSONException, IOException{
        String data = account.GetAsJSON().toString();
        ResponseHttp responseHttp = new ConnectionHandler().postData(ApiServerConstant.accountPostUri, ConnectionHandler.Content_Type.JSON, data);
        return responseHttp;
    }

    public ResponseHttp postToken(Account account, Context context) throws JSONException, IOException {
        String data = createPostUrlEncoded(account);
        ResponseHttp responseHttp = new ConnectionHandler().postData(ApiServerConstant.accountPostTokenUri, ConnectionHandler.Content_Type.URL_ENCODED, data);
        if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.SUCCESS){
            saveToken(responseHttp.getMessage(), context);
        }
        saveAccount(account,context);
        String tokenResp = getToken(context);
        return responseHttp;
    }

    private void saveAccount(Account account,Context context) {
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("name", account.getUserName());
        editor.putString("email", account.getEmail());
        editor.commit();
    }

    private void saveToken(String message, Context context) throws IOException,JSONException{
        String token = extractTokenFromMessage(message);
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("Token", token);
        editor.commit();
    }

    private String extractTokenFromMessage(String message) throws IOException, JSONException{
        JSONObject tokenJson = new JSONObject(message);
        return tokenJson.getString("access_token");
    }

    @NonNull
    private String createPostUrlEncoded(Account account) throws JSONException {
        StringBuilder res = new StringBuilder();
        res.append("grant_type=password");
        res.append("&username=");
        res.append(account.getUserName());
        res.append("&password=");
        res.append(account.getPassword());
        return res.toString();
    }

    public String getToken(Context context) {
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getString("Token", "error");
    }

    public void signOut(Context context) {
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("Token", "");
        editor.commit();
    }

    private String getFromSharedPreference(String key, Context context){
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getString(key, "error");
    }

    public Account getAccountInformation(Context context){
        String name = getFromSharedPreference("name", context);
        String email = getFromSharedPreference("email", context);

        Account account = new Account(name, email);
        return account;
    }
}
