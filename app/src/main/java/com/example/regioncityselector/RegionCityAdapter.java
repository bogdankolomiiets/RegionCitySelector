package com.example.regioncityselector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.regioncityselector.models.City;
import com.example.regioncityselector.models.Region;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.regex.Pattern;

public class RegionCityAdapter extends BaseAdapter implements Filterable {
    private static final int HEADER_TYPE = 0;
    private static final int ITEM_TYPE = 1;
    private int viewType;
    private Filter mFilter;
    private List<Object> notFilteredData = new ArrayList<>();
    private List<Object> filteredData = new ArrayList<>();
    private Set<Region> lastSelectedRegions;
    private List<Integer> lastSelectedIds = new ArrayList<>();
    private LayoutInflater mInflater;

    RegionCityAdapter(Context context, List<Object> regionCityData, Set<Region> lastSelectedRegions, int viewType) {
        notFilteredData.addAll(regionCityData);
        filteredData.addAll(regionCityData);
        this.viewType = viewType;
        this.lastSelectedRegions = lastSelectedRegions;
        mInflater = LayoutInflater.from(context);
        mFilter = new ItemFilter();

        for (Region region : lastSelectedRegions) {
            lastSelectedIds.add(region.getId());
            List<City> cityList = region.getCityList();
            if (cityList != null) {
                for (City city : cityList) {
                    lastSelectedIds.add(city.getId());
                }
            }
        }
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (viewType == MainActivity.REGION_VIEW) {
            view = mInflater.inflate(R.layout.region_city_item, viewGroup, false);
            Region region = (Region) filteredData.get(position);
            ItemViewHolder itemViewHolder = new ItemViewHolder();
            itemViewHolder.regionCityCheck = view.findViewById(R.id.region_city_check);
            itemViewHolder.regionCityCheck.setChecked(lastSelectedIds.contains(region.getId()));
            itemViewHolder.regionCityCheck.setOnCheckedChangeListener((compoundButton, checked) -> {
                if (checked) {
                    lastSelectedIds.add(region.getId());
                    lastSelectedRegions.add(region);
                } else {
                    lastSelectedIds.remove((Integer) region.getId());
                    lastSelectedRegions.remove(region);
                }
            });
            itemViewHolder.regionCityCheck.setText(region.getName());
            view.setTag(itemViewHolder);
        } else {
            if (getItemViewType(position) == ITEM_TYPE) {
                view = mInflater.inflate(R.layout.region_city_item, viewGroup, false);
                ItemViewHolder itemViewHolder = new ItemViewHolder();
                itemViewHolder.regionCityCheck = view.findViewById(R.id.region_city_check);
                City city = (City) filteredData.get(position);
                itemViewHolder.regionCityCheck.setChecked(lastSelectedIds.contains(city.getId()));
                itemViewHolder.regionCityCheck.setOnCheckedChangeListener((compoundButton, checked) -> {
                    if (checked) {
                        lastSelectedIds.add(city.getId());
                        ListIterator listIterator = filteredData.listIterator(position);
                        while (listIterator.hasPrevious()) {
                            Object object = listIterator.previous();
                            if (object instanceof Region) {
                                for (Region region : lastSelectedRegions) {
                                    if (region.equals(object)) {
                                        region.getCityList().add(city);
                                        Collections.sort(region.getCityList(), new UkrainianCityComparator());
                                        return;
                                    }
                                }
                                break;
                            }
                        }
                    } else {
                        lastSelectedIds.remove((Integer) city.getId());
                        ListIterator listIterator = filteredData.listIterator(position);
                        while (listIterator.hasPrevious()) {
                            Object object = listIterator.previous();
                            if (object instanceof Region) {
                                for (Region region : lastSelectedRegions) {
                                    if (region.equals(object)) {
                                        region.getCityList().remove(city);
                                        return;
                                    }
                                }
                                break;
                            }
                        }
                    }
                });
                itemViewHolder.regionCityCheck.setText(city.getTitle());
                view.setTag(itemViewHolder);
            } else {
                view = mInflater.inflate(R.layout.region_city_header, viewGroup, false);
                HeaderViewHolder headerViewHolder = new HeaderViewHolder();
                headerViewHolder.title = view.findViewById(R.id.header_title);
                Region region = (Region) filteredData.get(position);
                headerViewHolder.title.setText(region.getName());
                view.setTag(headerViewHolder);
            }
        }

        return view;
    }

    static class HeaderViewHolder {
        TextView title;
    }

    static class ItemViewHolder {
        CheckBox regionCityCheck;
    }

    @Override
    public int getItemViewType(int position) {
        if (viewType == MainActivity.REGION_VIEW) {
            return ITEM_TYPE;
        } else {
            if (filteredData.get(position) instanceof Region) {
                return HEADER_TYPE;
            } else return ITEM_TYPE;
        }
    }

    @Override
    public int getViewTypeCount() {
        if (viewType == MainActivity.REGION_VIEW) {
            return 1;
        } else return 2;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            int count = notFilteredData.size();
            List<Object> tempData = new ArrayList<>(count);
            Pattern pattern = Pattern.compile(".*" + charSequence.toString().toLowerCase() + ".*");

            FilterResults results = new FilterResults();

            switch (viewType) {
                case MainActivity.REGION_VIEW:
                    for (Object region : notFilteredData) {
                        if (pattern.matcher(((Region) region).getName().toLowerCase()).matches()) {
                            tempData.add(region);
                        }
                    }
                    break;
                case MainActivity.CITY_VIEW:
                    for (Object object : notFilteredData) {
                        if (object instanceof Region) {
                            tempData.add(object);
                        } else if (pattern.matcher(((City) object).getTitle().toLowerCase()).matches()) {
                            tempData.add(object);
                        }
                    }

                    //removing region title if we don't found any city for region
                    List<Integer> integers = new ArrayList<>();
                    for (int i = 0; i < tempData.size(); i++) {
                        try {
                            if (tempData.get(i) instanceof Region && tempData.get(i + 1) instanceof Region) {
                                integers.add(i);
                            }
                        } catch (Exception e) {
                            /* stub */
                        }
                    }

                    for (int i = 0; i < integers.size(); i++) {
                        tempData.remove(integers.get(i).intValue());
                    }

                    //if last position in list is region title than we remove item
                    if (tempData.get(tempData.size() - 1) instanceof Region) {
                        tempData.remove(tempData.size() - 1);
                    }
                    break;
            }

            results.values = tempData;
            results.count = tempData.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            filteredData.clear();
            filteredData = (ArrayList<Object>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}
