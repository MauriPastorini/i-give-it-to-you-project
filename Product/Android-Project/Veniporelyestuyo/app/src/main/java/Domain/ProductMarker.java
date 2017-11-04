package Domain;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

/**
 * Created by Mauri on 21-Jun-17.
 */

public class ProductMarker {
    private Marker marker;
    private Product product;

    public ProductMarker(Marker marker, Product product) {
        this.marker = marker;
        this.product = product;
    }

    public Marker getMarker() {

        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductMarker that = (ProductMarker) o;

        return marker.equals(that.marker);

    }

    @Override
    public int hashCode() {
        return marker.hashCode();
    }

    public static Product getProductFromMarker(ArrayList<ProductMarker> lstProductsMarkers, Marker marker) {
        String markerIdForSearch = marker.getId();
        for(ProductMarker productMarker : lstProductsMarkers){
            String actualMarkerId = productMarker.getMarker().getId();
            if (actualMarkerId.equals(markerIdForSearch))
                return productMarker.getProduct();
        }
        return null;
    }
}
