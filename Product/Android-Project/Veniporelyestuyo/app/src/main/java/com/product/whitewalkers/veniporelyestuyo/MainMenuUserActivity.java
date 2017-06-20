package com.product.whitewalkers.veniporelyestuyo;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import android.view.Menu;

import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import ApiCommunicationManager.ConnectionHandler;
import layout.ProductFragment;
import layout.ProfileFragment;
import layout.PublishProductFragment;
import layout.ReviewFragment;

import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class MainMenuUserActivity extends AppCompatActivity implements ProfileFragment.IProfileFragment, ProductFragment.IProductFragment, ReviewFragment.IReviewFragment{

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
        findViewById(R.id.progressBarMainMenuUser).setVisibility(View.INVISIBLE);
        findViewById(R.id.reviewFragment).setVisibility(View.INVISIBLE);
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
    @Override
    public void openProductInfoActivity(int productId) {
        Bundle bundle = new Bundle();
        bundle.putString("typeProductInfo", ProductFragment.TypeInfo.VIEW_SOLICITUDE.toString());
        bundle.putString("productId", ""+ productId);

        Fragment fragment = new ProductFragment();
        fragment.setArguments(bundle);

        FragmentManager managerInfoProduct = getSupportFragmentManager();
        FragmentTransaction transaction = managerInfoProduct.beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
    }

    @Override
    public void loadingVisible(boolean visible) {
        if (visible) {
            findViewById(R.id.progressBarMainMenuUser).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.progressBarMainMenuUser).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void returnToPreviousActivityOrFragment() {
        Fragment fragment = new ProfileFragment();
        FragmentManager managerProfile = getSupportFragmentManager();
        FragmentTransaction transactionProfile = managerProfile.beginTransaction();
        transactionProfile.replace(R.id.fragment, fragment);
        transactionProfile.commit();
    }

    @Override
    public void changeOpacityFragment(boolean normal){
        if(normal){
            float intensity = 0f;
            findViewById(R.id.fragment).getRootView().setBackgroundColor(Color.TRANSPARENT);
            findViewById(R.id.fragment).setAlpha(intensity);
        }
        else {
            float intensity = 0.7f;
            //findViewById(R.id.fragment).setAlpha(intensity);
            //findViewById(R.id.fragment).setBackgroundColor(Color.GRAY);
        }
    }

    @Override
    public void openReviewFragment(int id){
        ReviewFragment.newInstance(id);
        findViewById(R.id.reviewFragment).setVisibility(View.VISIBLE);
    }
}
