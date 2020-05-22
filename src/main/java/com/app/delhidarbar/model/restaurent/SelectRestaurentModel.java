package com.app.delhidarbar.model.restaurent;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SelectRestaurentModel {

@SerializedName("error")
@Expose
private Boolean error;
@SerializedName("restaurants")
@Expose
private List<Restaurant> restaurants = null;

public Boolean getError() {
return error;
}

public void setError(Boolean error) {
this.error = error;
}

public List<Restaurant> getRestaurants() {
return restaurants;
}

public void setRestaurants(List<Restaurant> restaurants) {
this.restaurants = restaurants;
}

}