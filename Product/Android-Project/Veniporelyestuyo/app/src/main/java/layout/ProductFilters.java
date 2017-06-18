package layout;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.product.whitewalkers.veniporelyestuyo.R;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import ApiCommunicationManager.ProductApiCommunication;
import Domain.Product;
import Domain.ResponseAsyncTask;
import Domain.ResponseHttp;
import MyStaticElements.LogRegistration;

public class ProductFilters extends Fragment {

    private IProductFilter iProductFilter;
    ArrayList<Product> productsByCategory;

    public interface IProductFilter {
        // TODO: Update argument type and name
        void updateProductList(ArrayList<Product> productList);
    }
    public ProductFilters() {
        // Required empty public constructor
    }

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_product_filters, container, false);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            iProductFilter = (ProductFilters.IProductFilter) context;
        }catch (ClassCastException ex){
            throw ex;
        }
    }

    private void loadProductsList(int categoryId){
        Integer[] params = new Integer[1];
        params[0] =categoryId;

        new ProductByCategoryTask(categoryId).execute();
    }

    private void updateActivityList(){
        iProductFilter.updateProductList(productsByCategory);
    }

    private class ProductByCategoryTask extends AsyncTask<Void, Void, ResponseAsyncTask> {

        private Context mContext;
        private int categoryId;

        public ProductByCategoryTask (int categoryIdIN){
            categoryId = categoryIdIN;
            mContext = getActivity();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected ResponseAsyncTask doInBackground(Void... params) {
             
            ResponseHttp response;
            try{
                response = new ProductApiCommunication().getProductsByCategory(categoryId);
            } catch (IOException ioEx){
                return new ResponseAsyncTask<Exception>(ResponseAsyncTask.TypeResponse.EXCEPTION,ioEx);
            }
            catch (JSONException jsonEx){
                return new ResponseAsyncTask<Exception>(ResponseAsyncTask.TypeResponse.EXCEPTION,jsonEx);
            }
            return new ResponseAsyncTask<ResponseHttp>(ResponseAsyncTask.TypeResponse.OK,response);
        }

        @Override
        protected void onPostExecute(ResponseAsyncTask result) {
            if (result.getTypeResponse() == ResponseAsyncTask.TypeResponse.EXCEPTION){
                Toast.makeText(mContext,"Error en cargar los productos: " + result.getDataResponse().toString(),Toast.LENGTH_LONG).show();
                LogRegistration.log(LogRegistration.TypeLog.EXCEPTION,result.getDataResponse().toString());
                return;
            }
            else{
                ResponseHttp responseHttp = (ResponseHttp) result.getDataResponse();
                if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.SUCCESS){
                    Toast.makeText(mContext,"OK",Toast.LENGTH_LONG).show();
                    productsByCategory = (ArrayList<Product>) responseHttp.getMessageObject();
                    updateActivityList();
                } else if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.CLIENT_ERROR){
                    Toast.makeText(mContext,"Error en solicitud: " + responseHttp.getMessage(),Toast.LENGTH_LONG).show();
                    LogRegistration.log(LogRegistration.TypeLog.ERROR, responseHttp.getMessage());
                }
                return;
            }
        }
    }
}



