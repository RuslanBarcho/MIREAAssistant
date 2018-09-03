package radonsoft.mireaassistant;

import android.app.ActivityManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import radonsoft.mireaassistant.fragments.Professors;
import radonsoft.mireaassistant.fragments.Schedule;
import radonsoft.mireaassistant.fragments.Settings;
import radonsoft.mireaassistant.fragments.VRAccess;
import radonsoft.mireaassistant.helpers.Global;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //Shared Preferences
    SharedPreferences sp;
    //Fragments
    Schedule schedule = new Schedule();
    VRAccess vraccess = new VRAccess();
    Professors professors = new Professors();
    Settings settings = new Settings();

    //Public variables
    public static int fragmentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), null , getResources().getColor(R.color.colorPrimaryDark));
            this.setTaskDescription(taskDesc);
        }
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        getWeekNumber();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getValues();
        //Toolbar
        //Fragment changer and set default
        FragmentTransaction ftrans = getSupportFragmentManager().beginTransaction();
        ftrans.replace(R.id.container, schedule);
        //Drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        //return fragment after rotation

        switch (fragmentID){
            case 1:
                ftrans.replace(R.id.container, schedule);
                break;
            case 4:
                ftrans.replace(R.id.container, settings);
                break;
            default:

                break;
        }
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (Global.firstRun){
            Global.firstRun = false;
            navigationView.getMenu().getItem(0).setChecked(true);
        }
        ftrans.commit();
    }

    public void saveString(String toSave, String TAG){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(TAG, toSave).commit();
    }

    public void saveInt(int toSave, String TAG){
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(TAG, toSave)
                .commit();
    }

    public void saveArray(ArrayList<String> toSave, String TAG){
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(toSave);
        editor.putString(TAG, json);
        editor.commit();
    }

    public void getArray(ArrayList<String> toGet, String TAG){
        Gson gson = new Gson();
        String json = sp.getString(TAG, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        toGet = gson.fromJson(json, type);
    }

    public void saveValues(){
        ArrayList<String> scheduleNamesOdd = new ArrayList<>();
        ArrayList<String> scheduleRoomsOdd = new ArrayList<>();
        ArrayList<String> scheduleTeachersOdd = new ArrayList<>();
        ArrayList<String> scheduleTypeOdd = new ArrayList<>();

        ArrayList<String> scheduleNamesEven = new ArrayList<>();
        ArrayList<String> scheduleRoomsEven = new ArrayList<>();
        ArrayList<String> scheduleTeachersEven = new ArrayList<>();
        ArrayList<String> scheduleTypeEven = new ArrayList<>();

        for ( int i = 0; i < Global.scheduleNamesOddString.length; i++){
            scheduleNamesOdd.add(Global.scheduleNamesOddString[i]);
            scheduleRoomsOdd.add(Global.scheduleRoomsOddString[i]);
            scheduleTeachersOdd.add(Global.scheduleTeachersOddString[i]);
            scheduleTypeOdd.add(Global.scheduleTypeOddString[i]);

            scheduleNamesEven.add(Global.scheduleNamesEvenString[i]);
            scheduleRoomsEven.add(Global.scheduleRoomsEvenString[i]);
            scheduleTeachersEven.add(Global.scheduleTeachersEvenString[i]);
            scheduleTypeEven.add(Global.scheduleTypeEvenString[i]);
        }

        saveInt(Global.instituteID, "INSTITUTE_ID");
        saveInt(Global.loginID, "LOGIN_STATUS");
        saveString(Global.groupID, "GROUP_ID");

        saveArray(scheduleNamesOdd, "SCHEDULE_NAME_ODD");
        saveArray(scheduleNamesEven, "SCHEDULE_NAME_EVEN");
        saveArray(scheduleRoomsOdd, "SCHEDULE_ROOM_ODD");
        saveArray(scheduleTeachersOdd, "SCHEDULE_TEACHER_ODD");
        saveArray(scheduleRoomsEven, "SCHEDULE_ROOM_EVEN");
        saveArray(scheduleTeachersEven, "SCHEDULE_TEACHER_EVEN");
        saveArray(scheduleTypeEven, "SCHEDULE_TYPE_EVEN");
        saveArray(scheduleTypeOdd, "SCHEDULE_TYPE_ODD");
    }

    public void getValues(){
        ArrayList<String> scheduleNamesOdd;
        ArrayList<String> scheduleRoomsOdd;
        ArrayList<String> scheduleTeachersOdd;
        ArrayList<String> scheduleTypeOdd;

        ArrayList<String> scheduleNamesEven;
        ArrayList<String> scheduleRoomsEven;
        ArrayList<String> scheduleTeachersEven;
        ArrayList<String> scheduleTypeEven;

        Global.loginID = sp.getInt("LOGIN_STATUS", 0);
        if (Global.loginID == 0){

        } else {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            Global.instituteID = sp.getInt("INSTITUTE_ID", 0);
            Global.groupID = sp.getString("GROUP_ID", "");

            String json = sp.getString("SCHEDULE_NAME_ODD", null);
            scheduleNamesOdd = gson.fromJson(json, type);
            String jsonOne = sp.getString("SCHEDULE_NAME_EVEN", null);
            scheduleNamesEven = gson.fromJson(jsonOne, type);
            String jsonTwo = sp.getString("SCHEDULE_ROOM_ODD", null);
            scheduleRoomsOdd = gson.fromJson(jsonTwo, type);
            String jsonThree = sp.getString("SCHEDULE_ROOM_EVEN", null);
            scheduleRoomsEven = gson.fromJson(jsonThree, type);
            String jsonFour = sp.getString("SCHEDULE_TEACHER_ODD", null);
            scheduleTeachersOdd = gson.fromJson(jsonFour, type);
            String jsonFive = sp.getString("SCHEDULE_TEACHER_EVEN", null);
            scheduleTeachersEven = gson.fromJson(jsonFive, type);
            String jsonSix = sp.getString("SCHEDULE_TYPE_ODD", null);
            scheduleTypeOdd = gson.fromJson(jsonSix, type);
            String jsonSeven = sp.getString("SCHEDULE_TYPE_EVEN", null);
            scheduleTypeEven = gson.fromJson(jsonSeven, type);

            Global.scheduleNamesOddString = scheduleNamesOdd.toArray(new String[scheduleNamesOdd.size()]);
            Global.scheduleRoomsOddString = scheduleRoomsOdd.toArray(new String[scheduleRoomsOdd.size()]);
            Global.scheduleTeachersOddString = scheduleTeachersOdd.toArray(new String[scheduleTeachersOdd.size()]);
            Global.scheduleTypeOddString = scheduleTypeOdd.toArray(new String[scheduleTypeOdd.size()]);

            Global.scheduleNamesEvenString = scheduleNamesEven.toArray(new String[scheduleNamesEven.size()]);
            Global.scheduleRoomsEvenString = scheduleRoomsEven.toArray(new String[scheduleRoomsEven.size()]);
            Global.scheduleTeachersEvenString = scheduleTeachersEven.toArray(new String[scheduleTeachersEven.size()]);
            Global.scheduleTypeEvenString = scheduleTypeEven.toArray(new String[scheduleTypeEven.size()]);
        }
    }

    public void getWeekNumber() {
        GregorianCalendar gc = new GregorianCalendar();
        int day = 0;
        gc.add(Calendar.DATE, day);
        Global.weekNumber = gc.get(Calendar.WEEK_OF_YEAR) - 1;
        Calendar calendar = Calendar.getInstance();
        int today = (calendar.get(Calendar.DAY_OF_WEEK));
        if (today == 1){
            Global.weekNumber = Global.weekNumber + 1;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        FragmentTransaction ftrans = getSupportFragmentManager().beginTransaction();
        if (id == R.id.nav_schedule) {
            ftrans.replace(R.id.container, schedule);
        //} else if (id == R.id.nav_VR_access) {
        //   ftrans.replace(R.id.container, vraccess);
        } //else if (id == R.id.nav_professors) {
       //     ftrans.replace(R.id.container, professors);
        //}
            else if (id == R.id.nav_tools) {
            ftrans.replace(R.id.container, settings);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        ftrans.commit();
        return true;
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Global.loginID != 0){
            saveValues();
        }
    }
    @Override
    protected void onPause(){
        super.onPause();
        if (Global.loginID != 0){
            saveValues();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        getValues();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
