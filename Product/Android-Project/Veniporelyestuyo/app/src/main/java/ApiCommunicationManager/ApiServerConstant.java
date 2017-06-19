package ApiCommunicationManager;

/**
 * Created by Mauri on 07-May-17.
 */

public class ApiServerConstant {
    //HARDCODED: Migrate this variables to runtime execution lecture for defer binding
    public static String ip = "192.168.2.116";
    public static String port = "51339";
    public static int timeOutConnectionApi = 9000;
    //

    private static String address = "http://" + ip;
    private static String serverUrl = address + ":" + port ;

    public static String apiUrl = address + ":" + port + "/api/";

    public static String productPostUri = ApiServerConstant.apiUrl + "Product";

    public static String categoryGetUri = ApiServerConstant.apiUrl + "Category";

    public static String accountPostUri = ApiServerConstant.apiUrl + "Account";

    public static String productStateApiUri = ApiServerConstant.apiUrl + "ProductState";

    public static String getUnmoderatedProductsUri = ApiServerConstant.apiUrl + "Product/Unmoderated";

    public static String acceptProductUri(int idProduct){
        return ApiServerConstant.apiUrl + "Product/" + idProduct + "/Accept";
    }

    public static String getProductsByCategory(int categoryId){
        return ApiServerConstant.apiUrl + "Product/Category/" + categoryId;
    }

    public static String deleteProductUri(int idProduct){
        return ApiServerConstant.apiUrl + "Product/" + idProduct;
    }

    public static String productPostPhotoUri(int idProduct){
        return ApiServerConstant.apiUrl + "Product/" + idProduct+"/Image";
    }
    public static String accountPostTokenUri = ApiServerConstant.serverUrl + "/token";

    public static String productGetUri(int productId) {
        return ApiServerConstant.apiUrl + "Product/" + productId;
    }

    public static String productPhotoGetUri(int productId) {
        return ApiServerConstant.apiUrl + "Product/" + productId + "/photo";
    }
}
