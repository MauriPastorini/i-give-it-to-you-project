package ApiCommunicationManager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.common.api.Response;
import com.product.whitewalkers.veniporelyestuyo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Domain.Account;
import Domain.Product;
import Domain.ResponseHttp;

import static com.product.whitewalkers.veniporelyestuyo.R.string.state;

/**
 * Created by Mauri on 07-May-17.
 */

public class ProductApiCommunication{
    private static final String TAG = "myLogMessageTag";
    private Product product;
    private String token;
    private Context context;

    public ProductApiCommunication(Context context) {
        this.context = context;
        token = new AccountApiCommunication().getToken(context);
    }

    public ResponseHttp postProduct(Product productParm, Context context) throws JSONException, IOException{
        Log.i(TAG, "Comenzando post product");
        Account account = new AccountApiCommunication().getAccountInformation(context);
        product = productParm;
        JSONObject productJson = new JSONObject();
        productJson.put("Name", product.name);
        productJson.put("CategoryId", product.categoryId);
        productJson.put("State", product.stateId);
        productJson.put("Latitude", product.latitude);
        productJson.put("Longitude", product.longitude);
        productJson.put("UserId", account.getId());
        productJson.put("Description", product.description);
        ResponseHttp responseHttp = new ConnectionHandler().postData(ApiServerConstant.productPostUri, ConnectionHandler.Content_Type.JSON, productJson.toString(), token);
        if (responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.SUCCESS)
            responseHttp = postFunctionReturn(responseHttp, context);
        return responseHttp;
    }

