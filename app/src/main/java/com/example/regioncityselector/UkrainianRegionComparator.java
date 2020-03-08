package com.example.regioncityselector;

import com.example.regioncityselector.models.Region;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class UkrainianRegionComparator implements Comparator<Region> {
    @Override
    public int compare(Region region, Region region2) {
        Collator collator = Collator.getInstance(new Locale("uk", "UA"));
        return collator.compare(region.getName(), region2.getName());
    }
}
