package com.product.whitewalkers.veniporelyestuyo;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import ApiCommunicationManager.AccountApiCommunication;
import Domain.Account;
import Domain.ResponseAsyncTask;
import Domain.ResponseHttp;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameTxt;
    private EditText emailTxt;
    private EditText passTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameTxt = (EditText)findViewById(R.id.username);
        emailTxt = (EditText)findViewById(R.id.email);
        passTxt = (EditText)findViewById(R.id.password);
    }

    public void registerClick(View v) {
        String username = usernameTxt.getText().toString();
        String email = emailTxt.getText().toString();
        String password = passTxt.getText().toString();
        String[] data = new String[3];
        data[0] = username;
        data[1] = email;
        data[2] = password;
        new RegisterTask(this).execute(data);
    }

    private class RegisterTask extends AsyncTask<String, Void, ResponseAsyncTask> {

        private Context mContext;

        public RegisterTask (Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected ResponseAsyncTask doInBackground(String... params) {
            String username = params[0];
            String email = params[1];
            String password = params[2];
            ResponseHttp response;
            try{
                response = new AccountApiCommunication().postAccount(new Account(0, username, email, password));
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
                Toast.makeText(mContext,"Error en registro, lo sentimos, intenta denuevo!",Toast.LENGTH_LONG).show();
                return;
            }
            else{
                ResponseHttp responseHttp = (ResponseHttp) result.getDataResponse();
                if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.SUCCESS){
                    Toast.makeText(mContext,"Registro OK",Toast.LENGTH_LONG).show();
                    Intent i = new Intent(mContext, LoginActivity.class);
                    startActivity(i);
                } else if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.CLIENT_ERROR){
                    Toast.makeText(mContext,responseHttp.getMessage(),Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

}


