package layout;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.product.whitewalkers.veniporelyestuyo.R;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import ApiCommunicationManager.AccountApiCommunication;
import ApiCommunicationManager.CategoryApiCommunication;
import ApiCommunicationManager.ProductApiCommunication;
import ApiCommunicationManager.ProductStateApiCommunication;
import Domain.Account;
import Domain.Category;
import Domain.Locator;
import Domain.Product;
import Domain.ProductState;
import Domain.ResponseAsyncTask;
import Domain.ResponseHttp;
import MyStaticElements.LogRegistration;

import static android.app.Activity.RESULT_OK;

public class PublishProductFragment extends Fragment {

    private static final String TAG = "myLogMessageTag";
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private ImageView imgPhoto1;
    private ImageView imgPhoto2;
    private ImageView imgPhoto3;

    private Product actualProduct;
    private ArrayList<Category> categories;
    private ArrayList<ProductState> productStates;
    View view;

    private Location myLocation;

    Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (container != null) {
            container.removeAllViews();
        }
        view = inflater.inflate(R.layout.fragment_publish_product, container, false);

        actualProduct = new Product();

        SetButtonConfirm();

        imgPhoto1 = (ImageView)view.findViewById(R.id.imgPhoto1);
        imgPhoto2 = (ImageView)view.findViewById(R.id.imgPhoto2);
        imgPhoto3 = (ImageView)view.findViewById(R.id.imgPhoto3);

