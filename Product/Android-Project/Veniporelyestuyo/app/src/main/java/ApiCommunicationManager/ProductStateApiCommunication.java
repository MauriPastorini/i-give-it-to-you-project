package ApiCommunicationManager;

import android.content.res.Resources;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import Domain.Category;
import Domain.Product;
import Domain.ProductState;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by Mauri on 11-May-17.
 */

public class ProductStateApiCommunication {
    private static final String TAG = "myLogMessageTag";

    public ArrayList<ProductState> getProductState() throws JSONException, IOException {
        Log.i(TAG, "Comenzando get products");

        ArrayList<ProductState> productStatesResult = new ArrayList<ProductState>();
        String productStatesGetResult = new ConnectionHandler().getDataInJson(ApiServerConstant.productStateApiUri);
        JSONArray productStatesArray = new JSONArray(productStatesGetResult);

        for(int i = 0; i<productStatesArray.length() ; i++){
            JSONObject cateJson = productStatesArray.getJSONObject(i);
            String name = cateJson.getString("Name");
            int id = Integer.parseInt(cateJson.getString("ProductStateId"));
            ProductState productState = new ProductState();
            productState.setId(id);
            productState.setName(name);
            productStatesResult.add(productState);
        }
        return productStatesResult;
    }

    public int getStateIdFromStatesCollection(String stateName, ArrayList<ProductState> productStates){
        for (int i = 0; i < productStates.size(); i++) {
            if (productStates.get(i).getName() == stateName) {
                return productStates.get(i).getId();
            }
        }
        throw new Resources.NotFoundException();
    }
}
