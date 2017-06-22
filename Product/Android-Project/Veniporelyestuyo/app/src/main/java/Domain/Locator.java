package Domain;

import android.content.Context;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by Franco on 20/6/2017.
 */
public class Locator implements LocationListener {

    private LocationManager locationManager;
    private Double latitude;
    private Double longitude;
    private Criteria criteria;
    private Location location;
    private String provider;

    public Locator(Context context, int SDKVersion) {
        locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();

        if(SDKVersion >=23) {
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
        }else {
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        }
        provider = locationManager.getBestProvider(criteria, true);

        try{
            if(SDKVersion >=23) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, this);
            }else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 0, this);
            }

            //setMostRecentLocation(locationManager.getLastKnownLocation(provider));
            location = locationManager.getLastKnownLocation(provider);
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        catch (Exception e )
        {
            latitude = -34.903891;
            longitude = -56.190729;
        }
    }

    private void setMostRecentLocation(Location lastKnownLocation) {

    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Location getLocation() {

        return location;
    }

    @Override
    public void onLocationChanged(Location location) {
        double lon = (double) (location.getLongitude());/// * 1E6);
        double lat = (double) (location.getLatitude());// * 1E6);

        latitude = lat;
        longitude = lon;

    }

    @Override
    public void onProviderDisabled(String arg0) {

    }

    @Override
    public void onProviderEnabled(String arg0) {

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

    }

}