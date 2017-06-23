package ApiCommunicationManager;

import android.content.res.Resources;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import Domain.ProductState;

/**
 * Created by Mauri on 11-May-17.
 */

public class ProductStateApiCommunication {

    ArrayList<ProductState> states = new ArrayList<>();

    ProductState stateNew = new ProductState("Nuevo",1);
    ProductState stateUsed = new ProductState("Usado" ,2);

    public ProductStateApiCommunication() {
        states.add(stateNew);
        states.add(stateUsed);
    }

    public ArrayList<ProductState> getProductState(){
        return states;
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
