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
import ApiCommunicationManager.ReviewProductApiCommunication;
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
    private int productId;

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
                Toast.makeText(mContext,"Error en cargar los productos, intenta denuevo!",Toast.LENGTH_LONG).show();
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
                    Toast.makeText(mContext,"Error en solicitud, intenta denuevo!",Toast.LENGTH_LONG).show();
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
        nameView.setText(account.getUserName());
    }

    //List products
    private void loadProductListView(){
        ArrayAdapter<Product> productsSolicitatedAdapter = new ProductsAdapter(getActivity());
        ListView unmoderatedProductsListView = (ListView) view.findViewById(R.id.lstProductsSolicitatedId);
        unmoderatedProductsListView.setAdapter(null);
        unmoderatedProductsListView.setAdapter(productsSolicitatedAdapter);
    }

    private class ProductsAdapter extends ArrayAdapter<Product> {

        Context context;

        public ProductsAdapter(Context context) {
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
            final Button rateBtn = (Button) listItemView.findViewById(R.id.btn_product_rate);
            final Button viewInfoBtn = (Button) listItemView.findViewById(R.id.btn_product_info);
            final Button btnRate1 = (Button) listItemView.findViewById(R.id.btn_rate_1);
            final Button btnRate2 = (Button) listItemView.findViewById(R.id.btn_rate_2);
            final Button btnRate3 = (Button) listItemView.findViewById(R.id.btn_rate_3);
            final Button btnRate4 = (Button) listItemView.findViewById(R.id.btn_rate_4);
            final Button btnRate5 = (Button) listItemView.findViewById(R.id.btn_rate_5);

            changeAllBtnsEnabled(btnRate1, btnRate2, btnRate3, btnRate4, btnRate5, false);
            changeOpacitiyButtons(btnRate1, btnRate2, btnRate3, btnRate4, btnRate5,0.3f);


            rateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View lstView) {
                    productId = currentProduct.id;
                    changeAllBtnsEnabled(btnRate1, btnRate2, btnRate3, btnRate4, btnRate5,true);
                    changeOpacitiyButtons(btnRate1, btnRate2, btnRate3, btnRate4, btnRate5,1f);
                    rateBtn.setEnabled(false);
                    rateBtn.setAlpha(0.3f);
                }
            });

            viewInfoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View lstView) {
                    iProfileFragment.openProductInfoActivity(currentProduct.id);
                }
            });
            btnRate1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View lstView) {
                    new ReviewTask(getContext(), 1, productId).execute();
                    changeAllBtnsEnabled(btnRate1, btnRate2, btnRate3, btnRate4, btnRate5,false);
                    viewInfoBtn.setEnabled(false);
                    viewInfoBtn.setAlpha(0.3f);
                    btnRate2.setAlpha(0.3f);
                    btnRate3.setAlpha(0.3f);
                    btnRate4.setAlpha(0.3f);
                    btnRate5.setAlpha(0.3f);

                }
            });
            btnRate2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View lstView) {
                    new ReviewTask(getContext(), 2, productId).execute();
                    changeAllBtnsEnabled(btnRate1, btnRate2, btnRate3, btnRate4, btnRate5,false);
                    viewInfoBtn.setEnabled(false);
                    viewInfoBtn.setAlpha(0.3f);
                    btnRate3.setAlpha(0.3f);
                    btnRate4.setAlpha(0.3f);
                    btnRate5.setAlpha(0.3f);
                }
            });
            btnRate3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View lstView) {
                    new ReviewTask(getContext(), 3, productId).execute();
                    changeAllBtnsEnabled(btnRate1, btnRate2, btnRate3, btnRate4, btnRate5,false);
                    viewInfoBtn.setEnabled(false);
                    viewInfoBtn.setAlpha(0.3f);
                    btnRate4.setAlpha(0.3f);
                    btnRate5.setAlpha(0.3f);
                }
            });
            btnRate4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View lstView) {
                    new ReviewTask(getContext(), 4, productId).execute();
                    changeAllBtnsEnabled(btnRate1, btnRate2, btnRate3, btnRate4, btnRate5,false);
                    viewInfoBtn.setEnabled(false);
                    viewInfoBtn.setAlpha(0.3f);
                    btnRate5.setAlpha(0.3f);
                }
            });
            btnRate5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View lstView) {
                    new ReviewTask(getContext(), 5, productId).execute();
                    changeAllBtnsEnabled(btnRate1, btnRate2, btnRate3, btnRate4, btnRate5,false);
                }
            });
            return listItemView;
        }

        void changeAllBtnsEnabled(Button btnRate1, Button btnRate2, Button btnRate3, Button btnRate4, Button btnRate5, boolean enable) {
            btnRate1.setEnabled(enable);
            btnRate2.setEnabled(enable);
            btnRate3.setEnabled(enable);
            btnRate4.setEnabled(enable);
            btnRate5.setEnabled(enable);

        }
        void changeOpacitiyButtons(Button btnRate1, Button btnRate2, Button btnRate3, Button btnRate4, Button btnRate5, float opac) {
            btnRate1.setAlpha(opac);
            btnRate2.setAlpha(opac);
            btnRate3.setAlpha(opac);
            btnRate4.setAlpha(opac);
            btnRate5.setAlpha(opac);
        }
    }

    public class ReviewTask extends AsyncTask<Void, Void, ResponseAsyncTask> {

        private Context mContext;
        private int productId;
        private int rate;

        public ReviewTask (Context context,int rate, int productId)
        {
            mContext = context;
            this.rate = rate;
            this.productId = productId;
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected ResponseAsyncTask doInBackground(Void... params) {
            ResponseHttp response;
            try{
                response = new ReviewProductApiCommunication(mContext).registerReview(productId,rate);
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
                Toast.makeText(mContext,"Error en realizar la calificacion: " + result.getDataResponse().toString(),Toast.LENGTH_LONG).show();
                LogRegistration.log(LogRegistration.TypeLog.EXCEPTION,result.getDataResponse().toString());
                return;
            }
            else{
                ResponseHttp responseHttp = (ResponseHttp) result.getDataResponse();
                if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.SUCCESS){
                    Toast.makeText(mContext,"Su calificación fue realizada",Toast.LENGTH_LONG).show();
                } else if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.CLIENT_ERROR){
                    Toast.makeText(mContext,"Error en solicitud: " + responseHttp.getMessage(),Toast.LENGTH_LONG).show();
                    LogRegistration.log(LogRegistration.TypeLog.ERROR, responseHttp.getMessage());
                }
                return;
            }
        }
    }

}
