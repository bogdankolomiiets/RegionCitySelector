package com.example.regioncityselector;

import com.example.regioncityselector.models.City;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class UkrainianCityComparator implements Comparator<City> {
    @Override
    public int compare(City city, City city2) {
        Collator collator = Collator.getInstance(new Locale("uk", "UA"));
        return collator.compare(city.getTitle(), city2.getTitle());
    }
}
