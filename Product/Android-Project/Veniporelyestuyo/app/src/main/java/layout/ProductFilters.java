package layout;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.product.whitewalkers.veniporelyestuyo.R;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import ApiCommunicationManager.CategoryApiCommunication;
import ApiCommunicationManager.ConnectionHandler;
import ApiCommunicationManager.ProductApiCommunication;
import Domain.Category;
import Domain.Product;
import Domain.ResponseAsyncTask;
import Domain.ResponseHttp;
import MyStaticElements.LogRegistration;

public class ProductFilters extends Fragment {

    private IProductFilter iProductFilter;
    ArrayList<Product> actualProducts;
    private ArrayList<Category> categories;
    Spinner categorySpinner;
    Spinner countrySpinner;


    public interface IProductFilter {
        // TODO: Update argument type and name
        void updateProductList(ArrayList<Product> productList);
    }

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_product_filters, container, false);
        if (container != null) {
            container.removeAllViews();
        }
        new CategoriesTask().execute();
        loadProductsListByCategory(0);
        categorySpinner = (Spinner) view.findViewById(R.id.filter_categorySpinner);
        countrySpinner = (Spinner) view.findViewById(R.id.filter_countrySpinner);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if(parent!=null && categories!=null) {
                        int categoryId = new CategoryApiCommunication().getCategoryIdFromCategoriesCollection(parent.getItemAtPosition(position).toString(), categories);
                        loadProductsListByCategory(categoryId);
                    }else{
                        loadProductsListByCategory(0);
                    }

                }catch (Resources.NotFoundException ex){
                    loadProductsListByCategory(0);
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                loadProductsListByCategory(0);
            }
        });

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadProductsListByCountry(parent.getItemAtPosition(position).toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                loadProductsListByCategory(0);
            }
        });
        checkConnection();
        return view;
    }

    private void checkConnection() {
        new ConnectionHandler().controlConnectionsAvaiable(getActivity());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            iProductFilter = (IProductFilter) context;
        }catch (ClassCastException ex){
            throw ex;
        }
    }

    private void loadProductsListByCategory(int categoryId){
        new ProductByCategoryTask(categoryId).execute();
    }

    private void loadProductsListByCountry(String country){
        new ProductByCountryTask(country).execute();
    }

    private void updateActivityList(){
        iProductFilter.updateProductList(actualProducts);
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
                response = new ProductApiCommunication(getActivity()).getProductsByCategory(categoryId, mContext);
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
                Toast.makeText(mContext,"Error en cargar los productos, intenta denuevo!",Toast.LENGTH_LONG).show();
                LogRegistration.log(LogRegistration.TypeLog.EXCEPTION,result.getDataResponse().toString());
                return;
            }
            else{
                ResponseHttp responseHttp = (ResponseHttp) result.getDataResponse();
                if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.SUCCESS){
                  //  Toast.makeText(mContext,"OK",Toast.LENGTH_LONG).show();
                    actualProducts = (ArrayList<Product>) responseHttp.getMessageObject();
                    updateActivityList();
                } else if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.CLIENT_ERROR){
                    Toast.makeText(mContext,"Error en solicitud, intenta denuevo!",Toast.LENGTH_LONG).show();
                    LogRegistration.log(LogRegistration.TypeLog.ERROR, responseHttp.getMessage());
                }
                return;
            }
        }
    }

    private class ProductByCountryTask extends AsyncTask<Void, Void, ResponseAsyncTask> {

        private Context mContext;
        private String country;

        public ProductByCountryTask (String country){
            this.country = country;
            mContext = getActivity();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected ResponseAsyncTask doInBackground(Void... params) {

            ResponseHttp response;
            try{
                response = new ProductApiCommunication(getActivity()).getProductsByCountry(country, mContext);
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
                Toast.makeText(mContext,"Error en cargar los productos, intenta denuevo!",Toast.LENGTH_LONG).show();
                LogRegistration.log(LogRegistration.TypeLog.EXCEPTION,result.getDataResponse().toString());
                return;
            }
            else{
                ResponseHttp responseHttp = (ResponseHttp) result.getDataResponse();
                if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.SUCCESS){
                    //  Toast.makeText(mContext,"OK",Toast.LENGTH_LONG).show();
                    actualProducts = (ArrayList<Product>) responseHttp.getMessageObject();
                    updateActivityList();
                } else if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.CLIENT_ERROR){
                    Toast.makeText(mContext,"Error en solicitud, intenta denuevo!",Toast.LENGTH_LONG).show();
                    LogRegistration.log(LogRegistration.TypeLog.ERROR, responseHttp.getMessage());
                }
                return;
            }
        }
    }

    private class CategoriesTask extends AsyncTask<Void, Void, ResponseAsyncTask> {

        private Context mContext;


        public CategoriesTask(){
            mContext = getActivity();
        }

        @Override
        protected ResponseAsyncTask doInBackground(Void... params) {
            ResponseHttp response;
            try{
                response = new CategoryApiCommunication().getCategories(mContext);
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
                Toast.makeText(mContext,"Error al cargar categorías, intenta denuevo!",Toast.LENGTH_LONG).show();
                LogRegistration.log(LogRegistration.TypeLog.EXCEPTION,result.getDataResponse().toString());
                return;
            }
            else{
                ResponseHttp responseHttp = (ResponseHttp) result.getDataResponse();
                if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.SUCCESS){
                 //   Toast.makeText(mContext,"Categorías obtenidas",Toast.LENGTH_LONG).show();
                    categories = (ArrayList<Category>) responseHttp.getMessageObject();
                    loadCategories();
                } else if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.CLIENT_ERROR){
                    Toast.makeText(mContext,"Error en solicitud, intenta denuevo!",Toast.LENGTH_LONG).show();
                    LogRegistration.log(LogRegistration.TypeLog.ERROR, responseHttp.getMessage());
                }
                return;
            }
        }
    }
    private void loadCategories() {
        ArrayList<String> categoriesOptions = new ArrayList<String>();
        categoriesOptions.add("Todas");
        for (int i=0;i<categories.size();i++){
            Category cate = categories.get(i);
            categoriesOptions.add(cate.getName());
        }
        this.loadCategorySpinner(categoriesOptions);
    }

    private void loadCategorySpinner(ArrayList<String> categoriesOptions) {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categoriesOptions); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fillSpinner(categorySpinner,spinnerArrayAdapter);
    }

    private void fillSpinner(final Spinner spinnerStates, final ArrayAdapter<String> spinnerArrayAdapter) {
        spinnerStates.setAdapter(spinnerArrayAdapter);
    }
}



