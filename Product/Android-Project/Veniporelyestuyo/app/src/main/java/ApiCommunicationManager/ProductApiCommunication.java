package ApiCommunicationManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import Domain.Product;
import Domain.ResponseHttp;
import MyExceptions.PostReturnFunctionException;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by Mauri on 07-May-17.
 */

public class ProductApiCommunication implements IHttpApiPostCommunication {
    private static final String TAG = "myLogMessageTag";
    private Product product;
    private boolean photosUploaded;

    public void postProduct(Product productParm) throws JSONException, IOException{
        photosUploaded = false;
        Log.i(TAG, "Comenzando post product");
        product = productParm;
        JSONObject productJson = createProductJsonData(product.name, product.categoryId, product.stateId, product.latitude, product.longitude);
        new ConnectionHandler().postDataJson(this, ApiServerConstant.productPostUri, productJson);
    }

    @NonNull
    private JSONObject createProductJsonData(String name, int category, int state, double latitude, double longitude) throws JSONException {
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
            new ConnectionHandler().postDataJson(this, ApiServerConstant.productPostPhotoUri(product.id), productsPhotosJson.get(i));
        }
    }

    private ArrayList<JSONObject> createPhotosJsonData(Product product) {
        ArrayList<byte[]> photos = new ArrayList<>();
        if (product.image1 != null)
            photos.add(convertBitmapToByte(product.image1));
        else
            photos.add(new byte[0]);
        if (product.image2 != null)
            photos.add(convertBitmapToByte(product.image2));
        else
            photos.add(new byte[0]);
        if (product.image3 != null)
            photos.add(convertBitmapToByte(product.image3));
        else
            photos.add(new byte[0]);
        ArrayList<JSONObject> jsonPhotos = new ArrayList<>();
        for(int i=0; i<photos.size();i++){
            JSONObject photoJson = new JSONObject();
            try{
                photoJson.put("ImageName", ("Photo " + i));
                String encoded = Base64.encodeToString(photos.get(i), Base64.DEFAULT);
                photoJson.put("ImageBase64", encoded);
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

    @Override
    public void postFunctionReturn(Object obj) throws PostReturnFunctionException{
        if (!photosUploaded){
            ResponseHttp response = (ResponseHttp)obj;
            String res = response.getMessage();
            res = res.replace("\n","");
            int id  = Integer.parseInt(res);
            Product actualProduct = this.product;
            actualProduct.id = id;
            photosUploaded = true;
            try{
                this.postProductPhoto(actualProduct);
            }
            catch (JSONException ex){
                throw new PostReturnFunctionException("Error JSONException posting image: " + ex);
            }
            catch (IOException ex){
                throw new PostReturnFunctionException("Error IOException posting image: " + ex);
            }
        }
    }

    public ResponseHttp getProductAndImages(int productId) throws IOException, JSONException {
        ResponseHttp responseHttpProduct = new ConnectionHandler().getDataInJson2(ApiServerConstant.productGetUri(productId), ConnectionHandler.Content_Type.JSON);
        if (responseHttpProduct.getTypeCode() != ResponseHttp.CategoryCodeResponse.SUCCESS){
            return responseHttpProduct;
        }
        ResponseHttp responseHttpProductImage = new ConnectionHandler().getDataInJson2(ApiServerConstant.productPhotoGetUri(productId), ConnectionHandler.Content_Type.JSON);
        if(responseHttpProductImage.getTypeCode() != ResponseHttp.CategoryCodeResponse.SUCCESS){
            return responseHttpProductImage;
        }
        Product product = decodeMessageToProduct(responseHttpProduct.getMessage());
        loadProductPhotos(product, responseHttpProductImage.getMessage());
        ResponseHttp finalResponse = new ResponseHttp(200);
        finalResponse.setMessageObject(product);
        return finalResponse;
    }

    private void loadProductPhotos(Product product, String message) throws JSONException{
        JSONArray productPhotos = new JSONArray(message);
        ArrayList<Bitmap> photos = new ArrayList<>();
        ArrayList<String> photosNames = new ArrayList<>();

        for(int i = 0; i<productPhotos.length() ; i++){
            JSONObject cateJson = productPhotos.getJSONObject(i);
            String name = cateJson.getString("ImageName");
            String imageString = cateJson.getString("ImageBase64");

            byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            photos.add(decodedByte);
            photosNames.add(name);
        }
        product.image1 = photos.get(1);
        product.image2 = photos.get(2);
        product.image3 = photos.get(3);
        product.image1Name = photosNames.get(1);
        product.image2Name = photosNames.get(2);
        product.image3Name = photosNames.get(3);
    }

    private Product decodeMessageToProduct(String message) throws JSONException{
        Product product = new Product();
        JSONObject productJson = new JSONObject(message);
        product.id = productJson.getInt("productId");
        product.categoryId = productJson.getJSONObject("category").getInt("categoryId");
        product.categoryName = productJson.getJSONObject("category").getString("categoryId");
        product.stateId = productJson.getJSONObject("state").getInt("productStateId");
        product.stateName = productJson.getJSONObject("category").getString("categoryId");
        return product;
    }
}


