package com.product.whitewalkers.veniporelyestuyo;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ApiCommunicationManager.AccountApiCommunication;
import Domain.Account;
import Domain.ResponseAsyncTask;
import Domain.ResponseHttp;
import MyStaticElements.LogRegistration;

public class ManageUsersActivity extends AppCompatActivity {

    ListView listview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);
        listview = (ListView) findViewById(R.id.userManageList);
        getInactiveUsers();
    }

    private void getInactiveUsers() {
        new UnmoderatedUsersTask(this).execute();
    }

    private class UnmoderatedUsersTask extends AsyncTask<String, Void, ResponseAsyncTask> {

        private Context mContext;

        public UnmoderatedUsersTask (Context context){
            mContext = context;
        }

        @Override
        protected ResponseAsyncTask doInBackground(String... params) {
            ResponseHttp response;
            try{
                response = new AccountApiCommunication().getUnmoderatedUsers(mContext);
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
                Toast.makeText(mContext,"Error!",Toast.LENGTH_LONG).show();
                LogRegistration.log(LogRegistration.TypeLog.ERROR, "Error al aprovar o denegar usuario");
                return;
            }
            else{
                ResponseHttp responseHttp = (ResponseHttp) result.getDataResponse();
                List<Account> inactiveUsers = (List<Account>)responseHttp.getMessageObject();
                if (inactiveUsers != null) {
                    listview.setAdapter(new UsersManageListAdapter(mContext, inactiveUsers));
                }
                if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.SUCCESS){
                    Toast.makeText(mContext,"OK",Toast.LENGTH_LONG).show();
                } else if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.CLIENT_ERROR){
                    Toast.makeText(mContext,"Error!",Toast.LENGTH_LONG).show();
                    LogRegistration.log(LogRegistration.TypeLog.ERROR, "Error al aprovar o denegar usuario");
                }
                return;
            }
        }
    }

}

