package com.product.whitewalkers.veniporelyestuyo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import Domain.Product;
import layout.ProductFilters;
import layout.ProductFragment;


public class ProductsMapActivity extends FragmentActivity implements OnMapReadyCallback, ProductFilters.IProductFilter {

    private GoogleMap mMap;

    private ArrayList<Product> productsToShow = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_map);
        ProductFilters bottomPictureFragment = (ProductFilters) getSupportFragmentManager().findFragmentById(R.id.productFilter);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //MyLocation myLocation = getActualLocation();
        MyLocation myLocation = new MyLocation(-34.903891, -56.190729);

        LatLng productLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.zoomTo(20));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(productLatLng));
        mMap.addMarker(new MarkerOptions().position(productLatLng).title("Usted esta aqui."));
        loadProductsMarkers();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

    }

    private void loadProductsMarkers() {
        for(Product product: productsToShow){
            LatLng productLatLng = new LatLng(product.latitude, product.longitude);
            mMap.addMarker(new MarkerOptions().position(productLatLng).title(product.name));
        }
    }

    @Override
    public void updateProductList(ArrayList<Product> productList) {
        productsToShow = productList;
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

