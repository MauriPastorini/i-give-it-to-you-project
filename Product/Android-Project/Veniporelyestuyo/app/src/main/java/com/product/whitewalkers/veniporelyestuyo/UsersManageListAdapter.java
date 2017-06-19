package com.product.whitewalkers.veniporelyestuyo;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import ApiCommunicationManager.AccountApiCommunication;
import Domain.Account;
import Domain.ResponseAsyncTask;
import Domain.ResponseHttp;
import MyStaticElements.LogRegistration;

/**
 * Created by faustosanchez on 14/6/17.
 */

public class UsersManageListAdapter extends BaseAdapter {

    Context context;
    List<Account> data;
    private static LayoutInflater inflater = null;
    Runnable sendInfo;

    public UsersManageListAdapter(Context context, List<Account> data) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.sendInfo = sendInfo;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.user_manage, null);
        TextView text = (TextView) vi.findViewById(R.id.userNameText);
        text.setText(data.get(position).toString());

        Button aproveBtn = (Button)vi.findViewById(R.id.aproveBtn);
        Button denyBtn = (Button)vi.findViewById(R.id.denyBtn);

        aproveBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String[] params = new String[5];
                params[0] = data.get(position).getId() + "";
                params[1] = data.get(position).getUserName();
                params[2] = data.get(position).getEmail();
                params[3] = data.get(position).getPassword();
                new SetAsAdminUserTask(context).execute(params);
                notifyDataSetChanged();
            }
        });
        denyBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String[] params = new String[5];
                params[0] = data.get(position).getId() + "";
                params[1] = data.get(position).getUserName();
                params[2] = data.get(position).getEmail();
                params[3] = data.get(position).getPassword();
                new DeleteUserTask(context).execute(params);
                notifyDataSetChanged();
            }
        });
        return vi;
    }

    private class SetAsAdminUserTask extends AsyncTask<String, Void, ResponseAsyncTask> {

        private Context mContext;

        public SetAsAdminUserTask (Context context){
            mContext = context;
        }

        @Override
        protected ResponseAsyncTask doInBackground(String... params) {
            int id = Integer.parseInt(params[0]);
            String username = params[1];
            String email = params[2];
            String password = params[3];
            Account account = new Account(id, username, email, password, false);
            ResponseHttp response;
            try{
                response = new AccountApiCommunication().setAsAdminAccount(account, mContext);
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

    private class DeleteUserTask extends AsyncTask<String, Void, ResponseAsyncTask> {

        private Context mContext;

        public DeleteUserTask (Context context){
            mContext = context;
        }

        @Override
        protected ResponseAsyncTask doInBackground(String... params) {
            int id = Integer.parseInt(params[0]);
            String username = params[1];
            String email = params[2];
            String password = params[3];
            Account account = new Account(id, username, email, password, false);
            ResponseHttp response;
            try{
                response = new AccountApiCommunication().deleteAccount(account, mContext);
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
