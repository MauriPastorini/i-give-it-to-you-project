package layout;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.product.whitewalkers.veniporelyestuyo.ProductActivity;
import com.product.whitewalkers.veniporelyestuyo.R;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.logging.LogRecord;

import ApiCommunicationManager.AccountApiCommunication;
import ApiCommunicationManager.ProductApiCommunication;
import Domain.Account;
import Domain.Product;
import Domain.ResponseAsyncTask;
import Domain.ResponseHttp;
import MyStaticElements.LogRegistration;

import static android.R.attr.data;
import static android.content.ContentValues.TAG;


public class ProductFragment extends Fragment {

    IProductFragment iProductFragment;

    public interface IProductFragment {
        void loadingVisible(boolean visible);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            iProductFragment = (IProductFragment) context;
        }catch (ClassCastException ex){
            throw ex;
        }
    }

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_product, container, false);
        changeVisibility(View.INVISIBLE, view);
        ProductActivity context = (ProductActivity)getActivity();
        return view;
    }



    //Call from activity
    public void setProductInfo(int productId){
        setProductData(productId);
    }

    private void changeVisibility(int visibility, View parmView) {
        try {
            ViewGroup rootView = (ViewGroup) parmView;
            int childViewCount = rootView.getChildCount();
            for (int i=0; i<childViewCount;i++){
                View view = rootView.getChildAt(i);
                view.setVisibility(visibility);
            }
        } catch (ClassCastException e){
            LogRegistration.log(LogRegistration.TypeLog.EXCEPTION, e.toString());
        } catch (NullPointerException e){
            LogRegistration.log(LogRegistration.TypeLog.EXCEPTION, e.toString());
        }
    }

    private void setProductData(int productId) {
        new ProductTask(productId).execute();
    }

    private class ProductTask extends AsyncTask<Void, Void, ResponseAsyncTask> {

        private int productId;
        private Context mContext;

        public ProductTask (int parmProductId){
            productId = parmProductId;
            mContext = getActivity();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected ResponseAsyncTask doInBackground(Void... params) {
            ResponseHttp response;
            try{
                response = new ProductApiCommunication().getProductAndImages(productId);
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
                Toast.makeText(mContext,"Error en cargar producto: " + result.getDataResponse().toString(),Toast.LENGTH_LONG).show();
                LogRegistration.log(LogRegistration.TypeLog.EXCEPTION,result.getDataResponse().toString());
                return;
            }
            else{
                ResponseHttp responseHttp = (ResponseHttp) result.getDataResponse();
                if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.SUCCESS){
                    Toast.makeText(mContext,"OK",Toast.LENGTH_LONG).show();
                    changeVisibility(View.VISIBLE, getView());
                    iProductFragment.loadingVisible(false);
                    loadProductValues(view, (Product)responseHttp.getMessageObject());
                } else if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.CLIENT_ERROR){
                    Toast.makeText(mContext,"Error en solicitud: " + responseHttp.getMessage(),Toast.LENGTH_LONG).show();
                    LogRegistration.log(LogRegistration.TypeLog.ERROR, responseHttp.getMessage());
                }
                return;
            }
        }
    }

    private void loadProductValues(View view, Product product) {
        TextView txtViewName = (TextView)view.findViewById(R.id.txtViewName);
        txtViewName.setText(product.name);

        TextView txtViewDescription = (TextView)view.findViewById(R.id.txtViewDescription);
        txtViewDescription.setText(product.description);

        ImageView imgViewPhoto1 = (ImageView) view.findViewById(R.id.imgViewPhoto1);
        imgViewPhoto1.setImageBitmap(product.image1);
        ImageView imgViewPhoto2 = (ImageView) view.findViewById(R.id.imgViewPhoto2);
        imgViewPhoto2.setImageBitmap(product.image2);
        ImageView imgViewPhoto3 = (ImageView) view.findViewById(R.id.imgViewPhoto3);
        imgViewPhoto3.setImageBitmap(product.image1);
    }
}
