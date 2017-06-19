package com.product.whitewalkers.veniporelyestuyo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import android.view.Menu;

import android.view.MenuInflater;
import android.widget.Toast;

import ApiCommunicationManager.ConnectionHandler;
import layout.ProfileFragment;
import layout.PublishProductFragment;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class MainMenuUserActivity extends AppCompatActivity implements ProfileFragment.IProfileFragment{

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
        setNavigationView();
    }

    private void setNavigationView() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_product:
                                Fragment publishProductFragment = new PublishProductFragment();
                                FragmentManager managerPublish = getSupportFragmentManager();
                                FragmentTransaction transactionPublish = managerPublish.beginTransaction();
                                transactionPublish.replace(R.id.fragment, publishProductFragment);
                                transactionPublish.commit();
                                break;
                            case R.id.navigation_map:
                                //FRAGMENT MAPA
                                //Fragment publishProductFragment = new PublishProductFragment();
                                //FragmentManager managerPublish = getSupportFragmentManager();
                                //FragmentTransaction transactionPublish = managerPublish.beginTransaction();
                                //transactionPublish.replace(R.id.fragment, publishProductFragment);
                                //transactionPublish.commit();
                                break;
                            case R.id.navigation_profile:
                                Fragment fragment = new ProfileFragment();
                                FragmentManager managerProfile = getSupportFragmentManager();
                                FragmentTransaction transactionProfile = managerProfile.beginTransaction();
                                transactionProfile.replace(R.id.fragment, fragment);
                                transactionProfile.commit();
                                break;
                        }
                        return true;
                    }
                });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.navigation_product:
                Toast.makeText(this,"Debe tener al menos 3 fotos",Toast.LENGTH_LONG).show();
                System.out.println("NAVIGATION ENTROOO");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void openLoginActivity() {
        startActivity(new Intent(MainMenuUserActivity.this,LoginActivity.class));
    }
}
