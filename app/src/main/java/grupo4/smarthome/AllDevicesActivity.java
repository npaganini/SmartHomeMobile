package grupo4.smarthome;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by estebankramer on 17/11/2017.
 */
public class AllDevicesActivity extends AppCompatActivity{

    private BottomNavigationView bottomNavView;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_devices);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.devices_all_loading_indicator);
        makeBottomNav();

        this.setTitle("All Devices");
        makeSearchQuery();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startDeviceActivity(Device device){
        Class deviceClass = getDeviceClass(device.getTypeId());
        if(device != null) {
            Intent intent = new Intent(AllDevicesActivity.this, deviceClass);
            intent.putExtra("name", device.getName());
            intent.putExtra("id", device.getId());
            intent.putExtra("typeId", device.getTypeId());
            intent.putExtra("state", device.getState());
            startActivity(intent);
        } else{
            Toast.makeText(this, "Device Not Found", Toast.LENGTH_SHORT).show();
        }
    }

    public Class getDeviceClass(String typeId){
        switch(typeId){
            case "eu0v2xgprrhhg41g": //blind
                return BlindActivity.class;
            case "go46xmbqeomjrsjr": //lamp
                return LampActivity.class;
            case "li6cbv5sdlatti0j": //ac
                return AcActivity.class;
            case "lsf78ly0eqrjbz91": //door
                return DoorActivity.class;
            case "mxztsyjzsrq7iaqc": //alarm
                return AlarmActivity.class;
            case "ofglvd9gqX8yfl3l": //timer
                return TimerActivity.class;
        }
        return null;
    }

    private class DevicesQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String apiSearchResults = null;
            try {
                apiSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return apiSearchResults;
        }

        @Override
        protected void onPostExecute(String apiSearchResults) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (apiSearchResults != null && !apiSearchResults.equals("")) {
                try {
                    JSONObject obj = new JSONObject(apiSearchResults);
                    Device[] deviceArray = createDevicesArray(obj);
                    DeviceArrayAdapter deviceArrayAdapter = new DeviceArrayAdapter(AllDevicesActivity.this, deviceArray);
                    ListView listView = (ListView) findViewById(R.id.devices_all_list_view);

                    if (listView != null) {
                        listView.setAdapter(deviceArrayAdapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                startDeviceActivity((Device) parent.getItemAtPosition(position));
                            }
                        });
                    }



                } catch (JSONException exception) {
                    showMessage("error");
                }
            } else {
                showMessage("error");
            }
        }
    }


    public Device[] createDevicesArray(JSONObject obj) throws JSONException {
        JSONArray array = obj.getJSONArray("devices");
        ArrayList<Device> device = new ArrayList<>();
        for(int i=0, length=array.length(); i< length ; i++){
            JSONObject json = array.getJSONObject(i);
            device.add(new Device(json.getString("id"), json.getString("name"), json.getString("typeId"), "off"));
        }
        return device.toArray(new Device[device.size()]);
    }


    private void showMessage(String s) {
        Toast.makeText(AllDevicesActivity.this, s, Toast.LENGTH_LONG).show();
    }

    private String makeSearchQuery() {
        String[] append = {"devices"};
        URL apiSearchUrl = NetworkUtils.buildUrl( append );
        new AllDevicesActivity.DevicesQueryTask().execute(apiSearchUrl);
        return apiSearchUrl.toString();
    }



    private class StateQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String apiSearchResults = null;
            try {
                apiSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return apiSearchResults;
        }

        @Override
        protected void onPostExecute(String apiSearchResults) {
            if (apiSearchResults != null && !apiSearchResults.equals("")) {
                try {
                    JSONObject obj = new JSONObject(apiSearchResults);

                } catch (JSONException exception) {

                }
            } else {
            }
        }
    }

    private void makeBottomNav() {
        bottomNavView = (BottomNavigationView) findViewById(R.id.devices_all_navigation);

        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch(item.getItemId()){
                    case R.id.navigation_rooms:
                        intent = new Intent(AllDevicesActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_devices:
                        intent = new Intent(AllDevicesActivity.this, AllDevicesActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_combo:
                        intent = new Intent(AllDevicesActivity.this, CombosActivity.class);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
    }
}