package grupo4.smarthome;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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

/**
 * Created by estebankramer on 18/11/2017.
 */

public class AlarmActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavView;
    private TextView alarmName;
    private TextView alarmState;
    private Switch armSwitch;
    private Button changeCodeButton;
    private RequestQueue requestQueue;
    private Resources res;
    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        makeBottomNav();

        res=getResources();

        alarmName = (TextView) findViewById(R.id.alarm_name);
        alarmState = (TextView) findViewById(R.id.alarm_state);
        changeCodeButton = (Button) findViewById(R.id.changeCodeButton);
        armSwitch = (Switch) findViewById(R.id.armSwitch);

        String deviceID = getIntent().getStringExtra("id");

        String url = "http://10.0.2.2:8080/api/devices/"+ deviceID +"/getState";
        requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,responseListener(), errorListener());
        requestQueue.add(stringRequest);

        changeCodeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String title = res.getString(R.string.changeAlarmTitle);
                String hint1 = res.getString(R.string.changeAlarmHint);
                String hint2 = res.getString(R.string.newCodeHint);
                createChangeCodeDialogue(title, hint1, hint2);
            }
        });

        armSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    String title = res.getString(R.string.activateAlarmtitle);
                    String hint = res.getString(R.string.activateAlarmHint);
                    createDialogue(title, hint);
                    alarmState.setText(res.getString(R.string.armed));
                }
                else{
                    alarmState.setText(res.getString(R.string.disarmed));
                }
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
                Toast.makeText(AlarmActivity.this, "error", Toast.LENGTH_LONG).show();
            }
        };
    }

    public void doWithState(JSONObject obj) throws JSONException {

        obj = obj.getJSONObject("result");
        String auxS = obj.getString("status");
        if(auxS.equals("armed")){
            armSwitch.setChecked(true);
            alarmState.setText(res.getString(R.string.armed));
        }
        else{
            armSwitch.setChecked(false);
            alarmState.setText(res.getString(R.string.disarmed));
        }


    };

    private void showMessage(String s) {
        Toast.makeText(AlarmActivity.this, s, Toast.LENGTH_LONG).show();
    }

    public void createDialogue(String title, String hint){
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialogue, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        final TextView promptTitle = (TextView) promptsView.findViewById(R.id.alertText);
        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.alertInput);
        promptTitle.setText(title);
        userInput.setHint(hint);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(res.getString(R.string.accept),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                //showMessage(userInput.getText().toString());
                            }
                        })
                .setNegativeButton(res.getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void createChangeCodeDialogue(String title, String hint1, String hint2){
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialogue_change, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        final TextView promptTitle = (TextView) promptsView.findViewById(R.id.alertText2);
        final EditText oldCode = (EditText) promptsView.findViewById(R.id.oldCode);
        final EditText newCode = (EditText) promptsView.findViewById(R.id.newCode);
        promptTitle.setText(title);
        oldCode.setHint(hint1);
        newCode.setHint(hint2);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(res.getString(R.string.accept),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                //showMessage(userInput.getText().toString());
                            }
                        })
                .setNegativeButton(res.getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void makeBottomNav() {
        bottomNavView = (BottomNavigationView) findViewById(R.id.alarm_navigation);

        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch(item.getItemId()){
                    case R.id.navigation_rooms:
                        intent = new Intent(AlarmActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_devices:
                        intent = new Intent(AlarmActivity.this, AllDevicesActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_combo:
                        intent = new Intent(AlarmActivity.this, CombosActivity.class);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
    }
}
