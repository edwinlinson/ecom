package com.example.demo.Global;

import com.example.demo.Model.Product;

import java.util.ArrayList;
import java.util.List;

public class GlobalData {
    public static List<Product> cart;
    static{
        cart = new ArrayList<Product>();
    }
}
