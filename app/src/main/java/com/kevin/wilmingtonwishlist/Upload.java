package com.kevin.wilmingtonwishlist;

import com.google.firebase.database.Exclude;

public class Upload {
    private String mName;
    private String mImageUrl;
    private String mDescription;
    private String mPrice;
    private String mContactEmail;
    private String mKey;
    private String mUser;

    public Upload(){
        //empty constructor needed.
    }

    public Upload(String name, String imageUrl, String description, String price, String contactEmail, String user){
       if (name.trim().equals("")) {
           name = "No Name";
       }
       if (description.equals("")) {
           description = "No Description";
       }
       if (price.equals("")) {
           price = "No Price";
       }
       if (contactEmail.equals("")) {
           contactEmail = "No Email";
       }

        mName = name;
        mImageUrl = imageUrl;
        mDescription = description;
        mPrice = price;
        mContactEmail = contactEmail;
        mUser = user;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getImageUrl(){
        return mImageUrl;
    }

    public void setImageUrl (String imageUrl){
        mImageUrl = imageUrl;
    }

    public String getDescription(){
        return mDescription;
    }

    public void setDescription(String description){
        mDescription = description;
    }

    public String getPrice(){
        return mPrice;
    }

    public void setPrice(String price){
        mPrice = price;
    }

    public String getContactEmail(){
        return mContactEmail;
    }

    public void setContactEmail(String contactEmail){
        mContactEmail = contactEmail;
    }

    @Exclude
    public String getKey(){
        return mKey;
    }

    @Exclude
    public void setKey(String key){
        mKey = key;
    }

    public String getmUser(){
        return mUser;
    }

    public void setUser(String user){
        mUser=user;
    }


}
