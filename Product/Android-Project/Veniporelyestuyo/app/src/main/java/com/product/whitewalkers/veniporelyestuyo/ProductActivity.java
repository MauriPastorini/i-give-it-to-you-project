package com.product.whitewalkers.veniporelyestuyo;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import ApiCommunicationManager.AccountApiCommunication;
import ApiCommunicationManager.ConnectionHandler;
import Domain.Account;
import MyStaticElements.LogRegistration;
import layout.ProductFragment;

public class ProductActivity extends AppCompatActivity implements ProductFragment.IProductFragment{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        loadInfoProductFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
    }

    private void checkConnection() {
        new ConnectionHandler().controlConnectionsAvaiable(this);
    }


    private void loadInfoProductFragment(){
        Bundle bundle = new Bundle();
        bundle.putString("typeProductInfo", ProductFragment.TypeInfo.VIEW_SOLICITUDE.toString());
        bundle.putString("productId", ""+ 1);//HARDCODED

        Fragment fragment = new ProductFragment();
        fragment.setArguments(bundle);

        FragmentManager managerInfoProduct = getSupportFragmentManager();
        FragmentTransaction transaction = managerInfoProduct.beginTransaction();
        transaction.replace(R.id.productInfoFragment, fragment);
        transaction.commit();

       // ProductFragment productFragment = (ProductFragment) getSupportFragmentManager().findFragmentById(R.id.productInfoFragment);
        //productFragment.setProductInfo(11);
    }

    @Override
    public void loadingVisible(boolean visible) {
        if (visible) {
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void returnToPreviousActivityOrFragment() {
        startActivity(new Intent(ProductActivity.this,MainMenuUserActivity.class));
    }
}
