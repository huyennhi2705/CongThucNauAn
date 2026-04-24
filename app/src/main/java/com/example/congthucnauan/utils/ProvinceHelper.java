package com.example.congthucnauan.utils;

import android.content.Context;

import com.example.congthucnauan.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ProvinceHelper {
    private final LinkedHashMap<String, List<String>> provincesDistrictMap = new LinkedHashMap<>();

    public ProvinceHelper(Context context) {
        loadProvincesFromRaw(context);
    }

    private String readJsonFromRaw(Context context, int resId) {
        try {
            InputStream is = context.getResources().openRawResource(resId);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadProvincesFromRaw(Context context) {
        try {
            String json = readJsonFromRaw(context, R.raw.provinces);
            if (json == null) return;

            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject province = array.getJSONObject(i);
                String name = province.getString("name");
                JSONArray districtArray = province.getJSONArray("district");
                
                List<String> districts = new ArrayList<>();
                for (int j = 0; j < districtArray.length(); j++) {
                    districts.add(districtArray.getString(j));
                }
                provincesDistrictMap.put(name, districts);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<String> getProvinceNames() {
        return new ArrayList<>(provincesDistrictMap.keySet());
    }

    public List<String> getDistricts(String provinceName) {
        List<String> districts = provincesDistrictMap.get(provinceName);
        return districts != null ? districts : new ArrayList<>();
    }
}
