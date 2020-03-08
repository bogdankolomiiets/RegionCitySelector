package com.example.regioncityselector;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.regioncityselector.models.City;
import com.example.regioncityselector.models.Region;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity implements RegionCityHandler {
    public static final int REGION_VIEW = 1;
    public static final int CITY_VIEW = 2;
    private final String lastSelectedRegionsJsonObj = "lastSelectedRegionsSet";
    private final String regionCityDivider = ", ";
    private final int regionCityDividerLength = regionCityDivider.length();
    private TextView regionTv;
    private TextView cityTv;
    private AlertDialog dialog;
    private Repository repository;
    private Set<Region> lastSelectedRegions;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        repository = new Repository();
        preferences = getSharedPreferences("LastSelectedRegions", MODE_PRIVATE);
        String regionCitySetFromPref = preferences.getString(lastSelectedRegionsJsonObj, "");

        //init last selected regions and cities
        lastSelectedRegions = new TreeSet<>(new UkrainianRegionComparator());
        try {
            JSONArray jsonArray = new JSONArray(regionCitySetFromPref);
            Gson gson = new GsonBuilder().create();
            for (int i = 0; i < jsonArray.length(); i++) {
                lastSelectedRegions.add(gson.fromJson(jsonArray.get(i).toString(), Region.class));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        regionTv = findViewById(R.id.region_tv);
        regionTv.setOnClickListener(view -> showRegionCity(repository.getRegionList(), REGION_VIEW));

        cityTv = findViewById(R.id.city_tv);
        cityTv.setOnClickListener(view -> {
            if (lastSelectedRegions.size() > 0) {
                showRegionCity(repository.getRegionCityList(), CITY_VIEW);
            } else Toast.makeText(this, R.string.region_first, Toast.LENGTH_SHORT).show();
        });

        makeTitlesForRegionAndCity(regionTv, cityTv);
    }

    private void makeTitlesForRegionAndCity(TextView regionTv, TextView cityTv) {
        StringBuilder regionTitleBuilder = new StringBuilder();
        StringBuilder cityTitleBuilder = new StringBuilder();
        for (Region region : lastSelectedRegions) {
            regionTitleBuilder.append(region.getName()).append(regionCityDivider);
            List<City> cityList = region.getCityList();
            if (cityList != null) {
                for (City city : cityList) {
                    cityTitleBuilder.append(city.getTitle()).append(regionCityDivider);
                }
            }
        }

        regionTv.setText(regionTitleBuilder.length() > 0 ? regionTitleBuilder.deleteCharAt(regionTitleBuilder.length() - regionCityDividerLength).toString() : getString(R.string.all_ukraine));
        cityTv.setText(cityTitleBuilder.length() > 0 ? cityTitleBuilder.deleteCharAt(cityTitleBuilder.length() - regionCityDividerLength).toString() : getString(R.string.all_cities));
    }

    @Override
    public void showRegionCity(List<Region> regionsDataForDialog, int regionCityType) {
        dialog = new AlertDialog.Builder(this).create();
        List<Object> regionCityArr = new ArrayList<>();
        View regionCitySelector = LayoutInflater.from(this).inflate(R.layout.region_city_selector, dialog.getListView(), false);
        TextView alertTitle = regionCitySelector.findViewById(R.id.alert_title);
        alertTitle.setText(regionCityType == REGION_VIEW ? R.string.region : R.string.city);
        ListView listView = regionCitySelector.findViewById(R.id.list_view);
        TextView cancelBtn = regionCitySelector.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(view -> dialog.dismiss());
        TextView okBtn = regionCitySelector.findViewById(R.id.ok_btn);
        okBtn.setOnClickListener(view -> {
            preferences.edit().putString(lastSelectedRegionsJsonObj, new Gson().toJson(lastSelectedRegions)).apply();
            makeTitlesForRegionAndCity(regionTv, cityTv);
            dialog.dismiss();
            makeRegionCitySearchString();
        });

        for (Region region : regionsDataForDialog) {
            regionCityArr.add(region.getJustRegion());
            List<City> cityList = region.getCityList();
            if (cityList != null) {
                regionCityArr.addAll(cityList);
            }
        }

        RegionCityAdapter regionCityAdapter = new RegionCityAdapter(this, regionCityArr, lastSelectedRegions, regionCityType);
        listView.setAdapter(regionCityAdapter);

        //setup searchView
        SearchView searchView = regionCitySelector.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                regionCityAdapter.getFilter().filter(s);
                return true;
            }
        });

        dialog.setView(regionCitySelector);
        dialog.show();
    }

    private void makeRegionCitySearchString() {
        int index = 0;
        StringBuilder builder = new StringBuilder();
        for (Region region : lastSelectedRegions) {
            List<City> cityList = region.getCityList();
            if (cityList.size() == 0) {
                builder.append("&m_state[").append(index++).append("]=").append(region.getId());
            } else {
                for (City city : cityList) {
                    builder.append("&m_state[").append(index).append("]=").append(region.getId());
                    builder.append("&m_city[").append(index++).append("]=").append(city.getId());
                }
            }
        }
        System.out.println("*****" + builder.toString());
    }
}