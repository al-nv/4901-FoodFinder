package com.example.onlinestore.model;
public class StoreModel {
    String storeId,storaName,address,phoneNo,mobileNo,email,operationHour,CloseHour,storeImage,latitude,longitude;

    public StoreModel() {
    }

    public String getStoreId() {
        return storeId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public String getCloseHour() {
        return CloseHour;
    }

    public void setCloseHour(String closeHour) {
        CloseHour = closeHour;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoraName() {
        return storaName;
    }

    public void setStoraName(String storaName) {
        this.storaName = storaName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOperationHour() {
        return operationHour;
    }

    public void setOperationHour(String operationHour) {
        this.operationHour = operationHour;
    }

    public String getStoreImage() {
        return storeImage;
    }

    public void setStoreImage(String storeImage) {
        this.storeImage = storeImage;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
