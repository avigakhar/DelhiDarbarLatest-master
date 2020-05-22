package com.app.delhidarbar.utils;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmProduct extends RealmObject {
    @PrimaryKey
    private int realmid;
    private int id;
    private int category_id;
    private int reviews;
    private int rating;
    private int quantity;
    private double price;
    private double myFinalPrice;
    private String description;
    private String image;
    private String name;
    private String type;
    private String all_type;
    private String select_type;
    private String ingredients;
    private String spices;
    private String addon_id;
    private String variantsJson;

    public RealmProduct() {
    }

    public String getAddon_id() {
        return addon_id;
    }

    public void setAddon_id(String addon_id) {
        this.addon_id = addon_id;
    }

    public int getRealmid() {
        return realmid;
    }

    public void setRealmid(int realmid) {
        this.realmid = realmid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getReviews() {
        return reviews;
    }

    public void setReviews(int reviews) {
        this.reviews = reviews;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpices() {
        return spices;
    }

    public void setSpices(String spices) {
        this.spices = spices;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAll_type() {
        return all_type;
    }

    public void setAll_type(String all_type) {
        this.all_type = all_type;
    }

    public String getSelect_type() {
        return select_type;
    }

    public void setSelect_type(String select_type) {
        this.select_type = select_type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getMyFinalPrice() {
        return myFinalPrice;
    }

    public void setMyFinalPrice(double myFinalPrice) {
        this.myFinalPrice = myFinalPrice;
    }

    public String getVariantsJson() {
        return variantsJson;
    }

    public RealmProduct setVariantsJson(String variantsJson) {
        this.variantsJson = variantsJson;
        return this;
    }
}
