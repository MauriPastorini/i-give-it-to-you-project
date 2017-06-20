package layout;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.product.whitewalkers.veniporelyestuyo.R;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import ApiCommunicationManager.AccountApiCommunication;
import ApiCommunicationManager.ProductApiCommunication;
import Domain.Account;
import Domain.Product;
import Domain.ResponseAsyncTask;
import Domain.ResponseHttp;
import MyStaticElements.LogRegistration;

public class ProfileFragment extends Fragment {
    ArrayList<Product> productsSolicitated = new ArrayList<>();
    View view;
    Account account;
    IProfileFragment iProfileFragment;

    public interface IProfileFragment{
        void openLoginActivity();
        void openProductInfoActivity(int productId);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            iProfileFragment = (IProfileFragment) context;
        }catch (ClassCastException ex){
            throw ex;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        account = new AccountApiCommunication().getAccountInformation(getActivity());
        loadAccountInformation();
        loadButtonListener();
        new ProductTask(getActivity()).execute();
        return view;
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
                response = new ProductApiCommunication(mContext).getProductsSolicitatedByClient(mContext);
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
                    productsSolicitated = (ArrayList<Product>) responseHttp.getMessageObject();
                    loadProductListView();
                } else if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.CLIENT_ERROR){
                    Toast.makeText(mContext,"Error en solicitud: " + responseHttp.getMessage(),Toast.LENGTH_LONG).show();
                    LogRegistration.log(LogRegistration.TypeLog.ERROR, responseHttp.getMessage());
                }
                return;
            }
        }
    }

    private void loadButtonListener() {
        Button btnSignOut = (Button)view.findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                new AccountApiCommunication().signOut(getActivity());
                iProfileFragment.openLoginActivity();
            }
        });
    }

    private void loadAccountInformation() {
        TextView nameView = (TextView) view.findViewById(R.id.txtName);
        TextView emailView = (TextView) view.findViewById(R.id.txtEmail);
        nameView.setText(account.getUserName());
        emailView.setText(account.getEmail());
    }

    //List products
    private void loadProductListView(){
        ArrayAdapter<Product> productsSolicitatedAdapter = new UnmoderatedProductsAdapter(getActivity());
        ListView unmoderatedProductsListView = (ListView) view.findViewById(R.id.lstProductsSolicitatedId);
        unmoderatedProductsListView.setAdapter(null);
        unmoderatedProductsListView.setAdapter(productsSolicitatedAdapter);
    }

    private class UnmoderatedProductsAdapter extends ArrayAdapter<Product> {

        Context context;

        public UnmoderatedProductsAdapter(Context context) {
            super(context, R.layout.products_solicitated, productsSolicitated);
            this.context = context;
        }

        @Override
        public View getView(final int currentProductIndex, View convertView, ViewGroup parent) {
            View listItemView = convertView;
            if (listItemView == null) {
                Activity activity = (Activity) context;
                listItemView = activity.getLayoutInflater().inflate(R.layout.products_solicitated, parent, false);
            }
            final Product currentProduct = productsSolicitated.get(currentProductIndex);

            TextView productName = (TextView) listItemView.findViewById(R.id.productSolicitatedName);
            productName.setText(currentProduct.name);
            Button rateBtn = (Button) listItemView.findViewById(R.id.btn_product_rate);
            Button viewInfoBtn = (Button) listItemView.findViewById(R.id.btn_product_info);

            rateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View lstView) {
                    //Rate Task
                }
            });

            viewInfoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View lstView) {
                    iProfileFragment.openProductInfoActivity(currentProduct.id);
                }
            });
            return listItemView;
        }
    }
}
