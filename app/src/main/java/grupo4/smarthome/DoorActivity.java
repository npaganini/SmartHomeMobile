package grupo4.smarthome;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Created by estebankramer on 18/11/2017.
 */

public class DoorActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavView;
    private RequestQueue requestQueue;
    private Resources res;
    private TextView doorState;
    private Switch openSwitch;
    private Switch lockSwitch;
    private boolean isOpened=false;
    private boolean isLocked=false;
    private String deviceID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door);
        makeBottomNav();
        deviceID = getIntent().getStringExtra("id");
        doorState = (TextView) findViewById(R.id.door_state);
        openSwitch = (Switch) findViewById(R.id.open);
        lockSwitch = (Switch) findViewById(R.id.lock);

        res = getResources();


        String url = "http://10.0.2.2:8080/api/devices/"+ deviceID +"/getState";
        requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,responseListener(), errorListener());
        requestQueue.add(stringRequest);

        openSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                openSwitchPutRequest(openSwitch);
                isOpened=!isOpened;
                setStateAndSwitched(isLocked, isOpened);

            }
        });

        lockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lockSwitchPutRequest(lockSwitch);
                isLocked=!isLocked;
                setStateAndSwitched(isLocked, isOpened);

            }
        });

    }

    public Response.Listener<String> responseListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the response string.
                try {
                    //showMessage(response);
                    JSONObject obj = new JSONObject(response);
                    doWithState(obj);

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
                Toast.makeText(DoorActivity.this, "error", Toast.LENGTH_LONG).show();
            }
        };
    }

    public void doWithState(JSONObject obj) throws JSONException {

        obj = obj.getJSONObject("result");
        String open = obj.getString("status");
        String locked = obj.getString("lock");
        if(open.equals("closed") && locked.equals("locked")){
            doorState.setText(res.getString(R.string.closedAndLocked));
            lockSwitch.setChecked(true);
            openSwitch.setChecked(true);
            isOpened=true;
            isLocked=true;
        }
        else if (open.equals("closed") && locked.equals("unlocked")){
            doorState.setText(res.getString(R.string.closedAndUnLocked));
            openSwitch.setChecked(true);
            lockSwitch.setChecked(false);
            isOpened=true;
            isLocked=false;
        }
        else if (open.equals("opened") && locked.equals("unlocked")){
            doorState.setText(res.getString(R.string.openedAndUnlocked));
            openSwitch.setChecked(false);
            lockSwitch.setChecked(false);
            isOpened=false;
            isLocked=false;
        }
        else{
            doorState.setText(res.getString(R.string.openedAndLocked));
            openSwitch.setChecked(false);
            lockSwitch.setChecked(true);
            isOpened=false;
            isLocked=true;
        }
    };

    private void showMessage(String s) {
        Toast.makeText(DoorActivity.this, s, Toast.LENGTH_LONG).show();
    }

    public void setStateAndSwitched(boolean isLocked, boolean isOpened){
        if(isOpened && isLocked){
            doorState.setText(res.getString(R.string.closedAndLocked));
        }
        else if (!isOpened && !isLocked){
            doorState.setText(res.getString(R.string.closedAndUnLocked));
        }
        else if (isOpened && !isLocked){
            doorState.setText(res.getString(R.string.openedAndUnlocked));
        }
        else{
            doorState.setText(res.getString(R.string.openedAndLocked));
        }
    }

        public void lockSwitchPutRequest(Switch s){
            String url;
            if(s.isChecked()){
                url = "http://10.0.2.2:8080/api/devices/"+ deviceID +"/lock";
            }
            else{
                url = "http://10.0.2.2:8080/api/devices/"+ deviceID +"/unlock";
            }
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,responseListener(), errorListener());

            // Add the request to the RequestQueue.
            requestQueue.add(stringRequest);
        }

        public void openSwitchPutRequest(Switch s){
            String url;
            if(!s.isChecked()){
                url = "http://10.0.2.2:8080/api/devices/"+ deviceID +"/open";
            }
            else{
                url = "http://10.0.2.2:8080/api/devices/"+ deviceID +"/close";
            }
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,responseListener(), errorListener());

            // Add the request to the RequestQueue.
            requestQueue.add(stringRequest);

        }

    private void makeBottomNav() {
        bottomNavView = (BottomNavigationView) findViewById(R.id.door_navigation);

        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch(item.getItemId()){
                    case R.id.navigation_rooms:
                        intent = new Intent(    DoorActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_devices:
                        intent = new Intent(DoorActivity.this, AllDevicesActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_combo:
                        intent = new Intent(DoorActivity.this, CombosActivity.class);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
    }
}