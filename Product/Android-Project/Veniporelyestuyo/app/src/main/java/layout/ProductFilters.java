package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.product.whitewalkers.veniporelyestuyo.R;

import java.util.ArrayList;

import Domain.Product;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProductFilters.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProductFilters#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductFilters extends Fragment {

    private IProductFilter mListener;

    public ProductFilters() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
    */
    // TODO: Rename and change types and number of parameters
    public static ProductFilters newInstance() {
        ProductFilters fragment = new ProductFilters();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IProductFilter) {
            mListener = (IProductFilter) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface IProductFilter {
        // TODO: Update argument type and name
        void updateProductList(ArrayList<Product> productList);
    }
}
