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
import android.widget.TextView;
import android.widget.Toast;
import com.product.whitewalkers.veniporelyestuyo.R;
import org.json.JSONException;
import java.io.IOException;
import ApiCommunicationManager.AccountApiCommunication;
import Domain.Account;
import Domain.Product;
import Domain.ResponseAsyncTask;
import Domain.ResponseHttp;
import MyStaticElements.LogRegistration;

public class ProfileFragment extends Fragment {
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
        return view;
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
}
