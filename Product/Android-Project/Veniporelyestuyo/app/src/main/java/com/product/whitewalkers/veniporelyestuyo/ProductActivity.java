package com.product.whitewalkers.veniporelyestuyo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import ApiCommunicationManager.ConnectionHandler;
import layout.ProductFragment;

public class ProductActivity extends AppCompatActivity implements ProductFragment.IProductFragment{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        callLoadFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
    }

    private void checkConnection() {
        new ConnectionHandler().controlConnectionsAvaiable(this);
    }


    private void callLoadFragment(){
        ProductFragment bottomPictureFragment = (ProductFragment) getSupportFragmentManager().findFragmentById(R.id.productInfoFragment);
        bottomPictureFragment.setProductInfo(1);
    }

    public void loadingVisible(boolean visible) {
        if (visible) {
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
        }
    }
}
