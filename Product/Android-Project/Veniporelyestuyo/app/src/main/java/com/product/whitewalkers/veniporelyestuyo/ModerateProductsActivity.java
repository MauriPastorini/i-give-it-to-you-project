package com.product.whitewalkers.veniporelyestuyo;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ForwardingListener;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ApiCommunicationManager.ConnectionHandler;
import ApiCommunicationManager.ProductApiCommunication;
import Domain.Product;
import Domain.ResponseAsyncTask;
import Domain.ResponseHttp;
import MyStaticElements.LogRegistration;

public class ModerateProductsActivity extends AppCompatActivity {

    ArrayList<Product> unmoderatedProductsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moderate_products);

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
        updateProductList();
    }

    private void updateProductList(){
        new ProductTask(this).execute();
    }

    private void checkConnection() {
        new ConnectionHandler().controlConnectionsAvaiable(this);
    }

    private class ProductTask extends AsyncTask<Void, Void, ResponseAsyncTask> {

        private Context mContext;
        public ProductTask (Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected ResponseAsyncTask doInBackground(Void... params) {
            ResponseHttp response;
            try{
                response = new ProductApiCommunication(mContext).getUnmoderatedProducts(mContext);
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
                    unmoderatedProductsList = (ArrayList<Product>) responseHttp.getMessageObject();
                    loadProductListView(unmoderatedProductsList);
                } else if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.CLIENT_ERROR){
                    Toast.makeText(mContext,"Error en solicitud: " + responseHttp.getMessage(),Toast.LENGTH_LONG).show();
                    LogRegistration.log(LogRegistration.TypeLog.ERROR, responseHttp.getMessage());
                }
                return;
            }
        }
    }

    private class AcceptProductTask extends AsyncTask<Integer, Void, ResponseAsyncTask> {

        private Context mContext;
        public AcceptProductTask (Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected ResponseAsyncTask doInBackground(Integer... params) {
            int productId = params[0];
            ResponseHttp response;
            try{
                response = new ProductApiCommunication(mContext).acceptProduct(productId, mContext);
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
                    updateProductList();
                } else if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.CLIENT_ERROR){
                    Toast.makeText(mContext,"Error en solicitud: " + responseHttp.getMessage(),Toast.LENGTH_LONG).show();
                    LogRegistration.log(LogRegistration.TypeLog.ERROR, responseHttp.getMessage());
                }
                return;
            }
        }
    }

    private class DeleteProductTask extends AsyncTask<Integer, Void, ResponseAsyncTask> {

        private Context mContext;
        public DeleteProductTask (Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected ResponseAsyncTask doInBackground(Integer... params) {
            int productId = params[0];
            ResponseHttp response;
            try{
                response = new ProductApiCommunication(mContext).deleteProduct(productId, mContext);
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
                    updateProductList();
                } else if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.CLIENT_ERROR){
                    Toast.makeText(mContext,"Error en solicitud: " + responseHttp.getMessage(),Toast.LENGTH_LONG).show();
                    LogRegistration.log(LogRegistration.TypeLog.ERROR, responseHttp.getMessage());
                }
                return;
            }
        }
    }

    private void loadProductListView(List<Product> unmoderatedProducts){
        ArrayAdapter<Product> unmoderatedProductsAdapter = new UnmoderatedProductsAdapter();
        ListView unmoderatedProductsListView = (ListView) findViewById(R.id.unmoderatedProductsListView);
        unmoderatedProductsListView.setAdapter(null);
        unmoderatedProductsListView.setAdapter(unmoderatedProductsAdapter);

    }

    private class UnmoderatedProductsAdapter extends ArrayAdapter<Product>{
        public UnmoderatedProductsAdapter(){
            super(ModerateProductsActivity.this,R.layout.moderate_products,unmoderatedProductsList);
        }
        @Override
        public View getView(final int currentProductIndex, View convertView, ViewGroup parent) {
            View listItemView  = convertView;
            if (listItemView == null){
                listItemView = getLayoutInflater().inflate(R.layout.moderate_products, parent,false);
            }
            Product currentProduct = unmoderatedProductsList.get(currentProductIndex);

            TextView productName = (TextView)listItemView.findViewById(R.id.item_productName);
            productName.setText(currentProduct.name);
            Button approveBtn = (Button)listItemView.findViewById(R.id.item_aproveBtn);
            Button denyBtn = (Button)listItemView.findViewById(R.id.irem_denyBtn);

            approveBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View lstView){
                    Integer[] params = new Integer[1];
                    params[0] = unmoderatedProductsList.get(currentProductIndex).id;
                    new AcceptProductTask(ModerateProductsActivity.this).execute(params);
                }
            });

            denyBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View lstView){
                    Integer[] params = new Integer[1];
                    params[0] = unmoderatedProductsList.get(currentProductIndex).id;
                    new DeleteProductTask(ModerateProductsActivity.this).execute(params);
                }
            });
            return listItemView;
        }

        @Override
        public boolean  areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }
    }
}
