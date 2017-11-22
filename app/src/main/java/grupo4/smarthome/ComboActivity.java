package grupo4.smarthome;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

import java.util.ArrayList;

/**
 * Created by Esteban on 11/19/2017.
 */

public class ComboActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavView;
    private RequestQueue requestQueue;
    private String comboId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combo);
        makeBottomNav();
//        Toast.makeText(this, getIntent().getStringExtra("name") + getIntent().getStringExtra("id"), Toast.LENGTH_SHORT).show();
        this.setTitle(getIntent().getStringExtra("name"));
        comboId = getIntent().getStringExtra("id");

        String url = "http://10.0.2.2:8080/api/routines/" + comboId;
//        Toast.makeText(ComboActivity.this, url, Toast.LENGTH_SHORT).show();

        requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,responseListener(), errorListener());
        requestQueue.add(stringRequest);

//        try {
//            JSONObject obj = new JSONObject(json);
//            JSONArray array = obj.getJSONArray("devices");
//
//            ArrayList<Device> device = new ArrayList<>();
//            for(int i=0, length=array.length(); i< length ; i++){
//                JSONObject json = array.getJSONObject(i);
//                device.add(new Device(json.getString("id"), json.getString("name"), json.getString("typeId"), "off"));
//            }
//            Device[] deviceArray = device.toArray(new Device[device.size()]);
//
//            DeviceArrayAdapter deviceArrayAdapter = new DeviceArrayAdapter(this, deviceArray);
//            ListView listView = (ListView) findViewById(R.id.combo_list);
//            if (listView != null) {
//                listView.setAdapter(deviceArrayAdapter);
//                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        Toast.makeText(ComboActivity.this, ((Device) parent.getItemAtPosition(position)).getName(), Toast.LENGTH_SHORT).show();
//                        //startDeviceActivity((Device) parent.getItemAtPosition(position));
//                    }
//                });
//
//
//            }
//            // Toast.makeText(this, roomList.toString() , Toast.LENGTH_LONG).show();
//        } catch (JSONException exception) {
//            Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
//        }

    }



    public Response.Listener<String> responseListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the response string.
                try {
                    JSONObject obj = new JSONObject(response);
                    Action[] actionsArray = createActionsArray(obj);
                    ActionArrayAdapter actionArrayAdapter = new ActionArrayAdapter(ComboActivity.this, actionsArray);
                    Toast.makeText(ComboActivity.this, "hola", Toast.LENGTH_LONG).show();
                    ListView listView = (ListView) findViewById(R.id.combo_list_view);
                    if (listView != null) {
                        listView.setAdapter(actionArrayAdapter);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public Response.ErrorListener errorListener(){
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ComboActivity.this, "error", Toast.LENGTH_LONG).show();
            }
        };
    }

    public Action[] createActionsArray(JSONObject obj) throws JSONException {
        obj = obj.getJSONObject("routine");
        JSONArray array = obj.getJSONArray("actions");
        ArrayList<Action> actions = new ArrayList<>();
        for (int i = 0, length = array.length(); i < length; i++) {
            JSONObject json = array.getJSONObject(i);
            //Toast.makeText(ComboActivity.this, json.toString(), Toast.LENGTH_LONG).show();
            actions.add(new Action(json.getString("deviceId"), json.getString("actionName")));
        }
        return actions.toArray(new Action[actions.size()]);
    }

    private void makeBottomNav() {
        bottomNavView = (BottomNavigationView) findViewById(R.id.combo_navigation);

        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch(item.getItemId()){
                    case R.id.navigation_rooms:
                        intent = new Intent(ComboActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_devices:
                        intent = new Intent(ComboActivity.this, AllDevicesActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_combo:
                        intent = new Intent(ComboActivity.this, CombosActivity.class);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
    }
}
