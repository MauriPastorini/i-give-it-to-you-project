package ApiCommunicationManager;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import Domain.User;
import MyExceptions.PostReturnFunctionException;

/**
 * Created by faustosanchez on 3/6/17.
 */

public class UserApiCommunication implements IHttpApiPostCommunication {

    public void postUser(User user) throws JSONException, IOException {
        JSONObject userJson = user.GetAsJSON();
        new ConnectionHandler().postDataJson(this, ApiServerConstant.userPostUri, userJson);
    }

    @Override
    public void postFunctionReturn(Object obj) throws PostReturnFunctionException {
        // Handle post response
    }
}
