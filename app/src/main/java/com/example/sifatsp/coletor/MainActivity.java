package com.example.sifatsp.coletor;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements OnProductListener{

    // TAG para encontrar erros
    private static final String TAG = "MyActivity";
    // Array para armazenar produtos utilizando a classe product model
    private ArrayList<ProductModel> products;
    // Fragment setado como null, somente iniciar o fragment quando criar produtos
    private ProductCreateFragment createFragment = null;
    // Botao criado para receber a criação de produtos
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // TAG para saber o retorno do onCreate
        Log.i(TAG, "onCreate: retorna onCreate");

        // Setar como null para criar o fragment no momento certo
        this.createFragment = null;

        // Atribuindo valor ao button
        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Instanciando o fragment para criação de produtos
                createFragment = new ProductCreateFragment();
                // Metodo para substituir o fragment
                replaceFragment(createFragment);
                // Faz o button sumir
                floatingActionButton.setVisibility(View.GONE);
                // Salva o arquivo
//                saveTxt();
            }
        });

        // Recebe os produtos listados
        this.products = retornoTXT();

        // Cria lista com os produtos do arquivo
        createProductList();


    }

    // Metodo para salvar o .txt
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

        try {
            FileOutputStream file = openFileOutput("coletor.TXT",MODE_PRIVATE);

            PrintWriter writer = new PrintWriter(file);
            writer.print(s);
            Log.i(TAG, getFilesDir().getPath());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), coletor);
        if (!file.mkdirs()) {
            Log.e("Directory", "Directory not created");
        }
        return file;
    }

    public ArrayList<ProductModel> retornoTXT(){

        ArrayList<ProductModel> listTxt = new ArrayList<ProductModel>();
        String s = null;
        InputStream inputStream = null;
        BufferedReader reader = null;


        try {

            if(isExternalStorageReadable()){

            }

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
