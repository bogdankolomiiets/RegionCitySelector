package com.example.regioncityselector;

import com.example.regioncityselector.models.Region;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Repository {
    private JSONArray regionArray;
    private JSONObject regionCityJsonObject;

    {
        try {
            regionArray = new JSONArray("[{\"name\":\"Київська\",\"value\":10},{\"name\":\"Вінницька\",\"value\":1},{\"name\":\"Волинська\",\"value\":18},{\"name\":\"Дніпропетровська\",\"value\":11},{\"name\":\"Донецька\",\"value\":13},{\"name\":\"Житомирська\",\"value\":2},{\"name\":\"Закарпатська\",\"value\":22},{\"name\":\"Запорізька\",\"value\":14},{\"name\":\"Івано-Франківська\",\"value\":15},{\"name\":\"Кіровоградська\",\"value\":16},{\"name\":\"Луганська\",\"value\":17},{\"name\":\"Львівська\",\"value\":5},{\"name\":\"Миколаївська\",\"value\":19},{\"name\":\"Одеська\",\"value\":12},{\"name\":\"Полтавська\",\"value\":20},{\"name\":\"Рівненська\",\"value\":9},{\"name\":\"Сумська\",\"value\":8},{\"name\":\"Тернопільська\",\"value\":3},{\"name\":\"Харківська\",\"value\":7},{\"name\":\"Херсонська\",\"value\":23},{\"name\":\"Хмельницька\",\"value\":4},{\"name\":\"Черкаська\",\"value\":24},{\"name\":\"Чернівецька\",\"value\":25},{\"name\":\"Чернігівська\",\"value\":6}]");
            regionCityJsonObject = new JSONObject("{\"citiesByStateId\":[{\"stateId\":1,\"stateName\":\"Вінницька\",\"citiesArray\":[{\"id\":111,\"name\":\"Вінниця\"},{\"id\":27,\"name\":\"Жмеринка\"},{\"id\":30,\"name\":\"Козятин\"},{\"id\":31,\"name\":\"Крижопіль\"},{\"id\":32,\"name\":\"Липовець\"},{\"id\":33,\"name\":\"Літин\"},{\"id\":34,\"name\":\"Могилів-Подільський\"},{\"id\":35,\"name\":\"Муровані Курилівці\"},{\"id\":36,\"name\":\"Немирів\"},{\"id\":37,\"name\":\"Оратів\"},{\"id\":38,\"name\":\"Піщанка\"},{\"id\":39,\"name\":\"Погребище\"},{\"id\":40,\"name\":\"Теплик\"},{\"id\":41,\"name\":\"Тиврів\"},{\"id\":42,\"name\":\"Томашпіль\"},{\"id\":43,\"name\":\"Тростянець\"},{\"id\":44,\"name\":\"Тульчин\"},{\"id\":45,\"name\":\"Хмільник\"},{\"id\":46,\"name\":\"Чернівці\"},{\"id\":47,\"name\":\"Чечельник\"},{\"id\":48,\"name\":\"Шаргород\"},{\"id\":49,\"name\":\"Ямпіль\"},{\"id\":597,\"name\":\"Бар\"},{\"id\":599,\"name\":\"Бершадь\"},{\"id\":602,\"name\":\"Гайсин\"},{\"id\":603,\"name\":\"Іллінці\"},{\"id\":604,\"name\":\"Калинівка\"},{\"id\":609,\"name\":\"Гнівань\"},{\"id\":644,\"name\":\"Ладижин\"},{\"id\":1595,\"name\":\"Якушинці\"}]},{\"stateId\":18,\"stateName\":\"Волинська\",\"citiesArray\":[{\"id\":18,\"name\":\"Луцьк\"},{\"id\":50,\"name\":\"Володимир-Волинський\"},{\"id\":51,\"name\":\"Горохів\"},{\"id\":52,\"name\":\"Іваничі\"},{\"id\":53,\"name\":\"Камінь-Каширський\"},{\"id\":54,\"name\":\"Ківерці\"},{\"id\":55,\"name\":\"Ковель\"},{\"id\":56,\"name\":\"Локачі\"},{\"id\":59,\"name\":\"Любешів\"},{\"id\":60,\"name\":\"Любомль\"},{\"id\":61,\"name\":\"Маневичі\"},{\"id\":62,\"name\":\"Нововолинськ\"},{\"id\":63,\"name\":\"Ратне\"},{\"id\":64,\"name\":\"Рожище\"},{\"id\":65,\"name\":\"Стара Вижівка\"},{\"id\":66,\"name\":\"Турійськ\"},{\"id\":67,\"name\":\"Шацьк\"},{\"id\":14945,\"name\":\"Берестечко\"}]}]}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    List<Region> getRegionList() {
        List<Region> regionList = new ArrayList<>();
        for (int i = 0; i < regionArray.length(); i++) {
            try {
                JSONObject object = new JSONObject(regionArray.get(i).toString());
                regionList.add(new Region(object.optInt("value"), object.optString("name")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(regionList, new UkrainianRegionComparator());
        return regionList;
    }

    List<Region> getRegionCityList() {
        List<Region> regionList = new ArrayList<>();
        Gson gson = new GsonBuilder().create();
        try {
            JSONArray regionCityArr = regionCityJsonObject.getJSONArray("citiesByStateId");
            for (int i = 0; i < regionCityArr.length(); i++) {
                Region region = gson.fromJson(regionCityArr.get(i).toString(), Region.class);
                Collections.sort(region.getCityList(), new UkrainianCityComparator());
                regionList.add(region);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return regionList;
    }

}