        setPhotosBtns();
        new CategoriesTask(context).execute();
        productStates = new ProductStateApiCommunication().getProductState();
        loadProductStates();
        return view;
    }

    public void loadProductStates(){
        ArrayList<String> productStatesOptions = new ArrayList<>();
        for (int i=0;i<productStates.size();i++){
            ProductState productState = productStates.get(i);
            productStatesOptions.add(productState.getName());
        }
        this.loadProductStateSpinner(productStatesOptions);
    }



    private void loadProductStateSpinner(ArrayList<String> prouctStateOptions) {

        Spinner spinnerStates = (Spinner) view.findViewById(R.id.productState);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context.getApplicationContext(), android.R.layout.simple_spinner_item, prouctStateOptions); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fillSpinners(spinnerStates,spinnerArrayAdapter);
    }

    private void fillSpinners(final Spinner spinnerStates, final ArrayAdapter<String> spinnerArrayAdapter) {
        spinnerStates.setAdapter(spinnerArrayAdapter);
    }

    private class CategoriesTask extends AsyncTask<Void, Void, ResponseAsyncTask> {

        private Context mContext;

        public CategoriesTask(Context context){
            mContext = context;
        }

        @Override
        protected ResponseAsyncTask doInBackground(Void... params) {
            ResponseHttp response;
            try{
                response = new CategoryApiCommunication().getCategories(mContext);
            } catch (IOException ioEx){
                return new ResponseAsyncTask<Exception>(ResponseAsyncTask.TypeResponse.EXCEPTION,ioEx);
            }
            catch (JSONException jsonEx){
                return new ResponseAsyncTask<Exception>(ResponseAsyncTask.TypeResponse.EXCEPTION,jsonEx);
            }
            return new ResponseAsyncTask<ResponseHttp>(ResponseAsyncTask.TypeResponse.OK,response);
        }

        @Override
        protected void onPostExecute(ResponseAsyncTask result) {
            if (result.getTypeResponse() == ResponseAsyncTask.TypeResponse.EXCEPTION){
                Toast.makeText(mContext,"Error en cargar categorías, intenta denuevo!",Toast.LENGTH_LONG).show();
                LogRegistration.log(LogRegistration.TypeLog.EXCEPTION,result.getDataResponse().toString());
                return;
            }
            else{
                ResponseHttp responseHttp = (ResponseHttp) result.getDataResponse();
                if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.SUCCESS){
                    Toast.makeText(mContext,"Categorías obtenidas",Toast.LENGTH_LONG).show();
                    categories = (ArrayList<Category>) responseHttp.getMessageObject();
                    loadCategories();
                } else if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.CLIENT_ERROR){
                    Toast.makeText(mContext,"Error en solicitud, intenta denuevo!",Toast.LENGTH_LONG).show();
                    LogRegistration.log(LogRegistration.TypeLog.ERROR, responseHttp.getMessage());
                }
                return;
            }
        }
    }
    private void loadCategories() {
        ArrayList<String> categoriesOptions = new ArrayList<String>();
        for (int i=0;i<categories.size();i++){
            Category cate = categories.get(i);
            categoriesOptions.add(cate.getName());
        }
        this.loadCategorySpinner(categoriesOptions);
    }

    private void loadCategorySpinner(ArrayList<String> categoriesOptions) {
        Spinner spinnerCategory = (Spinner) view.findViewById(R.id.productCategory);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context.getApplicationContext(), android.R.layout.simple_spinner_item, categoriesOptions); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fillSpinners(spinnerCategory,spinnerArrayAdapter);
    }

    //Images Logic
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        Button btn = (Button)view.findViewById(R.id.btnContinuar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "OnClick");
                EditText txtNombre = (EditText)view.findViewById(R.id.productName);
                EditText txtDescription = (EditText)view.findViewById(R.id.productDescription);
                Spinner spinCategory = (Spinner)view.findViewById(R.id.productCategory);
                Spinner spinState = (Spinner)view.findViewById(R.id.productState);

                String textNombre = txtNombre.getText().toString();
                String spinCategoryText = spinCategory.getSelectedItem().toString();
                String spinStateText = spinState.getSelectedItem().toString();
                String description = txtDescription.getText().toString();
                actualProduct.name = textNombre;
                actualProduct.description = description;

                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(context,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                        Locator locator = new Locator(context,android.os.Build.VERSION.SDK_INT);
                        if(locator.getLocation()!=null) {
                            myLocation = locator.getLocation();
                            actualProduct.latitude = myLocation.getLatitude();
                            actualProduct.longitude = myLocation.getLongitude();
                            //Toast.makeText(context,"Lat: " + myLocation.getLatitude() + " long: "+myLocation.getLongitude()+ ".", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        ActivityCompat.requestPermissions(getActivity(),new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},10);
                    }
                } else {
                    Locator locator = new Locator(context,android.os.Build.VERSION.SDK_INT);
                    if(locator.getLocation()!=null) {
                        myLocation = locator.getLocation();
                        actualProduct.latitude = myLocation.getLatitude();
                        actualProduct.longitude = myLocation.getLongitude();
                        //Toast.makeText(context,"Lat: " + myLocation.getLatitude() + " long: "+myLocation.getLongitude()+ ".", Toast.LENGTH_LONG).show();
                    }else{
                        actualProduct.latitude = -34.903891;
                        actualProduct.longitude = -56.190729;
                    }

                }


                CategoryApiCommunication categoryApiCommunication = new CategoryApiCommunication();
                ProductStateApiCommunication productStateApiCommunication = new ProductStateApiCommunication();

                try{
                    actualProduct.categoryId = categoryApiCommunication.getCategoryIdFromCategoriesCollection(spinCategoryText, categories);
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
                //if(actualProduct.image1 == null || actualProduct.image2 == null || actualProduct.image3 == null )
                    //Toast.makeText(context,"Debe tener al menos 3 fotos",Toast.LENGTH_LONG).show();
                //else
                    new ProductTask(actualProduct,context).execute();
            }
        });
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
                    Locator locator = new Locator(context,android.os.Build.VERSION.SDK_INT);
                    if (locator.getLocation() != null) {
                        myLocation = locator.getLocation();
                        actualProduct.latitude = myLocation.getLatitude();
                        actualProduct.longitude = myLocation.getLongitude();
                        //Toast.makeText(context,"Lat: " + myLocation.getLatitude() + " long: "+myLocation.getLongitude()+ ".", Toast.LENGTH_LONG).show();
                    }
                } else {

                    actualProduct.latitude = -34.903891;
                    actualProduct.longitude = -56.190729;
                    Toast.makeText(context, "No se pudo obtener la ubicacion dado que no se dieron permisos.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }


    private class ProductTask extends AsyncTask<Void, Void, ResponseAsyncTask> {

        private Context mContext;
        private Product actualProduct;

        public ProductTask(Product actualProduct,Context context){
            this.actualProduct = actualProduct;
            mContext = context;
        }

        @Override
        protected ResponseAsyncTask doInBackground(Void... params) {
            ResponseHttp response;
            try{
                response = new ProductApiCommunication(mContext).postProduct(actualProduct, mContext);
            } catch (IOException ioEx){
                return new ResponseAsyncTask<Exception>(ResponseAsyncTask.TypeResponse.EXCEPTION,ioEx);
            }
            catch (JSONException jsonEx){
                return new ResponseAsyncTask<Exception>(ResponseAsyncTask.TypeResponse.EXCEPTION,jsonEx);
            }
            response.setMessage("Ok");
            return new ResponseAsyncTask<>(ResponseAsyncTask.TypeResponse.OK,response);
        }

        @Override
        protected void onPostExecute(ResponseAsyncTask result) {
            if (result.getTypeResponse() == ResponseAsyncTask.TypeResponse.EXCEPTION){
                Toast.makeText(mContext,"Error en publicar producto, intenta denuevo!",Toast.LENGTH_LONG).show();
                LogRegistration.log(LogRegistration.TypeLog.EXCEPTION,result.getDataResponse().toString());
                return;
            }
            else{
                ResponseHttp responseHttp = (ResponseHttp) result.getDataResponse();
                if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.SUCCESS){
                    Toast.makeText(mContext,"Publicacion realizada",Toast.LENGTH_LONG).show();
                } else if(responseHttp.getTypeCode() == ResponseHttp.CategoryCodeResponse.CLIENT_ERROR){
                    Toast.makeText(mContext,"Error en solicitud, intenta denuevo!",Toast.LENGTH_LONG).show();
                    LogRegistration.log(LogRegistration.TypeLog.ERROR, responseHttp.getMessage());
                }
                return;
            }
        }
    }

    private void setPhotosBtns() {
        Button btnPhoto1 = (Button)view.findViewById(R.id.btnTakePhoto1);
        Button btnPhoto2 = (Button)view.findViewById(R.id.btnTakePhoto2);
        Button btnPhoto3 = (Button)view.findViewById(R.id.btnTakePhoto3);

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

    private void dispatchTakePictureIntent(int imageCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, imageCode);
        }
    }
}
