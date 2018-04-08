package com.example.sifatsp.coletor.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sifatsp.coletor.MainActivity;
import com.example.sifatsp.coletor.R;
import com.example.sifatsp.coletor.interfaces.OnProductListener;
import com.example.sifatsp.coletor.model.ProductModel;

import java.util.UUID;


public class ProductCreateFragment extends Fragment {

    private EditText txtName;
    private TextView txtCode;
    private TextView txtQnt;
    private Button btnCapture;
    private Button btnAdd;
    private Button btnCancel;
    private ProductModel model;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.product_create_model, container, false);


        this.model = new ProductModel(UUID.randomUUID().toString(), "", "", 0);

        this.txtName = (EditText)layout.findViewById(R.id.txtName);
        this.txtCode = (TextView)layout.findViewById(R.id.txtCode);
        this.txtQnt = (TextView)layout.findViewById(R.id.txtQnt);
        this.btnCapture = (Button)layout.findViewById(R.id.btnCapture);
        this.btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof OnProductListener){
                    OnProductListener listener = (OnProductListener) getActivity();
                    listener.barcodeCapture(model);
                }
            }
        });

        this.btnAdd = (Button)layout.findViewById(R.id.btnAdd);
        this.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof  OnProductListener){
                    OnProductListener listener = (OnProductListener) getActivity();
                    model.Name = txtName.getText().toString();
                    model.Barcode = txtCode.getText().toString();
                    model.qnt = Double.valueOf(txtQnt.getText().toString());

                    listener.beforeCreate(model);
                }
            }
        });

        this.btnCancel = (Button)layout.findViewById(R.id.btnCancel);
        this.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof OnProductListener){
                    OnProductListener listener = (OnProductListener) getActivity();
                    listener.cancel();
                }
            }
        });

        return layout;
    }

      public void setDados(ProductModel model){
          this.txtCode.setText(model.Barcode);
          this.txtName.setText(model.Name);
//          this.txtQnt.setText((double) model.qnt);
      }
}
