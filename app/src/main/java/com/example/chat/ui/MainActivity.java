package com.example.chat.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.chat.R;
import com.example.chat.loginrigestration.LoginActivity;
import com.example.chat.services.MySingleton;
import com.example.chat.settings.findfriends.FindFriendsActivity;
import com.example.chat.settings.SettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private Toolbar mtoolbar;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private TabsAccessorAdapter tabsAccessorAdapter;
//    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference rootReference;
//
final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAA9LGt-eA:APA91bEBUKsGO1HMg4fT-bH2Uxgt9ukyvgkH7D8lOlExvkMe7huSX5xFkZ4Jj1IZwlbGTjV_SLBUrhEG9RPK0bdPHplnLRVnSzKoLsnfuJzutegERWReWV1rumGX2jceOmblxn54jnYT";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";
    private String currentUserID;
    //
    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;

    //

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {

            sendUserToLoginActivity();

        } else {
            updateUserStatus("online");

            verifyUserExistance();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser!=null)
            updateUserStatus("offline");
    else{

    }}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser!=null)
            updateUserStatus("offline");
        else{

        }
    }

    private void verifyUserExistance() {
        String currentUserId = mAuth.getCurrentUser().getUid();

        rootReference.child("User").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if ((snapshot.child("name").exists())) { // to check if the user is new user or not
                    Toast.makeText(getBaseContext(), "welcome", Toast.LENGTH_LONG).show();
                } else {
                    //sendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
     //   currentUser = mAuth.getCurrentUser();
        rootReference = FirebaseDatabase.getInstance().getReference();
        init();

    }


    private void init() {
        mtoolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("WhatsApp");

        viewPager = (ViewPager2) findViewById(R.id.main_tabs_pager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        tabsAccessorAdapter = new TabsAccessorAdapter(fragmentManager, getLifecycle());
        viewPager.setAdapter(tabsAccessorAdapter);

        tabLayout = (TabLayout) findViewById(R.id.main_tabs);
        setTabsTitles();


    }

    private void setTabsTitles() {


        tabLayout.addTab(tabLayout.newTab().setText("Chat"));
        tabLayout.addTab(tabLayout.newTab().setText("Groups"));
        tabLayout.addTab(tabLayout.newTab().setText("Contact"));
        tabLayout.addTab(tabLayout.newTab().setText("Request"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {


            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

    }

    private void sendUserToLoginActivity() {

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // to prevent the backing to RegisterActivity
        startActivity(intent);
        finish();


    }

    private void sendUserToSettingsActivity() {

        Intent intent = new Intent(this, SettingsActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // to prevent the backing to RegisterActivity
        startActivity(intent);
        finish();



    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menue, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.logout) {
            updateUserStatus("offline");

            mAuth.signOut();
            sendUserToLoginActivity();

        } else if (item.getItemId() == R.id.settings) {
            sendUserToSettingsActivity();

        } else if (item.getItemId() == R.id.createGroup) {

            requestNewGroup();
        }
        else if (item.getItemId() == R.id.find_friends) {
            sendUserToFindFriendsAcivity();

        }
        else if (item.getItemId() == R.id.send_notification) {

            TOPIC = "/topics/userABC"; //topic must match with what the receiver subscribed to
            NOTIFICATION_TITLE = "NOTIFICATION_TITLE";
            NOTIFICATION_MESSAGE = "NOTIFICATION_MESSAGE";

            JSONObject notification = new JSONObject();
            JSONObject notifcationBody = new JSONObject();
            try {
                notifcationBody.put("title", NOTIFICATION_TITLE);
                notifcationBody.put("message", NOTIFICATION_MESSAGE);
                notification.put("to", TOPIC);
                notification.put("data", notifcationBody);
            } catch (JSONException e) {
                Log.e(TAG, "onCreate: " + e.getMessage() );
            }
            sendNotification(notification);

    }

        return true;
    }

    private void sendNotification(JSONObject notification) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                        Toast.makeText(MainActivity.this, "sent" , Toast.LENGTH_LONG).show();

//                        edtTitle.setText("");
//                        edtMessage.setText("");
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //
                        // parseVolleyError(error);
                        Toast.makeText(MainActivity.this, "Request error"+error.networkResponse.statusCode , Toast.LENGTH_LONG).show();



//                        Toast.makeText(MainActivity.this, "Request error", Toast.LENGTH_LONG).show();
                   Log.i("errorMessage", "onErrorResponse: Didn't work"+error.networkResponse.statusCode);
                    }
                }





                ){


            @Override
            public Map getHeaders() throws AuthFailureError {
                Map params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }

        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }


    private void sendUserToFindFriendsAcivity() {

        startActivity(new Intent(this, FindFriendsActivity.class));
    }

    private void requestNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Group name : ");
        final EditText etGroupName = new EditText(this);
        builder.setView(etGroupName);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = etGroupName.getText().toString();
                if (groupName.isEmpty()) {

                    Toast.makeText(getBaseContext(), "please entre the group name ...", Toast.LENGTH_LONG).show();

                } else {

                    createNewGroup(groupName);

                }

            }


        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });
        builder.show();


    }

    private void createNewGroup(String group) {


        rootReference.child("Groups").child(group).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(getBaseContext(), "the group created", Toast.LENGTH_LONG).show();

                }


            }
        });
    }
    private void updateUserStatus(String state){
        String saveCurrentTime,saveCurrentDate;
        Calendar calender=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(calender.getTime());
        SimpleDateFormat currentTime=new SimpleDateFormat("hh:mm a");
        saveCurrentTime=currentTime.format(calender.getTime());

        HashMap<String,Object> onlineState=new HashMap<>();
        onlineState.put("time",saveCurrentTime);
        onlineState.put("date",saveCurrentDate);
        onlineState.put("state",state);
        mAuth = FirebaseAuth.getInstance();

        currentUserID=mAuth.getCurrentUser().getUid();
        if(currentUserID!=null){
        rootReference.child("User").child(currentUserID).child("userState")
                .updateChildren(onlineState);}
    }
}