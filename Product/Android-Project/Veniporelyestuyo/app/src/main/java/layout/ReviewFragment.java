package layout;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.product.whitewalkers.veniporelyestuyo.R;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import ApiCommunicationManager.ProductApiCommunication;
import ApiCommunicationManager.ReviewProductApiCommunication;
import Domain.Product;
import Domain.ResponseAsyncTask;
import Domain.ResponseHttp;
import MyStaticElements.LogRegistration;

public class ReviewFragment extends Fragment {
    private int productId = 0;
    private View view;

    private IReviewFragment mListener;

    public static ReviewFragment newInstance(int idProduct) {
        ReviewFragment fragment = new ReviewFragment();
        Bundle args = new Bundle();
        args.putInt("productId", idProduct);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IReviewFragment) {
            mListener = (IReviewFragment) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IReviewFragment");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.changeOpacityFragment(true);
        mListener = null;
        view.setVisibility(View.INVISIBLE);
    }
    public interface IReviewFragment {
        void changeOpacityFragment(boolean normal);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productId = getArguments().getInt("productId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_review, container, false);
        mListener.changeOpacityFragment(false);
        view.setVisibility(View.VISIBLE);
        loadbtnListeners();
        return view;
    }

    private void loadbtnListeners(){
        Button btnRate1 = (Button) view.findViewById(R.id.btn_rate_1);
        Button btnRate2 = (Button) view.findViewById(R.id.btn_rate_2);
        Button btnRate3 = (Button) view.findViewById(R.id.btn_rate_3);
        Button btnRate4 = (Button) view.findViewById(R.id.btn_rate_4);
        Button btnRate5 = (Button) view.findViewById(R.id.btn_rate_5);

        btnRate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View lstView) {
                new ReviewTask(getContext(), 1, productId).execute();
            }
        });
        btnRate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View lstView) {
                new ReviewTask(getContext(), 2, productId).execute();
            }
        });
        btnRate3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View lstView) {
                new ReviewTask(getContext(), 3, productId).execute();
            }
        });
        btnRate4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View lstView) {
                new ReviewTask(getContext(), 4, productId).execute();
            }
        });
        btnRate5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View lstView) {
                new ReviewTask(getContext(), 5, productId).execute();
            }
        });
    }

    private class ReviewTask extends AsyncTask<Void, Void, ResponseAsyncTask> {

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
                Toast.makeText(mContext,"Error en realizar la calificacion, intenta denuevo!",Toast.LENGTH_LONG).show();
                LogRegistration.log(LogRegistration.TypeLog.EXCEPTION,result.getDataResponse().toString());
                return;
            }
            else{
                ResponseHttp responseHttp = (ResponseHttp) result.getDataResponse();
                if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.SUCCESS){
                    Toast.makeText(mContext,"Su calificación fue realizada",Toast.LENGTH_LONG).show();
                } else if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.CLIENT_ERROR){
                    Toast.makeText(mContext,"Error en solicitud, intenta denuevo!",Toast.LENGTH_LONG).show();
                    LogRegistration.log(LogRegistration.TypeLog.ERROR, responseHttp.getMessage());
                }
                return;
            }
        }
    }


}
