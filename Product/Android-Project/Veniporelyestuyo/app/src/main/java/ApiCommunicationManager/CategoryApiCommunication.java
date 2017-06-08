package ApiCommunicationManager;

import android.content.res.Resources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import Domain.Category;


/**
 * Created by Mauri on 11-May-17.
 */

public class CategoryApiCommunication {
    private static final String TAG = "myLogMessageTag";

    public ArrayList<Category> getCategories() throws JSONException, IOException {
        ArrayList<Category> categoriesResult = new ArrayList<Category>();
        String catergoriesGetResult = new ConnectionHandler().getData(ApiServerConstant.categoryGetUri, ConnectionHandler.Content_Type.JSON).getMessage();
        return getCategoriesFromJsonString(categoriesResult, catergoriesGetResult);
    }

    ArrayList<Category> getCategoriesFromJsonString(ArrayList<Category> categoriesResult, String catergoriesGetResult) throws JSONException {
        JSONArray categoriesArray = new JSONArray(catergoriesGetResult);
        for(int i = 0; i<categoriesArray.length() ; i++){
            JSONObject cateJson = categoriesArray.getJSONObject(i);
            String name = cateJson.getString("Name");
            int id = Integer.parseInt(cateJson.getString("CategoryId"));
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
