package com.example.onlinestore.model;
public class CartModel {

    String ProductId,StoreId,UserId,productName,StoreName,OrderDate,Latitude,Longitude,productImage;
    double Quantity,Rate,Amount,StockInQuantity,Total;
    int id;
    public CartModel() {
    }

    public String getProductId() {
        return ProductId;
    }

    public String getStoreName() {
        return StoreName;
    }

    public double getTotal() {
        return Total;
    }

    public void setTotal(double total) {
        Total = total;
    }

    public void setStoreName(String storeName) {
        StoreName = storeName;
    }

    public double getStockInQuantity() {
        return StockInQuantity;
    }

    public void setStockInQuantity(double stockInQuantity) {
        StockInQuantity = stockInQuantity;
    }

    public int getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getStoreId() {
        return StoreId;
    }

    public void setStoreId(String storeId) {
        StoreId = storeId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public double getQuantity() {
        return Quantity;
    }

    public void setQuantity(double quantity) {
        Quantity = quantity;
    }

    public double getRate() {
        return Rate;
    }

    public void setRate(double rate) {
        Rate = rate;
    }

    public double getAmount() {
        return Amount;
    }

    public void setAmount(double amount) {
        Amount = amount;
    }
}
