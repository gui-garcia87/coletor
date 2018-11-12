package com.example.sifatsp.coletor;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
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

                saveTxt();
            }
        });

        this.products = retornoTXT();

        createProductList();

    }


    public void saveTxt(){


        String s = null;
        StringBuilder builder = new StringBuilder();

       for (int i = 0; i < this.products.size(); i++){
           builder.append(products.get(i).Id);
           builder.append(";");
           builder.append(products.get(i).Name);
           builder.append(";");
           builder.append(products.get(i).qnt);
           builder.append(";");
           builder.append(products.get(i).Barcode);
           builder.append("\n");
       }

       Log.i(TAG,builder.toString());

       s = builder.toString();

        Log.i("Arquivo", "arquivo salvo");


        if (isExternalStorageWritable()) {

            System.out.println(isExternalStorageWritable());


            File folder = new File(Environment.getExternalStorageDirectory() + "/COLETA");
            if (!folder.exists()) {
                folder.mkdir();
            }

            System.out.println(folder.isDirectory());

            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/COLETA/" + "coletor.txt");

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
            }


            try {


                FileOutputStream outputStream = new FileOutputStream(file);

                PrintWriter writer = new PrintWriter(outputStream);
                writer.print(s);
                writer.flush();
                writer.close();
                Log.i(TAG, getFilesDir().getPath());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }


    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public File getStorageDir(String coletor) {

        if (Build.VERSION.SDK_INT >= 23) {
            int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }


            File file = new File(Environment.getExternalStorageDirectory() + "/COLETA",coletor);


            if(file.exists()){
                Log.e("File", "file created");
                Log.i("File", file.getAbsolutePath());
            }

        return file;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1000:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permissao Concedida", Toast.LENGTH_SHORT);
                } else {
                    Toast.makeText(this, "Permissao nao Concedida", Toast.LENGTH_SHORT);
                    finish();
                }
        }
    }

    public ArrayList<ProductModel> retornoTXT(){

        ArrayList<ProductModel> listTxt = new ArrayList<ProductModel>();


        String s = "coletor.txt";
        InputStream inputStream = null;
        BufferedReader reader = null;



        try {

            if (isExternalStorageReadable()) {

                try {
                    inputStream = new FileInputStream(getStorageDir(s));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            } else {

                Toast.makeText(getApplicationContext(), "Sem Acesso ao Arquivo", Toast.LENGTH_LONG).show();

            }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;


                while ((line = reader.readLine()) != null) {

                    String[] stringsSeparada = line.split(";");
                    ProductModel model = new ProductModel(stringsSeparada[0], stringsSeparada[1], stringsSeparada[2], Double.parseDouble(stringsSeparada[3]));
                    listTxt.add(model);

                }

            } catch(IOException e){
                e.printStackTrace();
            } finally{
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (reader != null) {
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

    @Override
    public void beforeCreate(ProductModel model) {

        for (int i = products.size() - 1; i >= 0; i--) {
            String s = products.get(i).Barcode;


            if( s.equals(model.Barcode)){

                this.products.set(i,model);
                createProductList();
                floatingActionButton.setVisibility(View.VISIBLE);
            } else{

                this.products.add(model);
                createProductList();
                floatingActionButton.setVisibility(View.VISIBLE);
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
