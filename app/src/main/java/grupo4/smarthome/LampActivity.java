package grupo4.smarthome;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
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
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextClock;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

/**
 * Created by estebankramer on 18/11/2017.
 */

public class LampActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavView;
    private SeekBar brightnessSeekBar;
    private TextView brightnessTextView;
    private TextView colorTextView;
    private Button [] buttons;
    private Switch lampSwitch;
    private RequestQueue requestQueue;
    private String deviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lamp);
        makeBottomNav();
        this.setTitle(getIntent().getStringExtra("name"));
        TextView name = (TextView) findViewById(R.id.lamp_name);
        name.setText(getIntent().getStringExtra("name"));
        brightnessSeekBar = (SeekBar) findViewById(R.id.brigthness_seekBar);
        brightnessTextView =  (TextView) findViewById(R.id.brightnessLevel_textView);
        colorTextView = (TextView) findViewById(R.id.colorPicked_textView);
        lampSwitch = (Switch) findViewById(R.id.lamp_switch);
        //makeStateSearchQuery(getIntent().getStringExtra("id"));

       deviceID = getIntent().getStringExtra("id");

        String url = "http://10.0.2.2:8080/api/devices/"+ deviceID +"/getState";
        requestQueue = Volley.newRequestQueue(this);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,responseListener(), errorListener());

        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
        switchPutRequest(lampSwitch);
        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brightnessTextView.setText(" " + new Integer (progress).toString());
                // Send brightness to api
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                makePostRequest(deviceID,"changeBrightness", Integer.valueOf((seekBar.getProgress())).toString());
            }
        });
        Resources res = getResources();
        buttons = new Button[4];
        int [] ids = {R.id.redButton,R.id.blueButton,R.id.greenButton,R.id.yellowButton};
        int [] drawId = {R.drawable.red_shape_stroked,R.drawable.blue_shape_stroked,R.drawable.green_shape_stroked,R.drawable.yellow_shape_stroked};
        String [] colors = {res.getString(R.string.colorRed),res.getString(R.string.colorBlue),res.getString(R.string.colorGreen),res.getString(R.string.colorYellow)};
        for (int i = 0 ; i < ids.length ; i++) {
            makeButton(i, ids[i],colors[i],drawId[i]);
        }

    }

    public void makeButton (final int index, int id, final String color, final int drawId) {
        buttons[index] = (Button) findViewById(id);
        buttons[index].setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
               colorTextView.setText(color);
               clearButtons(v);
               buttons[index].setBackground(ContextCompat.getDrawable(v.getContext(),drawId));
               makePostRequest(deviceID, "changeColor",getRGBColor(color));
           }
        });
    }
    public void clearButtons (View v) {
        int [] undrawId = {R.drawable.red_shape,R.drawable.blue_shape,R.drawable.green_shape,R.drawable.yellow_shape};
        for (int i = 0 ; i < undrawId.length ; i++) {
            buttons[i].setBackground(ContextCompat.getDrawable(v.getContext(),undrawId[i]));
        }
    }

    public void doWithState(JSONObject obj) throws JSONException {

        obj = obj.getJSONObject("result");
        String auxS = obj.getString("status");
        lampSwitch.setChecked(auxS.equals("on"));
        int auxI = obj.getInt("brightness");

        brightnessSeekBar.setProgress(auxI);
        brightnessTextView.setText(" " + new Integer (brightnessSeekBar.getProgress()).toString());
        auxS = obj.getString("color");
        //if(auxS.equals()) // Select different colors
        colorTextView.setText(getColorName(auxS));
    };

    private void showMessage(String s) {
        Toast.makeText(LampActivity.this, s, Toast.LENGTH_LONG).show();
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
                Toast.makeText(LampActivity.this, "error", Toast.LENGTH_LONG).show();
            }
        };
    }

    public String getColorName(String color){
        View v = (View) findViewById(R.id.colorButtonGroup);
        Resources res = getResources();
        switch(color){
            case "FFEB3B":
                buttons[3].setBackground(ContextCompat.getDrawable(v.getContext(),R.drawable.yellow_shape_stroked));
                return " " + res.getString(R.string.colorYellow);
            case "2196F3":
                buttons[1].setBackground(ContextCompat.getDrawable(v.getContext(),R.drawable.blue_shape_stroked));
                return " " + res.getString(R.string.colorBlue);
            case "4CAF50":
                buttons[2].setBackground(ContextCompat.getDrawable(v.getContext(),R.drawable.green_shape_stroked));
                return " " + res.getString(R.string.colorGreen);
        }
        buttons[0].setBackground(ContextCompat.getDrawable(v.getContext(),R.drawable.red_shape_stroked));
        return  " " + res.getString(R.string.colorRed); // My default.
    };

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

    public String getRGBColor(String color) {
        switch(color){
            case "Yellow":
                return "FFEB3B";
            case "Blue":
                return "2196F3";
            case "Green":
                return "4CAF50";
            case "Red":
                return "F44336";
            case "Amarillo":
                return "FFEB3B";
            case "Azul":
                return "2196F3";
            case "Verde":
                return "4CAF50";
            case "Rojo":
                return "F44336";
        }
        return "2196F3";
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

    private void makeBottomNav() {
        bottomNavView = (BottomNavigationView) findViewById(R.id.lamp_navigation);

        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch(item.getItemId()){
                    case R.id.navigation_rooms:
                        intent = new Intent(LampActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_devices:
                        intent = new Intent(LampActivity.this, AllDevicesActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_combo:
                        intent = new Intent(LampActivity.this, CombosActivity.class);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
    }

}