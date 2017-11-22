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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Esteban on 11/21/2017.
 */

public class CombosActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavView;
    private ProgressBar mLoadingIndicator;
    private BottomNavigationView navigation;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combos);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.combos_loading_indicator);
        makeBottomNav();

        requestQueue = Volley.newRequestQueue(this);
        String url = "http://10.0.2.2:8080/api/routines";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        try {
                            JSONObject obj = new JSONObject(response);
                            Combo[] comboArray = createCombosArray(obj);
                            CombosArrayAdapter comboArrayAdapter = new CombosArrayAdapter(CombosActivity.this, comboArray);
                            ListView listView = (ListView) findViewById(R.id.combos_list_view);
                            if (listView != null) {
                                listView.setAdapter(comboArrayAdapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        startComboActivity(CombosActivity.this, (Combo) parent.getItemAtPosition(position));
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
                Toast.makeText(CombosActivity.this, "error", Toast.LENGTH_LONG).show();
            }
        });

        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    private void showMessage(String s) {
        Toast.makeText(CombosActivity.this,s, Toast.LENGTH_SHORT).show();
    }

    private class ComboQueryTask extends AsyncTask<URL, Void, String> {

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
                    Combo[] comboArray = createCombosArray(obj);
                    CombosArrayAdapter comboArrayAdapter = new CombosArrayAdapter(CombosActivity.this, comboArray);
                    ListView listView = (ListView) findViewById(R.id.combos_list_view);
                    if (listView != null) {
                        listView.setAdapter(comboArrayAdapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                startComboActivity(CombosActivity.this, (Combo) parent.getItemAtPosition(position));
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

    public void startComboActivity(Context context, Combo combo){
        Intent intent = new Intent( context, ComboActivity.class);
        intent.putExtra("name", combo.getName());
        intent.putExtra("id", combo.getId());
        startActivity(intent);
    }

    public Combo[] createCombosArray(JSONObject obj) throws JSONException {
        JSONArray array = obj.getJSONArray("routines");
        //Toast.makeText(CombosActivity.this,array.toString(), Toast.LENGTH_LONG).show();
        ArrayList<Combo> combos = new ArrayList<>();
        for (int i = 0, length = array.length(); i < length; i++) {
            JSONObject json = array.getJSONObject(i);
            combos.add(new Combo(json.getString("id"), json.getString("name")));
        }
        return combos.toArray(new Combo[combos.size()]);
    }

    private void makeBottomNav() {
        bottomNavView = (BottomNavigationView) findViewById(R.id.combos_navigation);

        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch(item.getItemId()){
                    case R.id.navigation_rooms:
                        intent = new Intent(CombosActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_devices:
                        intent = new Intent(CombosActivity.this, AllDevicesActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_combo:
                        intent = new Intent(CombosActivity.this, CombosActivity.class);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
    }
}