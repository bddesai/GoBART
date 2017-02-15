package com.source.bhavin.gobart.model;

/**
 * Created by Bhavin on 3/21/2015.
 */
public class Station {
    private String Name;
    private String Abbr;

    private String latitude;
    private String longitude;

    private String address, city, county, state, zipcode;

    public Station() { }

    public String getName() {
        return this.Name;
    }

    public void setName(String name) { this.Name = name; }

    public String getAbbr() {
        return this.Abbr;
    }

    public void setAbbr(String abbr) {
        this.Abbr = abbr;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() { return latitude; }

    public void setLatitude(String latitude) { this.latitude = latitude; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public String getCounty() { return county; }

    public void setCounty(String county) { this.county = county; }

    public String getState() { return state; }

    public void setState(String state) { this.state = state; }

    public String getZipcode() { return zipcode; }

    public void setZipcode(String zipcode) { this.zipcode = zipcode; }
}
