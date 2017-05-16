package ApiCommunicationManager;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import Domain.Product;

import static android.R.attr.bitmap;

/**
 * Created by Mauri on 07-May-17.
 */

public class ProductApiCommunication {
    private static final String TAG = "myLogMessageTag";

    public void postProduct(Product product) throws JSONException, IOException{
        Log.i(TAG, "Comenzando post product");
        Log.i(TAG, "Coneccion establecida");
        JSONObject productJson = createProductJsonData(product.name, product.category, product.state, product.latitude, product.longitude);
        new ConnectionHandler().PostDataJson(ApiServerConstant.productPostUri, productJson);
        //TODO: Send product images to the api
    }

    @NonNull
    private JSONObject createProductJsonData(String name, String category, String state, double latitude, double longitude) throws JSONException {
        JSONObject product = new JSONObject();
        product.put("Name", name);
        product.put("CategoryId", category);
        product.put("State", state);
        product.put("Latitude", latitude);
        product.put("Longitude", longitude);
        return product;
    }

    public void postProductPhoto(Product product) throws JSONException, IOException{
        Log.i(TAG, "Comenzando post photo");
        ArrayList<JSONObject> productsPhotosJson = createPhotosJsonData(product);
        for(int i=0; i<productsPhotosJson.size();i++){
            new ConnectionHandler().PostDataJson(ApiServerConstant.productPostPhotoUri, productsPhotosJson.get(i));
        }
        //TODO: Send product images to the api
    }

    private ArrayList<JSONObject> createPhotosJsonData(Product product) {
        ArrayList<byte[]> photos = new ArrayList<>();
        photos.add(convertBitmapToByte(product.image1));
        photos.add(convertBitmapToByte(product.image2));
        photos.add(convertBitmapToByte(product.image3));

        ArrayList<JSONObject> jsonPhotos = new ArrayList<>();
        for(int i=0; i<photos.size();i++){
            JSONObject photoJson = new JSONObject();
            try{
                photoJson.put("ImageName", ("Photo " + i));
                photoJson.put("ImageByteArray", photos.get(i).toString());
            }
            catch (JSONException ex){
                Log.i(TAG, "Error convirtiendo a foto a json");
            }
            jsonPhotos.add(photoJson);
        }
        return jsonPhotos;
    }

    private byte[] convertBitmapToByte(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
}


