package ApiCommunicationManager;

import android.support.annotation.NonNull;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

import Domain.Product;

/**
 * Created by Mauri on 07-May-17.
 */

public class ProductApiCommunication {
    private static final String TAG = "myLogMessageTag";

    public void postProduct(Product product) throws JSONException, IOException{
        Log.i(TAG, "Comenzando post product");
        Log.i(TAG, "Coneccion establecida");
        JSONObject productJson = createJsonData(product.name, product.category, product.state, product.latitude, product.longitude);
        new ConnectionHandler().PostDataJson(ApiServerConstant.productPostUri, productJson);
        //TODO: Send product images to the api
    }

    @NonNull
    private JSONObject createJsonData(String name, String category, String state, double latitude, double longitude) throws JSONException {
        JSONObject product = new JSONObject();
        product.put("Name", name);
        product.put("CategoryId", category);
        product.put("State", state);
        product.put("Latitude", latitude);
        product.put("Longitude", longitude);
        return product;
    }
}


