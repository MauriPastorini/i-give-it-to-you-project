package com.product.whitewalkers.veniporelyestuyo;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import ApiCommunicationManager.ConnectionHandler;
import layout.ProductFragment;

public class MainMenuUserActivity extends AppCompatActivity {

    private void checkConnection() {
        new ConnectionHandler().controlConnectionsAvaiable(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_user);
        loadInfoProductFragment();
    }

    private void loadInfoProductFragment(){
        PublishProductFragment bottomPictureFragment = (PublishProductFragment) getSupportFragmentManager().findFragmentById(R.id.productInfoFragment);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            return false;
        }
    };

}
