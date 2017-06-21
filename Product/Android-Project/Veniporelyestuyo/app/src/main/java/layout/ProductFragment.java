package layout;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.product.whitewalkers.veniporelyestuyo.R;

import org.json.JSONException;

import java.io.IOException;

import ApiCommunicationManager.ProductApiCommunication;
import Domain.Product;
import Domain.ResponseAsyncTask;
import Domain.ResponseHttp;
import MyStaticElements.LogRegistration;


public class ProductFragment extends Fragment {

    public enum TypeInfo{
        VIEW_SOLICITUDE, MAKE_SOLICITUDE
    }

    IProductFragment iProductFragment;
    TypeInfo typeProductInfo;
    private int productId;

    public interface IProductFragment {
        void loadingVisible(boolean visible);
        void returnToPreviousActivityOrFragment();
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
        if (container != null) {
            container.removeAllViews();
        }
        changeVisibility(View.INVISIBLE, view);
        String typeProductInfo = getArguments().getString("typeProductInfo");
        this.typeProductInfo = TypeInfo.valueOf(typeProductInfo);
        loadButtonListener();
        productId = Integer.parseInt(getArguments().getString("productId"));
        setProductData(productId);
        return view;
    }

    public void setProductInfo(){
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
                response = new ProductApiCommunication(mContext).getProductAndImages(productId);
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
            view.findViewById(R.id.btnWantIt).setEnabled(true);
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

    private void loadButtonListener() {
        Button btnSignOut = (Button)view.findViewById(R.id.btnWantIt);
        if (typeProductInfo == TypeInfo.MAKE_SOLICITUDE){
            btnSignOut.setText("LO QUIERO!");
            btnSignOut.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    iProductFragment.loadingVisible(true);
                    view.findViewById(R.id.btnWantIt).setEnabled(false);
                    new SolicitudeTask(productId).execute();
                }
            });
        } else if(typeProductInfo == TypeInfo.VIEW_SOLICITUDE){
            btnSignOut.setText("YA NO LO QUIERO");
            btnSignOut.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    iProductFragment.loadingVisible(true);
                    view.findViewById(R.id.btnWantIt).setEnabled(false);
                    new SolicitudeTask(productId).execute();
                }
            });
        }
    }

    private class SolicitudeTask extends AsyncTask<Void, Void, ResponseAsyncTask> {

        private int productId;
        private Context mContext;

        public SolicitudeTask (int parmProductId){
            productId = parmProductId;
            mContext = getActivity();
        }

        @Override
        protected ResponseAsyncTask doInBackground(Void... params) {
            ResponseHttp response = null;
            try{
                if (typeProductInfo == TypeInfo.MAKE_SOLICITUDE){
                    response = new ProductApiCommunication(mContext).registerSolicitude(productId, mContext);
                }
                else if(typeProductInfo == TypeInfo.VIEW_SOLICITUDE){
                    response = new ProductApiCommunication(mContext).cancelSolicitude(productId);
                }
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
            iProductFragment.loadingVisible(false);
            view.findViewById(R.id.btnWantIt).setEnabled(true);
            if (result.getTypeResponse() == ResponseAsyncTask.TypeResponse.EXCEPTION){
                Toast.makeText(mContext,"Error en realizar la solicitud: " + result.getDataResponse().toString(),Toast.LENGTH_LONG).show();
                LogRegistration.log(LogRegistration.TypeLog.EXCEPTION,result.getDataResponse().toString());
                return;
            }
            else{
                ResponseHttp responseHttp = (ResponseHttp) result.getDataResponse();
                if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.SUCCESS){
                    Toast.makeText(mContext,"Producto eliminado de tus solicitudes",Toast.LENGTH_LONG).show();
                    changeVisibility(View.VISIBLE, getView());
                    iProductFragment.returnToPreviousActivityOrFragment();
                } else if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.CLIENT_ERROR){
                    Toast.makeText(mContext,"Error en solicitud: " + responseHttp.getMessage(),Toast.LENGTH_LONG).show();
                    LogRegistration.log(LogRegistration.TypeLog.ERROR, responseHttp.getMessage());
                }
                return;
            }
        }
    }
}
