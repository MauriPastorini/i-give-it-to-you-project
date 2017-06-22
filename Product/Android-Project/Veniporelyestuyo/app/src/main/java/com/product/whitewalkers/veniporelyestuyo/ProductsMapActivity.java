package com.product.whitewalkers.veniporelyestuyo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;

import ApiCommunicationManager.ConnectionHandler;
import Domain.Product;
import Domain.Locator;
import Domain.ProductMarker;
import MyStaticElements.LogRegistration;
import layout.ProductFilters;
import layout.ProductFragment;
import layout.PublishProductFragment;

import static android.R.attr.configure;
import static android.os.Build.VERSION_CODES.M;


public class ProductsMapActivity extends FragmentActivity implements GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback, ProductFilters.IProductFilter , ProductFragment.IProductFragment {

    private GoogleMap mMap;
    private ArrayList<Product> productsToShow = new ArrayList<>();

    private MyLocation myLocation = new MyLocation(-34.903891, -56.190729);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        checkConnection();

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                Locator locator = new Locator(this);
                if(locator.getLocation()!=null) {
                    myLocation.setLatitude(locator.getLatitude());
                    myLocation.setLongitude(locator.getLongitude());
                }
            } else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},10);
            }
        } else {
            Locator locator = new Locator(this);
            if(locator.getLocation()!=null) {
                myLocation.setLatitude(locator.getLatitude());
                myLocation.setLongitude(locator.getLongitude());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 10: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Locator locator = new Locator(this);
                    if (locator.getLocation() != null) {
                        myLocation.setLatitude(locator.getLatitude());
                        myLocation.setLongitude(locator.getLongitude());
                    }
                } else {
                    Toast.makeText(this, "No se pudo obtener la ubicacion dado que no se dieron permisos.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void loadProductsMarkers() {
        if (mMap!= null){
            mMap.clear();
            lstProductsMarkers.clear();
            setMarkerOnCurrentLocation();
            for(Product product: productsToShow){
                LatLng productLatLng = new LatLng(product.latitude, product.longitude);
                LogRegistration.log(LogRegistration.TypeLog.STEP,product.name);
                Marker marker = mMap.addMarker(new MarkerOptions().position(productLatLng).title(product.name));
                lstProductsMarkers.add(new ProductMarker(marker,product));
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        loadingVisible(false);
        mMap = googleMap;
        loadProductsMarkers();
    }

    void setMarkerOnCurrentLocation() {
        LatLng userLatLang = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLatLang));
        Marker marker = mMap.addMarker(new MarkerOptions().position(userLatLang).title("Usted esta aqui."));
        lstProductsMarkers.add(new ProductMarker(marker,null));
        mMap.setOnInfoWindowClickListener(this);
    }

    ArrayList<ProductMarker> lstProductsMarkers = new ArrayList<>();

    @Override
    public void onInfoWindowClick(Marker marker) {
        Product product = ProductMarker.getProductFromMarker(lstProductsMarkers,marker);
        if(product!=null){
            loadingVisible(true);
            Bundle bundle = new Bundle();
            bundle.putString("typeProductInfo", ProductFragment.TypeInfo.MAKE_SOLICITUDE.toString());
            bundle.putString("productId", ""+ product.id);

            Fragment fragment = new ProductFragment();
            fragment.setArguments(bundle);

            FragmentManager managerInfoProduct = getSupportFragmentManager();
            FragmentTransaction transaction = managerInfoProduct.beginTransaction();
            transaction.replace(R.id.map, fragment);
            transaction.commit();
        }
    }

    private void checkConnection() {
        new ConnectionHandler().controlConnectionsAvaiable(this);
    }

    @Override
    public void updateProductList(ArrayList<Product> productList) {
        productsToShow = productList;
        loadProductsMarkers();
    }

    @Override
    public void loadingVisible(boolean visible){
        if (visible) {
            findViewById(R.id.progressBarMap).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.progressBarMap).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void returnToPreviousActivityOrFragment(){
        loadingVisible(false);
        startActivity(new Intent(ProductsMapActivity.this,ProductsMapActivity.class));
    }
}

class MyLocation{
    private double latitude;
    private double longitude;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MyLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}

