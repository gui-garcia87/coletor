package com.example.sifatsp.coletor;


import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.sifatsp.coletor.fragments.ProductCreateFragment;
import com.example.sifatsp.coletor.fragments.ProductListFragment;
import com.example.sifatsp.coletor.interfaces.OnProductListener;
import com.example.sifatsp.coletor.model.ProductModel;
import com.github.clans.fab.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements OnProductListener{

    private static final String TAG = "MyActivity";
    private ArrayList<ProductModel> products;
    private ProductCreateFragment createFragment = null;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: retorna onCreate");

        this.createFragment = null;

       floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFragment = new ProductCreateFragment();
                replaceFragment(createFragment);
                floatingActionButton.setVisibility(View.GONE);
            }
        });



        this.products = retornoTXT();

        createProductList();


    }



    public ArrayList<ProductModel> retornoTXT(){

        ArrayList<ProductModel> listTxt = new ArrayList<ProductModel>();
        String s = null;
        InputStream inputStream = null;
        BufferedReader reader = null;


        try {
            inputStream = getResources().openRawResource(R.raw.coletor);
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;


            while ((line = reader.readLine()) != null){

                String[] stringsSeparada = line.split(";");
                ProductModel model = new ProductModel(stringsSeparada[0],stringsSeparada[1],stringsSeparada[0],Double.parseDouble(stringsSeparada[4]));
                listTxt.add(model);

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return listTxt;
    }

    private void createProductList(){

        Bundle bundle = new Bundle();
        bundle.putSerializable("dataSource", products);

        ProductListFragment fragment = new ProductListFragment();
        fragment.setArguments(bundle);

        replaceFragment(fragment);

        this.createFragment = null;

    }

    private void replaceFragment(android.support.v4.app.Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.customLayout, fragment);
        ft.commit();
    }

 /*   public ArrayList<ProductModel> fakeList(){
        ArrayList<ProductModel> result = new ArrayList<ProductModel>();
        result.add(new ProductModel(UUID.randomUUID().toString(), "Keyboard", "1212121212222",0));
        result.add(new ProductModel(UUID.randomUUID().toString(), "Mouse", "2323234343231",0));
        result.add(new ProductModel(UUID.randomUUID().toString(), "Notebook", "90909099989898",0));
        result.add(new ProductModel(UUID.randomUUID().toString(), "Printer", "0909878787654",0));
        return result;
    }*/

    @Override
    public void beforeCreate(ProductModel model) {

        for (int i = products.size() - 1; i >= 0; i--) {
            String s = products.get(i).Barcode;


            if( s.equals(model.Barcode)){

                this.products.set(i,model);
                createProductList();
            } else{

                this.products.add(model);
                createProductList();
            }
        }


    }

    @Override
    public void barcodeCapture(ProductModel model) {
        IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
        integrator.initiateScan();
    }

    @Override
    public void cancel() {

        createProductList();
        floatingActionButton.setVisibility(View.VISIBLE);

    }

    public ProductModel comparador(String barcode){

        for (int i = products.size() - 1; i >= 0; i--) {
            String s = products.get(i).Barcode;
            Log.i(TAG, barcode);

           if( s.equals(barcode)){

                ProductModel model = new ProductModel(products.get(i).Id, products.get(i).Name, products.get(i).Barcode, products.get(i).qnt);

//                return true;
               return model;
           }
        }

        return null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null){
            String barcode = result.getContents();
            if (barcode != null && !"".equals(barcode)){

                Log.i("teste", barcode);
                if(comparador(barcode).Barcode.equals(barcode)) {
//                    createFragment.setBarcode(barcode);
                      createFragment.setDados(comparador(barcode));
                     Toast.makeText(getApplicationContext(), "produto adicionado", Toast.LENGTH_LONG).show();
                }else
                    Toast.makeText(getApplicationContext(), "produto nao adicionado", Toast.LENGTH_LONG).show();

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
