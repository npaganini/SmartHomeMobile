package grupo4.smarthome;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;

/**
 * Created by estebankramer on 18/11/2017.
 */

public class AcActivity extends AppCompatActivity {

    private final static int MIN_TEMP = 18;
    private final static int MAX_TEMP = 38;

    private BottomNavigationView bottomNavView;
    private TextView acName;
    private Switch acSwitch;
    private SeekBar temperatureSeekBar;
    private TextView temperatureTextView;
    private Spinner modeSpinner;
    private Spinner verticalSpinner;
    private Spinner horizontalSpinner;
    private Spinner fanSpinner;
    private RequestQueue requestQueue;
    private String deviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ac);
        makeBottomNav();
        acName = (TextView) findViewById(R.id.ac_name);
        acSwitch = (Switch) findViewById(R.id.ac_switch);
        temperatureSeekBar = (SeekBar) findViewById(R.id.temperature_seekBar);
        temperatureTextView = (TextView) findViewById(R.id.temperatureLevel_textView);
        modeSpinner = (Spinner) findViewById(R.id.mode_spinner);
        verticalSpinner = (Spinner) findViewById(R.id.verticalSwing_spinner);
        horizontalSpinner = (Spinner) findViewById(R.id.horizontalSwing_spinner);
        fanSpinner = (Spinner) findViewById(R.id.speed_spinner);

        acName.setText(getIntent().getStringExtra("name"));

        deviceID = getIntent().getStringExtra("id");

        switchPutRequest(acSwitch);

        modePutRequest();
        verticalPutRequest();


        String url = "http://10.0.2.2:8080/api/devices/"+ deviceID +"/getState";
        requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,responseListener(), errorListener());
        requestQueue.add(stringRequest);

        temperatureSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                makePostRequest(deviceID,"setTemperature", getTemperature());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub
                temperatureTextView.setText(" " + getTemperature());

            }
        });
    }

    private void showMessage(String s) {
        Toast.makeText(AcActivity.this, s, Toast.LENGTH_LONG).show();
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
                Toast.makeText(AcActivity.this, "error", Toast.LENGTH_LONG).show();
            }
        };
    }

    public void doWithState(JSONObject obj) throws JSONException {
        obj = obj.getJSONObject("result");
        String auxS = obj.getString("status");
        acSwitch.setChecked(auxS.equals("on"));
        int auxI = obj.getInt("temperature") - MIN_TEMP;
        temperatureSeekBar.setProgress(auxI*100/(MAX_TEMP-MIN_TEMP));
        temperatureTextView.setText(" " + getTemperature());
        auxS = obj.getString("mode");
        //showMessage(auxS);
        setModeSpinner(auxS);
        auxS = obj.getString("verticalSwing");
        setVerticalSpinner(auxS);
        auxS = obj.getString("horizontalSwing");
        setHorizontalSpinner(auxS);
        auxS = obj.getString("fanSpeed");
        setFanSpinner(auxS);
    };

    public String getTemperature () {
        Integer temperature = ((temperatureSeekBar.getProgress()*(MAX_TEMP-MIN_TEMP))/100) + MIN_TEMP;
        return temperature.toString();
    }

    public void setModeSpinner(String option){

        switch(option){
            case "cool":
                modeSpinner.setSelection(0);
            case "heat":
                modeSpinner.setSelection(1);
            case "fan":
                modeSpinner.setSelection(2);
        }
    }

    public String getModeSpinnerSelection(int option){

        switch(option){
            case 0:
                return "cool";
            case 1:
                return "heat";
            case 2:
                return "fan";
        }
        return "cool";
    }

    public void setVerticalSpinner(String option){

        switch(option){
            case "Auto":
                modeSpinner.setSelection(0);
            case "22":
                modeSpinner.setSelection(1);
            case "45":
                modeSpinner.setSelection(2);
            case "67":
                modeSpinner.setSelection(3);
            case "90":
                modeSpinner.setSelection(4);
        }
    }

    public String getVerticalSpinnerSelection(int option){

        switch(option){
            case 0:
                return "auto";
            case 1:
                return "22";
            case 2:
                return "45";
            case 3:
                return "67";
            case 4:
                return "90";
        }
        return "auto";
    }

    public void setHorizontalSpinner(String option){

        switch(option){
            case "Auto":
                modeSpinner.setSelection(0);
            case "-90":
                modeSpinner.setSelection(1);
            case "-45":
                modeSpinner.setSelection(2);
            case "0":
                modeSpinner.setSelection(3);
            case "45":
                modeSpinner.setSelection(4);
            case "90":
                modeSpinner.setSelection(5);
        }
    }

    public void setFanSpinner(String option){
        switch(option){
            case "Auto":
                modeSpinner.setSelection(0);
            case "25":
                modeSpinner.setSelection(1);
            case "50":
                modeSpinner.setSelection(2);
            case "75":
                modeSpinner.setSelection(3);
            case "100":
                modeSpinner.setSelection(4);
        }
    }

    public void makePostRequest(String deviceID, String action, String parameter){
        String URL = "http://10.0.2.2:8080/api/devices/" + deviceID + "/" + action;
        final String requestBody;
        if(parameter!= null){
            requestBody = "[\"" + parameter + "\"]";
        } else{
            requestBody ="";
        }
        StringRequest stringPostRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("VOLLEY ONRESPONSE", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY ERROR", error.toString());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                    // can get more details such as response.headers
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        requestQueue.add(stringPostRequest);
    }

    public void switchPutRequest(Switch s){
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String url;
                if(!isChecked){
                    url = "http://10.0.2.2:8080/api/devices/"+ deviceID +"/turnOff";
                }
                else{
                    url = "http://10.0.2.2:8080/api/devices/"+ deviceID +"/turnOn";
                }
                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,responseListener(), errorListener());

                // Add the request to the RequestQueue.
                requestQueue.add(stringRequest);
            }
        });
    }

    public void modePutRequest(){
        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                makePostRequest(deviceID, "setMode", getModeSpinnerSelection(i));
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
    }

    public void verticalPutRequest(){
        verticalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                makePostRequest(deviceID, "setVerticalSwing", getVerticalSpinnerSelection(i));
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
    }

    private void makeBottomNav() {
        bottomNavView = (BottomNavigationView) findViewById(R.id.ac_navigation);

        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch(item.getItemId()){
                    case R.id.navigation_rooms:
                        intent = new Intent(AcActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_devices:
                        intent = new Intent(AcActivity.this, AllDevicesActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_combo:
                        intent = new Intent(AcActivity.this, CombosActivity.class);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
    }
}
