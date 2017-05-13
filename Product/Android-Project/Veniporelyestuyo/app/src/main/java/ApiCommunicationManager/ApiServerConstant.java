package ApiCommunicationManager;

/**
 * Created by Mauri on 07-May-17.
 */

public class ApiServerConstant {
    public static String address = "http://192.168.0.109";
    public static String port = "51339";
    public static String url = address + ":" + port + "/api/";

    public static String productPostUri = ApiServerConstant.url + "/Product";

    public static String categoryGetUri = ApiServerConstant.url + "Category";

    public static String productStateApiUri = ApiServerConstant.url + "ProductState";
}
