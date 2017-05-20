package com.product.whitewalkers.veniporelyestuyo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import ApiCommunicationManager.CategoryApiCommunication;
import ApiCommunicationManager.ConnectionHandler;
import ApiCommunicationManager.ProductApiCommunication;
import ApiCommunicationManager.ProductStateApiCommunication;
import Domain.Category;
import Domain.Product;
import Domain.ProductState;
import MyStaticElements.DialogCloseDueToConnection;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.os.Build.VERSION_CODES.M;
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
    private ArrayList<Category> categories;
    private ArrayList<ProductState> productStates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_product);

        CheckConnection();

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

        new Thread(new Runnable() {
            public void run() {
                loadCategories();
                loadProductStates();
            }
        }).start();
    }

    private void CheckConnection() {
        new ConnectionHandler().controlConnectionsAvaiable(this);
    }

    public void loadCategories(){
        categories = new ArrayList<>();
        try{
            categories = new CategoryApiCommunication().getCategories();
        }
        catch (IOException ex){
            Log.i(TAG, "Error en get de categorias del servidor: " + ex);
        }
        catch (JSONException ex){
            Log.i(TAG, "Error convirtiendo data a JSON: " + ex);
        }

        ArrayList<String> categoriesOptions = new ArrayList<String>();
        for (int i=0;i<categories.size();i++){
            Category cate = categories.get(i);
            categoriesOptions.add(cate.getName());
        }
        this.loadCategorySpinner(categoriesOptions);
    }

    public void loadProductStates(){
        productStates = new ArrayList<>();
        try{
            productStates = new ProductStateApiCommunication().getProductState();
        }
        catch (IOException ex){
            Log.i(TAG, "Error en get de categorias del servidor: " + ex);
        }
        catch (JSONException ex){
            Log.i(TAG, "Error convirtiendo data a JSON: " + ex);
        }

        ArrayList<String> productStatesOptions = new ArrayList<String>();
        for (int i=0;i<productStates.size();i++){
            ProductState productState = productStates.get(i);
            productStatesOptions.add(productState.getName());
        }
        this.loadProductStateSpinner(productStatesOptions);
    }

    private void loadCategorySpinner(ArrayList<String> categoriesOptions) {
        Spinner spinnerCategory = (Spinner) findViewById(R.id.productCategory);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoriesOptions); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        runTherD(spinnerCategory,spinnerArrayAdapter);
    }

    private void loadProductStateSpinner(ArrayList<String> prouctStateOptions) {

        Spinner spinnerStates = (Spinner) findViewById(R.id.productState);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, prouctStateOptions); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        runTherD(spinnerStates,spinnerArrayAdapter);
    }

    private void runTherD(final Spinner spinnerStates, final ArrayAdapter<String> spinnerArrayAdapter) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinnerStates.setAdapter(spinnerArrayAdapter);
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

                actualProduct.latitude = 1; //HARDCODE, change for getMapLatitude()
                actualProduct.longitude = 1;//HARDCODE, change for getMapLongitude()
                ProductApiCommunication productApiCommunication = new ProductApiCommunication();
                CategoryApiCommunication categoryApiCommunication = new CategoryApiCommunication();
                ProductStateApiCommunication productStateApiCommunication = new ProductStateApiCommunication();

                try{
                    actualProduct.categoryId = categoryApiCommunication.getCategorieIdFromCategoriesCollection(spinCategoryText, categories);
                }
                catch (Resources.NotFoundException ex){
                    Log.i(TAG, "Error en cargar id de categoria, no se encontro la categoria de nombre: " + spinCategoryText + " en la lista: " + categories.toString());
                }
                try{
                    actualProduct.stateId = productStateApiCommunication.getStateIdFromStatesCollection(spinStateText, productStates);
                }
                catch (Resources.NotFoundException ex){
                    Log.i(TAG, "Error en cargar id de Estado, no se encontro la categoria de nombre: " + spinCategoryText + " en la lista: " + categories.toString());
                }
                try{
                    productApiCommunication.postProduct(actualProduct);
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
