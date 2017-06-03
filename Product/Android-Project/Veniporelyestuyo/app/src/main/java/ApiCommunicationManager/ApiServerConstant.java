package ApiCommunicationManager;

/**
 * Created by Mauri on 07-May-17.
 */

public class ApiServerConstant {
    public static String address = "http://192.168.0.114";
    public static String port = "51339";
    public static String serverUrl = address + ":" + port ;

    public static String apiUrl = address + ":" + port + "/api/";

    public static String productPostUri = ApiServerConstant.apiUrl + "/Product";

    public static String categoryGetUri = ApiServerConstant.apiUrl + "Category";

    public static String userPostUri = ApiServerConstant.apiUrl + "/Account";

    public static String productStateApiUri = ApiServerConstant.apiUrl + "ProductState";
    public static String productPostPhotoUri(int idProduct){
        return ApiServerConstant.apiUrl + "Product/" + idProduct;
    }
}
