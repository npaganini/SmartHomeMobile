package grupo4.smarthome;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class TimerActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavView;
    private TextView timerName;
    private TextView timerState;
    private EditText timerInput;
    private Button timerButton;
    private RequestQueue requestQueue;
    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        makeBottomNav();

        timerName = (TextView) findViewById(R.id.timer_name);
        timerState = (TextView) findViewById(R.id.timer_room);
        timerInput = (EditText) findViewById(R.id.timerInput);
        timerButton = (Button) findViewById(R.id.timer_button);

        res=getResources();

        String deviceID = getIntent().getStringExtra("id");

        String url = "http://10.0.2.2:8080/api/devices/"+ deviceID +"/getState";
        requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,responseListener(), errorListener());
        requestQueue.add(stringRequest);

        timerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

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
                Toast.makeText(TimerActivity.this, "error", Toast.LENGTH_LONG).show();
            }
        };
    }

    public void doWithState(JSONObject obj) throws JSONException {

        obj = obj.getJSONObject("result");
        String status = obj.getString("status");
        String interval = obj.getString("interval");
        String remaining = obj.getString("remaining");

        if(status.equals("inactive")){
            timerState.setText(res.getString(R.string.inactive));
        }
        else{
            timerState.setText( res.getString(R.string.active) + res.getString(R.string.timeRemaining) + " " + remaining );
        }

    };

    private void showMessage(String s) {
        Toast.makeText(TimerActivity.this, s, Toast.LENGTH_LONG).show();
    }


    private void makeBottomNav() {
        bottomNavView = (BottomNavigationView) findViewById(R.id.timer_navigation);

        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch(item.getItemId()){
                    case R.id.navigation_rooms:
                        intent = new Intent(TimerActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_devices:
                        intent = new Intent(TimerActivity.this, AllDevicesActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_combo:
                        intent = new Intent(TimerActivity.this, CombosActivity.class);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
    }
}
