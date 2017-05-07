package com.product.whitewalkers.veniporelyestuyo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import ApiCommunicationManager.ProductApiCommunication;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.product.whitewalkers.veniporelyestuyo.R.id.productState;

public class PublishProductActivity extends AppCompatActivity {

    private static final String TAG = "myLogMessageTag";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_product);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        SetButtonConfirm();
    }

    private void SetButtonConfirm() {
        Button btn = (Button)findViewById(R.id.btnContinuar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "OnClick");
                EditText txtNombre = (EditText)findViewById(R.id.productName);
                Spinner spinCategory = (Spinner)findViewById(R.id.productCategory);
                Spinner spinState = (Spinner)findViewById(R.id.productState);

                String textNombre = txtNombre.getText().toString();
                String spinCategoryText = spinCategory.getSelectedItem().toString();
                String spinStateText = spinState.getSelectedItem().toString();

                Log.i(TAG, "Click info " + textNombre + " - " + spinCategoryText + " - " + spinStateText);


                ProductApiCommunication.postProduct(textNombre,spinCategoryText, spinStateText);

            }
        });
    }

}
