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
        List<Account> inactiveUsers = getInactiveUsers();
        listview.setAdapter(new UsersManageListAdapter(this, inactiveUsers));
    }

    private List<Account> getInactiveUsers() {
        ArrayList<Account> result = new ArrayList<Account>();
        result.add(new Account("test", "Testsdfsdf123", "Abcd21213", true));
        return result;
    }

}

