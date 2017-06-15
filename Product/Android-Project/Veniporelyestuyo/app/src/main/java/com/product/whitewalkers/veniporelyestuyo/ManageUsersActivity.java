package com.product.whitewalkers.veniporelyestuyo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class ManageUsersActivity extends AppCompatActivity {

    ListView listview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);
        listview = (ListView) findViewById(R.id.userManageList);
        //TODO: Get data from api to show users to manage
        listview.setAdapter(new UsersManageListAdapter(this, new String[] { "data1",
                "data2" }));
    }
}
