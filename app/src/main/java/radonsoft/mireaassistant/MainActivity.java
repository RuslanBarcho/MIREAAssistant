package radonsoft.mireaassistant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import radonsoft.mireaassistant.fragments.Professors;
import radonsoft.mireaassistant.fragments.Schedule;
import radonsoft.mireaassistant.fragments.Settings;
import radonsoft.mireaassistant.fragments.VRAccess;
import radonsoft.mireaassistant.model.Group;
import radonsoft.mireaassistant.model.RequestWrapper;
import radonsoft.mireaassistant.model.Response;
import radonsoft.mireaassistant.network.GroupsService;
import radonsoft.mireaassistant.network.InstitutesService;
import radonsoft.mireaassistant.network.NetworkSingleton;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //Shared Preferences
    SharedPreferences sp;
    //Fragments
    Schedule schedule = new Schedule();
    VRAccess vraccess = new VRAccess();
    Professors professors = new Professors();
    Settings settings = new Settings();
    public int today;
    public ArrayList<String> groups = new ArrayList();
    public ArrayList<String> institute = new ArrayList();
    public ArrayList<String> instituteCompiled = new ArrayList();
    public ArrayList<String> groupsCompiled = new ArrayList();
    public String[] groupsString;
    public String[] instituteString;

    //Public variables
    public int week;
    public int instituteID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        getWeekNumber();
        getInstituteList();getInstituteList();getInstituteList();
        getGroupList();getGroupList();getGroupList();
        saveValues();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Fragment changer and set default
        FragmentTransaction ftrans = getSupportFragmentManager().beginTransaction();
        ftrans.replace(R.id.container, schedule);
        //Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ftrans.commit();
    }

    public void getGroupList(){
        NetworkSingleton.getRetrofit().create(GroupsService.class)
                .getGroups()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(RequestWrapper::getResponse)
                .map(Response::getGroups)
                .toObservable()
                .flatMap(Observable::fromIterable)
                .doOnNext(g -> groups.add(g.getGroup()))
                .subscribe((Group group) -> {
                    Log.i("Schedule", group.getGroup());
                }, error -> {
                    Log.e("Schedule", error.toString(), error);
                });
    }

    public void getInstituteList(){
        NetworkSingleton.getRetrofit().create(InstitutesService.class)
                .getInstitutes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(RequestWrapper::getResponse)
                .map(Response::getGroups)
                .toObservable()
                .flatMap(Observable::fromIterable)
                .doOnNext(g -> institute.add(String.valueOf(g.getInstitute())))
                .map(Group::getInstitute)
                .subscribe((institute) -> {

                }, error -> {
                    Log.e("inst", error.toString(), error);
                });
        compileInstituteList(institute);
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
        saveArray(groups, "GROUPS");
        saveArray(institute, "INSTITUTE");
        saveArray(instituteCompiled, "INSTITUTE_COMPILED");
        saveArray(groupsCompiled, "GROUPS_COMPILED");
    }

    public void getValues(){
        getArray(groups, "GROUPS");
        getArray(institute, "INSTITUTE");
        getArray(instituteCompiled, "INSTITUTE_COMPILED");
        getArray(groupsCompiled, "GROUPS_COMPILED");
    }

    public void compileInstituteList(ArrayList<String> toCompile){
        int i;
        for (i = 0; i < institute.size(); i++){
            String local = institute.get(i);
            if (instituteCompiled.contains(local)){

            }
            else{
                instituteCompiled.add(local);
            }
        }
        instituteString = instituteCompiled.toArray(new String[instituteCompiled.size()]);
    }

    public void sortGroups(ArrayList<String> toSort, ArrayList<String> fullInstitutesList, String instituteID){
        int i;
        for (i = 0; i < groups.size(); i++){
            if (fullInstitutesList.get(i).equals(instituteID) & !groupsCompiled.contains(toSort.get(i))){
                groupsCompiled.add(toSort.get(i));
            }
        }
        groupsString = groupsCompiled.toArray(new String[groupsCompiled.size()]);
    }

    public void getWeekNumber() {
        GregorianCalendar gc = new GregorianCalendar();
        int day = 0;
        gc.add(Calendar.DATE, day);
        week = gc.get(Calendar.WEEK_OF_YEAR);
    }

    //Title
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.ErrorDialogTheme);
            builder.setTitle("About");
            builder.setMessage("Sample text");
            builder.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        } else if (id == R.id.action_refresh_all) {
            getGroupList();
            getInstituteList();
            Toast toast = Toast.makeText(this, "Refreshed",Toast.LENGTH_SHORT);
            toast.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        FragmentTransaction ftrans = getSupportFragmentManager().beginTransaction();
        if (id == R.id.nav_schedule) {
            ftrans.replace(R.id.container, schedule);
        } else if (id == R.id.nav_VR_access) {
            ftrans.replace(R.id.container, vraccess);
        } else if (id == R.id.nav_professors) {
            ftrans.replace(R.id.container, professors);
        } else if (id == R.id.nav_tools) {
            ftrans.replace(R.id.container, settings);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        ftrans.commit();
        return true;
    }
}
