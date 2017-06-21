package com.product.whitewalkers.veniporelyestuyo;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import java.io.IOException;
import ApiCommunicationManager.AccountApiCommunication;
import ApiCommunicationManager.ConnectionHandler;
import Domain.Account;
import Domain.ResponseAsyncTask;
import Domain.ResponseHttp;

import static android.R.attr.data;
import static com.product.whitewalkers.veniporelyestuyo.R.id.textView;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "myLogMessageTag";

    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
    }

    private void checkConnection() {
        new ConnectionHandler().controlConnectionsAvaiable(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
        TextView txtAdmin = (TextView)findViewById(R.id.txtEditUsername);
        TextView txtPassAdmin = (TextView)findViewById(R.id.txtEditContraseña);
        txtAdmin.setText("superUser");
        txtPassAdmin.setText("Super123");
    }

    public void login(View view){
        EditText txtEditUsername = (EditText)findViewById(R.id.txtEditUsername);
        EditText txtEditContraseña = (EditText)findViewById(R.id.txtEditContraseña);
        String username = txtEditUsername.getText().toString();
        String pass = txtEditContraseña.getText().toString();
        if(username.equals("")||pass.equals("")){
            Toast.makeText(this,"Usuario ni contraseña pueden ser vacios." ,Toast.LENGTH_LONG).show();
        } else{
            String[] data = new String[2];
            data[0] = username;
            data[1] = pass;
            new LoginTask(this).execute(data);
        }
    }

    private class LoginTask extends AsyncTask<String, Void, ResponseAsyncTask> {

        private Context mContext;
        private Account account;

        public LoginTask (Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        }

        @Override
        protected ResponseAsyncTask doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            ResponseHttp response;
            try{
                account = new Account(0,username,"",password);
                response = new AccountApiCommunication().postToken(account, mContext);
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
            findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
            if (result.getTypeResponse() == ResponseAsyncTask.TypeResponse.EXCEPTION){
                Toast.makeText(mContext,"Error en login: " + result.getDataResponse().toString(),Toast.LENGTH_LONG).show();
                Log.i(TAG, result.getDataResponse().toString());
                return;
            }
            else{
                ResponseHttp responseHttp = (ResponseHttp) result.getDataResponse();
                if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.SUCCESS){
                    Intent myIntent;
                    if(account.isAdmin()){
                        myIntent = new Intent(LoginActivity.this, AdminMenuActivity.class);
                    } else{
                        myIntent = new Intent(LoginActivity.this, MainMenuUserActivity.class);
                    }
                    LoginActivity.this.startActivity(myIntent);
                } else if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.CLIENT_ERROR){
                    Toast.makeText(mContext,"Usuario o contraseña incorrecta ",Toast.LENGTH_LONG).show();
                    Log.i(TAG, responseHttp.getMessage().toString());
                }
                return;
            }
        }
    }

    public void register(View view){
        startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
    }
}
