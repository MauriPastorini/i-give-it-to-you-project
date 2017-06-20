package com.product.whitewalkers.veniporelyestuyo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AdminMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);
    }

    public void adminUsers(View v){
        changeActivity(ManageUsersActivity.class);
    }

    public void adminPosts(View v){
        changeActivity(ModerateProductsActivity.class);
    }

    public void continueApp(View v){
        changeActivity(MainMenuUserActivity.class);
    }

    private void changeActivity(Class<?> context){
        Intent intent = new Intent(this, context);
        startActivity(intent);
    }
}
