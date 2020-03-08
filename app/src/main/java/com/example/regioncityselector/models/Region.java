package com.example.regioncityselector.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Region {

    public Region(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Expose
    @SerializedName("stateId")
    private int id;

    @Expose
    @SerializedName("stateName")
    private String name;

    @Expose
    @SerializedName("citiesArray")
    private List<City> cityList;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<City> getCityList() {
        if (cityList == null) {
            cityList = new ArrayList<>();
        }
        return cityList;
    }

    public Region getJustRegion() {
        return new Region(id, name);
    }

    @Override
    public @NonNull String toString() {
        return "Region{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cityList=" + cityList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Region region = (Region) o;

        if (id != region.id) return false;
        return name.equals(region.name);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        return result;
    }
}
