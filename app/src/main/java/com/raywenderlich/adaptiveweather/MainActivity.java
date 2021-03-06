package com.raywenderlich.adaptiveweather;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Location> mLocations = new ArrayList<>();
    LocationAdapter mLocationAdapter;
    private static final String SELECTED_LOCATION_INDEX = "selectedLocationIndex";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setLayoutManager(layoutManager);

        loadData();

        mLocationAdapter = new LocationAdapter(this, mLocations, new LocationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Location location) {
                // TODO
                loadForecast(location.getForecast());
            }
        });
        mRecyclerView.setAdapter(mLocationAdapter);

        if (savedInstanceState != null) {
            int index = savedInstanceState.getInt(SELECTED_LOCATION_INDEX);
            mLocationAdapter.setSelectedLocationIndex(index);
            loadForecast(mLocations.get(index).getForecast());
        }
    }

    private void loadData() {
        String json = null;
        try {
            InputStream is = getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        JSONArray array = null;
        try {
            array = new JSONArray(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject object = (JSONObject) array.get(i);
                JSONArray stringArray = (JSONArray) object.get("forecast");
                List<String> forecast = new ArrayList<String>();
                for (int j = 0; j < stringArray.length(); j++) {
                    forecast.add(stringArray.getString(j));
                }
                Location location = new Location((String) object.get("name"), forecast);
                mLocations.add(location);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private Drawable mapWeatherToDrawable(String forecast) {
        int drawableId = 0;
        switch (forecast) {
            case "sun":
                drawableId = R.drawable.ic_sun;
                break;
            case "rain":
                drawableId = R.drawable.ic_rain;
                break;
            case "fog":
                drawableId = R.drawable.ic_fog;
                break;
            case "thunder":
                drawableId = R.drawable.ic_thunder;
                break;
            case "cloud":
                drawableId = R.drawable.ic_cloud;
                break;
            case "snow":
                drawableId = R.drawable.ic_snow;
                break;
        }
        return getResources().getDrawable(drawableId);
    }

    private void loadForecast(List<String> forecast) {
        FlexboxLayout forecastView = (FlexboxLayout) findViewById(R.id.forecast);
        for (int i = 0; i < forecastView.getChildCount(); i++) {
            AppCompatImageView dayView = (AppCompatImageView) forecastView.getChildAt(i);
            dayView.setImageDrawable(mapWeatherToDrawable(forecast.get(i)));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_LOCATION_INDEX, mLocationAdapter.getSelectedLocationIndex());
    }
}
