package com.example.sifatsp.coletor.interfaces;

import com.example.sifatsp.coletor.model.ProductModel;

public interface OnProductListener {

    void beforeCreate(ProductModel model);
    void barcodeCapture(ProductModel model);
    void cancel();
}
