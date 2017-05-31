package ApiCommunicationManager;

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

    public ResponseHttp postToken(Account account) throws JSONException, IOException {
        String data = createPostUrlEncoded(account);
        ResponseHttp responseHttp = new ConnectionHandler().postData2(ApiServerConstant.accountPostTokenUri, ConnectionHandler.Content_Type.URL_ENCODED, data);
        return responseHttp;
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
}
