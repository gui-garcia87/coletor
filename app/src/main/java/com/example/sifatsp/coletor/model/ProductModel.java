package com.example.sifatsp.coletor.model;

import java.io.Serializable;

public class ProductModel implements Serializable {

    public String Id;
    public String Name;
    public String Barcode;
    public double qnt;

    public ProductModel(String id, String name, String barcode,double qnt) {
        Id = id;
        Name = name;
        Barcode = barcode;
        this.qnt = qnt;
    }
}
