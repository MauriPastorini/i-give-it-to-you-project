package ApiCommunicationManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Domain.Account;
import Domain.ResponseHttp;

/**
 * Created by Mauri on 28-May-17.
 */

public class AccountApiCommunication {

    public ResponseHttp postAccount(Account account) throws JSONException, IOException{
        String data = account.GetAsJSON().toString();
        ResponseHttp responseHttp = new ConnectionHandler().postData(ApiServerConstant.accountPostUri, ConnectionHandler.Content_Type.JSON, data, null);
        return responseHttp;
    }

    public ResponseHttp postToken(Account account, Context context) throws JSONException, IOException {
        String data = createPostUrlEncoded(account);
        ResponseHttp responseHttp = new ConnectionHandler().postData(ApiServerConstant.accountPostTokenUri, ConnectionHandler.Content_Type.URL_ENCODED, data, null);
        if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.SUCCESS){
            saveToken(responseHttp.getMessage(), context);
        }
        String tokenResp = getToken(context);
        return responseHttp;
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

    public ResponseHttp setAsAdminAccount(Account account, Context context) throws IOException, JSONException {
        ResponseHttp responseHttp = new ConnectionHandler().postData(ApiServerConstant.accountSetAsAdminUri(account.getId()), ConnectionHandler.Content_Type.JSON, account.GetAsJSON().toString(), getToken(context));
        return responseHttp;
    }

    public ResponseHttp deleteAccount(Account account, Context context) throws IOException, JSONException {
        String data = account.GetAsJSON().toString();
        ResponseHttp responseHttp = new ConnectionHandler().deleteData(ApiServerConstant.accountDeleteUri(account.getId()), ConnectionHandler.Content_Type.JSON, data, getToken(context));
        return responseHttp;
    }

    public ResponseHttp getUnmoderatedUsers(Context context) throws IOException, JSONException {
        ResponseHttp responseHttp = new ConnectionHandler().getData(ApiServerConstant.accountGetUri, ConnectionHandler.Content_Type.JSON, getToken(context));
        if (responseHttp.getTypeCode() != ResponseHttp.CategoryCodeResponse.SUCCESS){
            return responseHttp;
        }
        ResponseHttp finalResponse = new ResponseHttp(200);
        List<Account> accounts = decodeDataToAccounts(responseHttp.getMessage());
        finalResponse.setMessageObject(accounts);
        return finalResponse;
    }

    private List<Account> decodeDataToAccounts(String json){
        List<Account> result = new ArrayList<Account>();
        try {
            JSONArray data = new JSONArray(json);
            for (int i = 0; i < data.length(); i++){
                JSONObject jsonObject = data.getJSONObject(i);
                int userId = jsonObject.getInt("userId");
                String userName = jsonObject.getString("userName");
                String email = jsonObject.getString("email");
                Account a = new Account(userId, userName, email, "", false);
                result.add(a);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
