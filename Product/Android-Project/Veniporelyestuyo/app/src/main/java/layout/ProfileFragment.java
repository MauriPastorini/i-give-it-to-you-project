package layout;

import android.app.Activity;
import android.content.Context;
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

import com.product.whitewalkers.veniporelyestuyo.ModerateProductsActivity;
import com.product.whitewalkers.veniporelyestuyo.R;

import java.util.ArrayList;

import ApiCommunicationManager.AccountApiCommunication;
import Domain.Account;
import Domain.Product;

public class ProfileFragment extends Fragment {
    ArrayList<Product> productsSolicitated = new ArrayList<>();
    View view;
    Account account;
    IProfileFragment iprofileFragment;

    public interface IProfileFragment{
        void openLoginActivity();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            iprofileFragment = (IProfileFragment) context;
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
        loadHardcodedProducts();
        loadProductListView();
        return view;
    }

    private void loadHardcodedProducts(){
        Product product1 = new Product();
        Product product2 = new Product();
        product1.name = "Producto 1";
        product2.name = "Producto 2";
        productsSolicitated.add(product1);
        productsSolicitated.add(product2);
    }

    private void loadButtonListener() {
        Button btnSignOut = (Button)view.findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                new AccountApiCommunication().signOut(getActivity());
                iprofileFragment.openLoginActivity();
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
        ArrayAdapter<Product> unmoderatedProductsAdapter = new UnmoderatedProductsAdapter(getActivity());
        ListView unmoderatedProductsListView = (ListView) view.findViewById(R.id.lstProductsSolicitatedId);
        unmoderatedProductsListView.setAdapter(null);
        unmoderatedProductsListView.setAdapter(unmoderatedProductsAdapter);
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
            Product currentProduct = productsSolicitated.get(currentProductIndex);

            TextView productName = (TextView) listItemView.findViewById(R.id.productSolicitatedName);
            productName.setText(currentProduct.name);
            Button approveBtn = (Button) listItemView.findViewById(R.id.btn_product_rate);
            Button denyBtn = (Button) listItemView.findViewById(R.id.btn_product_info);

            approveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View lstView) {
                    System.out.println("HELLOOO Listener");

                    //Integer[] params = new Integer[1];
                    // params[0] = productsSolicitated.get(currentProductIndex).id;
                    //new AcceptProductTask(ModerateProductsActivity.this).execute(params);

                }
            });

            denyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View lstView) {
                    System.out.println("HELLOOO");
                    //Integer[] params = new Integer[1];
                    //params[0] = productsSolicitated.get(currentProductIndex).id;
                    //new DeleteProductTask(ModerateProductsActivity.this).execute(params);
                }
            });
            return listItemView;
        }
    }
}
