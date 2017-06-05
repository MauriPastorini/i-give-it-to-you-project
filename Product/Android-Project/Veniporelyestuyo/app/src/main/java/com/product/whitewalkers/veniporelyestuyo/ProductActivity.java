package com.product.whitewalkers.veniporelyestuyo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import layout.ProductFragment;

public class ProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        callLoadFragment();
    }

    private void callLoadFragment(){
        ProductFragment bottomPictureFragment = (ProductFragment) getSupportFragmentManager().findFragmentById(R.id.productInfoFragment);
        bottomPictureFragment.setProductInfo(1);
    }
}