    public ResponseHttp postProductPhoto(Product product, Context context) throws JSONException, IOException{
        ArrayList<JSONObject> productsPhotosJson = createPhotosJsonData(product);
        for(int i=0; i<productsPhotosJson.size();i++){
            ResponseHttp responseAux = new ConnectionHandler().postData(ApiServerConstant.productPostPhotoUri(product.id), ConnectionHandler.Content_Type.JSON, productsPhotosJson.get(i).toString(), token);
            if (responseAux.getTypeCode() != ResponseHttp.CategoryCodeResponse.SUCCESS){
                return responseAux;
            }
        }
        ResponseHttp finalResponse = new ResponseHttp(200);
        return finalResponse;
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

    public ResponseHttp postFunctionReturn(ResponseHttp obj, Context context) throws IOException, JSONException{
        ResponseHttp response = obj;
        String res = response.getMessage();
        res = res.replace("\n","");
        int id  = Integer.parseInt(res);
        Product actualProduct = this.product;
        actualProduct.id = id;
        ResponseHttp newResponse = this.postProductPhoto(actualProduct, context);
        return newResponse;
    }

    public ResponseHttp getProductAndImages(int productId) throws IOException, JSONException {
        ResponseHttp responseHttpProduct = new ConnectionHandler().getData(ApiServerConstant.productGetUri(productId), ConnectionHandler.Content_Type.JSON, token);
        if (responseHttpProduct.getTypeCode() != ResponseHttp.CategoryCodeResponse.SUCCESS){
            return responseHttpProduct;
        }
        ResponseHttp responseHttpProductImage = new ConnectionHandler().getData(ApiServerConstant.productPhotoGetUri(productId), ConnectionHandler.Content_Type.JSON, token);
        if(responseHttpProductImage.getTypeCode() != ResponseHttp.CategoryCodeResponse.SUCCESS){
            return responseHttpProductImage;
        }
        Product product = decodeMessageToProduct(responseHttpProduct.getMessage());
        loadProductPhotos(product, responseHttpProductImage.getMessage());
        ResponseHttp finalResponse = new ResponseHttp(200);
        finalResponse.setMessageObject(product);
        return finalResponse;
    }

    public ResponseHttp acceptProduct(int productId, Context context) throws IOException, JSONException{
        ResponseHttp responseHttpAcceptProduct = new ConnectionHandler().postData(ApiServerConstant.acceptProductUri(productId), ConnectionHandler.Content_Type.JSON,null, token);
        if (responseHttpAcceptProduct.getTypeCode() != ResponseHttp.CategoryCodeResponse.SUCCESS){
            return responseHttpAcceptProduct;
        }
        ResponseHttp finalResponse = new ResponseHttp(200);
        return finalResponse;
    }

    public ResponseHttp deleteProduct(int productId, Context context) throws IOException, JSONException{
        ResponseHttp responseHttpAcceptProduct = new ConnectionHandler().deleteData(ApiServerConstant.deleteProductUri(productId), ConnectionHandler.Content_Type.JSON, "", token);
        if (responseHttpAcceptProduct.getTypeCode() != ResponseHttp.CategoryCodeResponse.SUCCESS){
            return responseHttpAcceptProduct;
        }
        ResponseHttp finalResponse = new ResponseHttp(200);
        return finalResponse;
    }

    public ResponseHttp getUnmoderatedProducts(Context context) throws IOException, JSONException{
        ResponseHttp responseHttpUnmoderatedProducts = new ConnectionHandler().getData(ApiServerConstant.getUnmoderatedProductsUri, ConnectionHandler.Content_Type.JSON, token);
        if (responseHttpUnmoderatedProducts.getTypeCode() != ResponseHttp.CategoryCodeResponse.SUCCESS){
            return responseHttpUnmoderatedProducts;
        }
        List<Product> unmoderatedProducts = decodeResponseProductList(responseHttpUnmoderatedProducts.getMessage());
        ResponseHttp finalResponse = new ResponseHttp(200);
        finalResponse.setMessageObject(unmoderatedProducts);
        return finalResponse;
    }

    public ResponseHttp getProductsByCategory(int categoryId, Context context) throws IOException, JSONException{
        ResponseHttp responseHttpProductsByCategory = new ConnectionHandler().getData(ApiServerConstant.getProductsByCategory(categoryId), ConnectionHandler.Content_Type.JSON, token);
        if (responseHttpProductsByCategory.getTypeCode() != ResponseHttp.CategoryCodeResponse.SUCCESS){
            return responseHttpProductsByCategory;
        }
        List<Product> productsByCategory = decodeResponseProductList(responseHttpProductsByCategory.getMessage());
        ResponseHttp finalResponse = new ResponseHttp(200);
        finalResponse.setMessageObject(productsByCategory);
        return finalResponse;
    }

    public ResponseHttp getProductsByCountry(String country, Context context) throws IOException, JSONException{
        ResponseHttp responseHttpProductsByCountry = new ConnectionHandler().getData(ApiServerConstant.getProductsByCountry(country), ConnectionHandler.Content_Type.JSON, token);
        if (responseHttpProductsByCountry.getTypeCode() != ResponseHttp.CategoryCodeResponse.SUCCESS){
            return responseHttpProductsByCountry;
        }
        List<Product> productsByCountry = decodeResponseProductList(responseHttpProductsByCountry.getMessage());
        ResponseHttp finalResponse = new ResponseHttp(200);
        finalResponse.setMessageObject(productsByCountry);
        return finalResponse;
    }

    private List<Product> decodeResponseProductList(String message)throws JSONException{
        JSONArray productsToDecode = new JSONArray(message);
        List<Product> decodedProducts = new ArrayList<Product>();
        for (int currentProduct = 0; currentProduct <productsToDecode.length() ; currentProduct++) {
            JSONObject currentProductJSON = productsToDecode.getJSONObject(currentProduct);
            Product currentProductObj = decodeMessageToProduct(currentProductJSON.toString());
            decodedProducts.add(currentProductObj);
        }
        return decodedProducts;
    }

    private void loadProductPhotos(Product product, String message) throws JSONException{
        JSONArray productPhotos = new JSONArray(message);
        ArrayList<Bitmap> photos = new ArrayList<>();
        ArrayList<String> photosNames = new ArrayList<>();

        for(int i = 0; i<productPhotos.length() ; i++){
            JSONObject cateJson = productPhotos.getJSONObject(i);
            String name = cateJson.getString("imageName");
            String imageString = cateJson.getString("imageBase64");

            byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            photos.add(decodedByte);
            photosNames.add(name);
        }
        if(productPhotos.length() == 0) {
            Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_image);
            product.image1 = largeIcon;
            product.image2 = largeIcon;
            product.image3 = largeIcon;
            return;
        }
        product.image1 = photos.get(0);
        product.image1Name = photosNames.get(0);
        if(productPhotos.length() == 1) {
            Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_image);
            product.image2 = largeIcon;
            product.image3 = largeIcon;
            return;
        }
        product.image2 = photos.get(1);
        product.image2Name = photosNames.get(1);
        if(productPhotos.length() == 2) {
            Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_image);
            product.image3 = largeIcon;
            return;
        }
        product.image3 = photos.get(2);
        product.image3Name = photosNames.get(2);
    }

    private Product decodeMessageToProduct(String message) throws JSONException{
        Product product = new Product();
        JSONObject productJson = new JSONObject(message);
        product.id = productJson.getInt("productId");
        product.name = productJson.getString("name");
        product.categoryId = productJson.getInt("categoryId");
        product.categoryName = productJson.getString("name");
        product.stateId = productJson.getInt("stateId");
        product.stateName = productJson.getString("name");
        product.latitude = productJson.getDouble("latitude");
        product.longitude = productJson.getDouble("longitude");
        product.description = productJson.getString("description");
        return product;
    }

    public ResponseHttp registerSolicitude(int productId, Context context) throws JSONException,IOException {
        int idAccount = new AccountApiCommunication().getAccountInformation(context).getId();
        if (idAccount == 0){
            ResponseHttp responseHttp = new ResponseHttp(500);
            responseHttp.setMessage("No hay datos del usuario en el sistema");
            return responseHttp;
        }
        return new ConnectionHandler().postData(ApiServerConstant.postSolicitude(productId,idAccount),ConnectionHandler.Content_Type.JSON,"",token);
    }

    public  ResponseHttp cancelSolicitude(int productId)throws JSONException,IOException {
        int idAccount = new AccountApiCommunication().getAccountInformation(context).getId();
        if (idAccount == 0){
            ResponseHttp responseHttp = new ResponseHttp(500);
            responseHttp.setMessage("No hay datos del usuario en el sistema");
            return responseHttp;
        }
        return new ConnectionHandler().deleteData(ApiServerConstant.deleteSolicitude(productId,idAccount),ConnectionHandler.Content_Type.JSON,"",token);
    }

    public ResponseHttp getProductsSolicitatedByClient(Context context) throws IOException, JSONException{
        int idAccount = new AccountApiCommunication().getAccountInformation(context).getId();
        ResponseHttp response = new ConnectionHandler().getData(ApiServerConstant.getProductsSolicitatedByClientUri(idAccount), ConnectionHandler.Content_Type.NONE, token);
        if (response.getTypeCode() != ResponseHttp.CategoryCodeResponse.SUCCESS){
            return response;
        }
        List<Product> productsOfUser = decodeResponseProductList(response.getMessage());
        ResponseHttp finalResponse = new ResponseHttp(200);
        finalResponse.setMessageObject(productsOfUser);
        return finalResponse;
    }
}


