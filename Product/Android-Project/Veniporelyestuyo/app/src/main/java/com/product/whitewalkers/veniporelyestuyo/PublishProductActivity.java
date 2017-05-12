package com.product.whitewalkers.veniporelyestuyo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;

import ApiCommunicationManager.ProductApiCommunication;
import Domain.Product;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.product.whitewalkers.veniporelyestuyo.R.id.image;
import static com.product.whitewalkers.veniporelyestuyo.R.id.imgPhoto1;
import static com.product.whitewalkers.veniporelyestuyo.R.id.productState;
import static com.product.whitewalkers.veniporelyestuyo.R.id.text;

public class PublishProductActivity extends AppCompatActivity {

    private static final String TAG = "myLogMessageTag";
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            return false;
        }

    };

    private ImageView imgPhoto1;
    private ImageView imgPhoto2;
    private ImageView imgPhoto3;

    private Product actualProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_product);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        actualProduct = new Product();

        SetButtonConfirm();

        imgPhoto1 = (ImageView)findViewById(R.id.imgPhoto1);
        imgPhoto2 = (ImageView)findViewById(R.id.imgPhoto2);
        imgPhoto3 = (ImageView)findViewById(R.id.imgPhoto3);

        Button btnPhoto1 = (Button)findViewById(R.id.btnTakePhoto1);
        Button btnPhoto2 = (Button)findViewById(R.id.btnTakePhoto2);
        Button btnPhoto3 = (Button)findViewById(R.id.btnTakePhoto3);

        btnPhoto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "TakePhotoBtnClick1");
                dispatchTakePictureIntent(1);
            }
        });

        btnPhoto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "TakePhotoBtnClick2");
                dispatchTakePictureIntent(2);
            }
        });

        btnPhoto3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "TakePhotoBtnClick3");
                dispatchTakePictureIntent(3);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imgPhoto1.setImageBitmap(imageBitmap);
            actualProduct.image1 = imageBitmap;
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imgPhoto2.setImageBitmap(imageBitmap);
            actualProduct.image2 = imageBitmap;
        } else if (requestCode == 3 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imgPhoto3.setImageBitmap(imageBitmap);
            actualProduct.image3 = imageBitmap;
        }
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
                actualProduct.name = textNombre;
                actualProduct.category = spinCategoryText;
                actualProduct.state = spinStateText;
                actualProduct.latitude = 1; //HARDCODE, change for getMapLatitude()
                actualProduct.longitude = 1;//HARDCODE, change for getMapLongitude()
                actualProduct.category = "1"; //HARDCODE, change for getCategoryIdFromName(spinCategoryText)
                actualProduct.state = "1";//HARDCODE, change for getStateIdFromName(spinStateText)
                Log.i(TAG, "Click info " + textNombre + " - " + spinCategoryText + " - " + spinStateText);
                try{
                    new ProductApiCommunication().postProduct(actualProduct);
                }catch (JSONException ex){
                    Log.i(TAG, "Problems in creating json: " + ex);
                }
                catch (IOException ex){
                    Log.i(TAG, "Problems in connection: " + ex);
                }

            }
        });
    }

    private void dispatchTakePictureIntent(int imageCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, imageCode);
        }
    }

}
