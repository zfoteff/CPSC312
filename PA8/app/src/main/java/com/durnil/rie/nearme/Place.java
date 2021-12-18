/**
 * This program implements the Place class for NearMe. It stores the place id, name,
 * address, and rating.
 *
 * Rie implemented the design of the PlaceSearchActivity (minus the RecyclerView),
 * getting the user's location, calling the PlacesAPI search, and parsing the search data
 * into Place objects.
 *
 * Zac
 *
 * Sources:
 * https://developers.google.com/maps/documentation/places/web-service/place-id
 * https://stackoverflow.com/questions/13135447/setting-onclicklistener-for-the-drawable-right-of-an-edittext
 *
 * CPSC 312, Fall 2021
 * Programming Assignment #8
 *
 * @author Rie Durnil and Zac Foteff
 * @version v1 12/9/21
 */

package com.durnil.rie.nearme;

public class Place {
    private String id;
    private String name;
    private String vicinity;
    private boolean isOpen;
    private double rating;
    private String phone;
    private String review;

    /**
     Constructor for Place
     */
    public Place(String id, String name, String vicinity, boolean isOpen, double rating) {
        this.id = id;
        this.name = name;
        this.vicinity = vicinity;
        this.isOpen = isOpen;
        this.rating = rating;
        this.phone = "";
        this.review = "";
        this.phone = "";
    }

    @Override
    public String toString() {
        return ""+this.id+" "+this.name+" "+this.vicinity+" "+this.isOpen+" "+this.rating;
    }

    /**
     Getter for id
     */
    public String getId() {
        return id;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    /**
     Setter for id
     */
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String reiew) {
        this.review = review;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}