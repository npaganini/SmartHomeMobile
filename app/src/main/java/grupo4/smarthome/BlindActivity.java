package grupo4.smarthome;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

/**
 * Created by estebankramer on 18/11/2017.
 */

public class BlindActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavView;
    private Button curtainButton;
    private TextView curtainState;
    private RequestQueue requestQueue;
    private Resources res;
    private boolean isOpened=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blind);
        makeBottomNav();
        curtainButton = (Button) findViewById(R.id.blind_button);
        curtainState = (TextView) findViewById(R.id.blind_state);
        String deviceID = getIntent().getStringExtra("id");
        res = getResources();

        String url = "http://10.0.2.2:8080/api/devices/"+ deviceID +"/getState";
        requestQueue = Volley.newRequestQueue(this);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,responseListener(), errorListener());

        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
        curtainButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                curtainButton.setEnabled(false);
                if(isOpened){
                    curtainButton.setText(res.getString(R.string.blindClosingState));
                    curtainState.setText(res.getString(R.string.blindClosingState));
                }
                else{
                    curtainButton.setText(res.getString(R.string.blindOpeningState));
                    curtainState.setText(res.getString(R.string.blindOpeningState));
                }
                isOpened= !isOpened;
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
                Toast.makeText(BlindActivity.this, "error", Toast.LENGTH_LONG).show();
            }
        };
    }

    public void doWithState(JSONObject obj) throws JSONException {

        obj = obj.getJSONObject("result");
        showMessage(obj.toString());
        String auxS = obj.getString("status");
        if(auxS.equals("opened")){
            curtainState.setText(res.getString(R.string.blindOpenedState));
            curtainButton.setText(res.getString(R.string.closeBlind));
            isOpened=true;
        }
        else{
            isOpened=false;
            curtainState.setText(res.getString(R.string.blindClosedState));
            curtainButton.setText(res.getString(R.string.closeBlind));
        }

    };

    private void showMessage(String s) {
        Toast.makeText(BlindActivity.this, s, Toast.LENGTH_LONG).show();
    }

    private void makeBottomNav() {
        bottomNavView = (BottomNavigationView) findViewById(R.id.blind_navigation);

        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch(item.getItemId()){
                    case R.id.navigation_rooms:
                        intent = new Intent(BlindActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_devices:
                        intent = new Intent(BlindActivity.this, AllDevicesActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_combo:
                        intent = new Intent(BlindActivity.this, CombosActivity.class);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
    }

}
