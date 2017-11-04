package ApiCommunicationManager;

import android.content.Context;
import android.content.res.Resources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import Domain.Category;
import Domain.ResponseHttp;


/**
 * Created by Mauri on 11-May-17.
 */

public class CategoryApiCommunication {
    private static final String TAG = "myLogMessageTag";

    public ResponseHttp getCategories(Context context) throws JSONException, IOException {
        String token = (new AccountApiCommunication()).getToken(context);
        ResponseHttp response = new ConnectionHandler().getData(ApiServerConstant.categoryGetUri, ConnectionHandler.Content_Type.JSON, token);
        ArrayList<Category> categories = getCategoriesFromJsonString(response.getMessage());
        ResponseHttp finalResponse = new ResponseHttp(200);
        finalResponse.setMessageObject(categories);
        return finalResponse;
    }

    ArrayList<Category> getCategoriesFromJsonString(String catergoriesGetResult) throws JSONException {
        ArrayList<Category> categoriesResult = new ArrayList<>();
        JSONArray categoriesArray = new JSONArray(catergoriesGetResult);
        for(int i = 0; i<categoriesArray.length() ; i++){
            JSONObject cateJson = categoriesArray.getJSONObject(i);
            String name = cateJson.getString("name");
            int id = Integer.parseInt(cateJson.getString("categoryId"));
            Category category = new Category();
            category.setId(id);
            category.setName(name);
            categoriesResult.add(category);
        }
        return categoriesResult;
    }

    public int getCategoryIdFromCategoriesCollection(String categoryName, ArrayList<Category> categories){
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getName() == categoryName) {
                return categories.get(i).getId();
            }
        }
        throw new Resources.NotFoundException();
    }
}
