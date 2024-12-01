package com.example.shoppinglist;

public class Product {

    private String Name;
    private int Price;
    private int Quantity;

    public Product(){

    }

    public Product(String name, int price, int quantity) {
        this.Name = name;
        this.Price = price;
        this.Quantity = quantity;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        this.Price = price;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        this.Quantity = quantity;
    }
}
