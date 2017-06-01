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

    public ResponseHttp postToken(Account account, Context context) throws JSONException, IOException {
        String data = createPostUrlEncoded(account);
        ResponseHttp responseHttp = new ConnectionHandler().postData2(ApiServerConstant.accountPostTokenUri, ConnectionHandler.Content_Type.URL_ENCODED, data);
        if(!(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.SUCCESS)){
            saveToken(responseHttp.getMessage(), context);
        }
        String tokenResp = getToken(context);
        return responseHttp;
    }

    private void saveToken(String message, Context context) {
        String token = extractTokenFromMessage(message);
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("Token", token);
        editor.commit();
    }

    private String extractTokenFromMessage(String message){
        return "Hardcoded test token";
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
}
