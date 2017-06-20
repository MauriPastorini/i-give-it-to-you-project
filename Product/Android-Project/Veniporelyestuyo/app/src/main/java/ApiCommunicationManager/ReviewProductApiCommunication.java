package ApiCommunicationManager;

import android.content.Context;

import org.json.JSONException;

import java.io.IOException;

import Domain.ResponseHttp;


/**
 * Created by Mauri on 20-Jun-17.
 */

public class ReviewProductApiCommunication {
    private String token;
    private Context context;

    public ReviewProductApiCommunication(Context context) {
        this.context = context;
        token = new AccountApiCommunication().getToken(context);
    }

    public ResponseHttp registerReview(int productId, int rate) throws IOException,JSONException{
        return new ConnectionHandler().postData(ApiServerConstant.rateProduct(productId,rate), ConnectionHandler.Content_Type.NONE, "", token);
    }
}
