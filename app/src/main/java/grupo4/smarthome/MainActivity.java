package grupo4.smarthome;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavView;
    private ProgressBar mLoadingIndicator;
    private BottomNavigationView navigation;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
       // makeSearchQuery();
        makeBottomNav();

        requestQueue = Volley.newRequestQueue(this);
        String url = "http://10.0.2.2:8080/api/rooms";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        try {
                            JSONObject obj = new JSONObject(response);
                            Room[] roomArray = createRoomsArray(obj);
                            RoomArrayAdapter roomArrayAdapter = new RoomArrayAdapter(MainActivity.this, roomArray);
                            ListView listView = (ListView) findViewById(R.id.list_view);
                            if (listView != null) {
                                listView.setAdapter(roomArrayAdapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        startDevicesActivity(MainActivity.this, (Room) parent.getItemAtPosition(position));
                                    }
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_LONG).show();
            }
        });

        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }


    private String makeSearchQuery() {
        String[] append = {"rooms"};
        URL apiSearchUrl = NetworkUtils.buildUrl(append);
        new RoomsQueryTask().execute(apiSearchUrl);
        return apiSearchUrl.toString();
    }

    private void showMessage(String s) {
        Toast.makeText(MainActivity.this,s, Toast.LENGTH_SHORT).show();
    }

    private class RoomsQueryTask extends AsyncTask<URL, Void, String> {

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
                    Room[] roomArray = createRoomsArray(obj);
                    RoomArrayAdapter roomArrayAdapter = new RoomArrayAdapter(MainActivity.this, roomArray);
                    ListView listView = (ListView) findViewById(R.id.list_view);
                    if (listView != null) {
                        listView.setAdapter(roomArrayAdapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                startDevicesActivity(MainActivity.this, (Room) parent.getItemAtPosition(position));
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

    public void startDevicesActivity(Context context, Room room){
        Intent intent = new Intent( context, DevicesActivity.class);
        intent.putExtra("name", room.getName());
        intent.putExtra("id", room.getId());
        startActivity(intent);
    }

    public Room[] createRoomsArray(JSONObject obj) throws JSONException {
        JSONArray array = obj.getJSONArray("rooms");

        ArrayList<Room> rooms = new ArrayList<>();
        for (int i = 0, length = array.length(); i < length; i++) {
            JSONObject json = array.getJSONObject(i);
            rooms.add(new Room(json.getString("id"), json.getString("name")));
        }
        return rooms.toArray(new Room[rooms.size()]);
    }

    private void makeBottomNav() {
        bottomNavView = (BottomNavigationView) findViewById(R.id.main_navigation);

        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch(item.getItemId()){
                    case R.id.navigation_rooms:
                        intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_devices:
                        intent = new Intent(MainActivity.this, AllDevicesActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_combo:
                        intent = new Intent(MainActivity.this, CombosActivity.class);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
    }
}