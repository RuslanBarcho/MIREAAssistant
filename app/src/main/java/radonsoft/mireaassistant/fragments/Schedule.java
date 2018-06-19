package radonsoft.mireaassistant.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import radonsoft.mireaassistant.MainActivity;
import radonsoft.mireaassistant.R;
import radonsoft.mireaassistant.helpers.ConvertStrings;
import radonsoft.mireaassistant.helpers.Global;
import radonsoft.mireaassistant.helpers.RecyclerViewAdapter;
import radonsoft.mireaassistant.helpers.TimeManager;
import radonsoft.mireaassistant.model.Group;
import radonsoft.mireaassistant.model.schedule.Even;
import radonsoft.mireaassistant.model.schedule.Odd;

public class Schedule extends Fragment {
    //Views
    LinearLayout mainlayout;
    View mRootView;
    ProgressDialog progressDialog;
    private Spinner daySelecter;
    private Spinner weekSelecter;

    private int today;
    private int checkNull;

    public ArrayList<String> institutes = new ArrayList<>();
    public ArrayList<String> institutesCompiled = new ArrayList<>();
    public ArrayList<String> institutesTranslited = new ArrayList<>();

    public String[] institutesString;
    public String[] institutesStringIntegers;
    public ArrayList<String> groups = new ArrayList<>();
    public ArrayList<String> groupsCompiled = new ArrayList<>();
    public ArrayList<String> groupsTranslited = new ArrayList<>();
    public String[] groupsString;
    public String[] groupsStringTranslited;

    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;

    public SwipeRefreshLayout mSwipeRefreshLayout;

    private boolean optionBar = false;

    MainActivity ma;
    String[] days = {"Monday", "Tuesday", "Wednesday","Thursday","Friday","Saturday"};
    String[] weeks = {"Even", "Odd"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        //startTimers();
        setRetainInstance(true);
        //initialize activity
        ma = new MainActivity();
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.schedule));
        //Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        //initialize elements
        mSwipeRefreshLayout = mRootView.findViewById(R.id.swiperefresh);
        daySelecter = mRootView.findViewById(R.id.spinner);
        weekSelecter = mRootView.findViewById(R.id.spinner1);
        days = getResources().getStringArray(R.array.schedule_days);
        weeks = getResources().getStringArray(R.array.schedule_weeks);

        mainlayout = mRootView.findViewById(R.id.mainLayout);

        recyclerView = mRootView.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);

        //set content
        addItemsOnSpinner(days, daySelecter);
        addWeeksOnSpinner(weeks, weekSelecter);
        setToday();
        //start dialog if it's first app running
        AllClear();
        if (Global.loginID == 0){
            mainlayout.setVisibility(View.GONE);
            AllClear();
            getInstitutesAndGroups();
        } else {
            checkNull = 6;
            setSchedule();
        }
        //after content set things
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3
        );
        //code for refresh widget
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mSwipeRefreshLayout.setRefreshing(true);
                        getAndSortSchedule();
                        optionBar = true;
                    }
                }
        );
        //setting fragment identificator
        ma.fragmentID = 1;

        daySelecter.setSelection(today);
        if (Global.weekNumber % 2 == 0){
            weekSelecter.setSelection(0);
        } else{
            weekSelecter.setSelection(1);
        }

        return mRootView;
    }

    public void setSchedule(){
        if (Global.weekNumber % 2 == 0){
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new RecyclerViewAdapter(today, true);
            recyclerView.setAdapter(adapter);
        } else{
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new RecyclerViewAdapter(today, false);
            recyclerView.setAdapter(adapter);
        }
    }

    public void addItemsOnSpinner(final String[] toAdd, Spinner toAddIn){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_item, toAdd);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        toAddIn.setAdapter(dataAdapter);
        toAddIn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                today = selectedItemPosition;
                if (checkNull == 6) {
                    if (Global.weekNumber % 2 == 0){
                        setSchedule();
                    } else{
                        setSchedule();
                    }
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void addWeeksOnSpinner(final String[] toAdd, Spinner toAddIn){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_item, toAdd);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        toAddIn.setAdapter(dataAdapter);
        toAddIn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                switch (selectedItemPosition){
                    case 0:
                        if (Global.weekNumber % 2 == 0){
                            setSchedule();
                        } else {
                            Global.weekNumber = Global.weekNumber +1;
                            setSchedule();
                        }
                        break;
                    case 1:
                        if (Global.weekNumber % 2 == 0){
                            Global.weekNumber = Global.weekNumber -1;
                            setSchedule();
                        } else {
                            setSchedule();
                        }
                        break;
                }

            }
            public void onNothingSelected(AdapterView<?> parent) {
                //nothing to do
            }
        });
    }

    public void getInstitutesAndGroups(){
        AllClear();
        showProgressDialog();
        ConvertStrings stringConverter = new ConvertStrings();
        Global global = new Global();
        global.getGroupsAndInsts(new DisposableObserver<Group>() {
            @Override
            public void onNext(@NonNull Group group) {
                Log.i("Group", group.getGroup());
                Log.i("Institute", String.valueOf(group.getInstitute()));
                institutes.add(String.valueOf(group.getInstitute()));
                groups.add(group.getGroup());
                if (!institutesCompiled.contains(String.valueOf(group.getInstitute()))){
                    institutesCompiled.add(String.valueOf(group.getInstitute()));
                }
            }
            @Override
            public void onError(@NonNull Throwable error) {
                Log.e("Schedule", error.toString(), error);
                progressDialog.dismiss();
                errorMessage();
            }
            @Override
            public void onComplete() {
                int i;
                Collections.sort(institutesCompiled, new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        return s1.compareToIgnoreCase(s2);
                    }
                });
                for (i = 0; i < institutesCompiled.size(); i++){
                    stringConverter.instituteNumber = institutesCompiled.get(i);
                    stringConverter.convertInstitutes();
                    institutesTranslited.add(stringConverter.instituteOutput);
                }
                institutesStringIntegers = institutesCompiled.toArray(new String[institutesCompiled.size()]);
                institutesString = institutesTranslited.toArray(new String[institutesTranslited.size()]);
                progressDialog.dismiss();
                showInstituteChooseDialog();
            }
        });
    }

    public void sortGroups(ArrayList<String> toSort, ArrayList<String> fullInstitutesList, String instituteID){
        int i;
        for (i = 0; i < groups.size(); i++){
            if (fullInstitutesList.get(i).equals(instituteID) & !groupsCompiled.contains(toSort.get(i))){
                groupsCompiled.add(toSort.get(i));
            }
        }
        Collections.sort(groupsCompiled, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });
        ConvertStrings transliter = new ConvertStrings();
        for (i = 0; i<groupsCompiled.size(); i++){
            transliter.translitInput =groupsCompiled.get(i);
            transliter.translitGroups();
            groupsTranslited.add(transliter.translitOutput);
        }
        groupsStringTranslited = groupsTranslited.toArray(new String[groupsTranslited.size()]);
        groupsString = groupsCompiled.toArray(new String[groupsCompiled.size()]);
    }

    public void showInstituteChooseDialog(){
        if (getActivity() != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getString(R.string.choose_institute))
                    .setCancelable(false)
                    .setItems(institutesString, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Global.instituteID = Integer.valueOf(institutesStringIntegers[which]);
                            sortGroups(groups, institutes, String.valueOf(Global.instituteID));
                            showGroupChooseDialog();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void showGroupChooseDialog(){
        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.choose_group))
                    .setCancelable(false)
                    .setItems(groupsStringTranslited, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Global.groupID = groupsString[which];
                            getAndSortSchedule();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void getAndSortSchedule(){
        if (Global.loginID == 0){
            showProgressDialog();
        }
        ArrayList<String> scheduleNamesOdd = new ArrayList<>();
        ArrayList<String> scheduleRoomsOdd = new ArrayList<>();
        ArrayList<String> scheduleTeachersOdd = new ArrayList<>();
        ArrayList<String> scheduleTypeOdd = new ArrayList<>();

        ArrayList<String> scheduleNamesEven = new ArrayList<>();
        ArrayList<String> scheduleRoomsEven = new ArrayList<>();
        ArrayList<String> scheduleTeachersEven = new ArrayList<>();
        ArrayList<String> scheduleTypeEven = new ArrayList<>();

        Global global = new Global();
        global.getScheduleEven(new DisposableObserver<Even>() {
            @Override
            public void onNext(@NonNull Even even) {
                Log.i("Schedule", even.getName().toString());
                scheduleNamesEven.add(even.getName().toString());
                if (even.getRoom() == null) {
                    scheduleRoomsEven.add("―");
                } else {
                    scheduleRoomsEven.add(even.getRoom().toString());
                }
                if (even.getTeacher() == null) {
                    scheduleTeachersEven.add("―");
                } else {
                    scheduleTeachersEven.add(even.getTeacher().toString());
                }
                if (even.getType() == null) {
                    scheduleTypeEven.add("―");
                } else {
                    scheduleTypeEven.add(even.getType().toString());
                }
            }

            @Override
            public void onError(@NonNull Throwable error) {
                Log.e("Schedule", error.toString(), error);
                if (optionBar){
                    Toast toast = Toast.makeText(getActivity(), getString(R.string.error_body),Toast.LENGTH_SHORT);
                    toast.show();
                    optionBar = false;
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onComplete() {
                ma.getWeekNumber();
                Global.scheduleNamesEvenString = scheduleNamesEven.toArray(new String[scheduleNamesEven.size()]);
                Global.scheduleRoomsEvenString = scheduleRoomsEven.toArray(new String[scheduleRoomsEven.size()]);
                Global.scheduleTeachersEvenString = scheduleTeachersEven.toArray(new String[scheduleTeachersEven.size()]);
                Global.scheduleTypeEvenString = scheduleTypeEven.toArray(new String[scheduleTypeEven.size()]);
                if (Global.weekNumber % 2 == 0) {
                    weekSelecter.setSelection(0);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    adapter = new RecyclerViewAdapter(today, true);
                    recyclerView.setAdapter(adapter);
                    checkNull = 6;
                    mainlayout.setVisibility(View.VISIBLE);
                    if (optionBar & getActivity() != null){
                        Toast toast = Toast.makeText(getActivity(), getString(R.string.refreshed),Toast.LENGTH_SHORT);
                        toast.show();
                        optionBar = false;
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            }
        });
        global.getScheduleOdd(new DisposableObserver<Odd>() {
            @Override
            public void onNext(@NonNull Odd odd) {
                Log.i("Schedule", odd.getName().toString());
                scheduleNamesOdd.add(odd.getName().toString());
                if (odd.getRoom() == null) {
                    scheduleRoomsOdd.add("―");
                } else {
                    scheduleRoomsOdd.add(odd.getRoom().toString());
                }
                if (odd.getTeacher() == null) {
                    scheduleTeachersOdd.add("―");
                } else {
                    scheduleTeachersOdd.add(odd.getTeacher().toString());
                }
                if (odd.getType() == null) {
                    scheduleTypeOdd.add("―");
                } else {
                    scheduleTypeOdd.add(odd.getType().toString());
                }
            }

            @Override
            public void onError(@NonNull Throwable error) {
                Log.e("Schedule", error.toString(), error);
                if (Global.loginID == 0){
                    progressDialog.dismiss();
                    errorMessage();
                }
                if (optionBar){
                    Toast toast = Toast.makeText(getActivity(), getString(R.string.error_body),Toast.LENGTH_SHORT);
                    toast.show();
                    optionBar = false;
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onComplete() {
                ma.getWeekNumber();
                Global.scheduleNamesOddString = scheduleNamesOdd.toArray(new String[scheduleNamesOdd.size()]);
                Global.scheduleRoomsOddString = scheduleRoomsOdd.toArray(new String[scheduleRoomsOdd.size()]);
                Global.scheduleTeachersOddString = scheduleTeachersOdd.toArray(new String[scheduleTeachersOdd.size()]);
                Global.scheduleTypeOddString = scheduleTypeOdd.toArray(new String[scheduleTypeOdd.size()]);
                if (Global.loginID == 0){
                    progressDialog.dismiss();
                    Global.loginID = 3;
                }
                if (Global.weekNumber % 2 != 0) {
                    weekSelecter.setSelection(1);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    adapter = new RecyclerViewAdapter(today, false);
                    recyclerView.setAdapter(adapter);
                    checkNull = 6;
                    mainlayout.setVisibility(View.VISIBLE);
                    if (optionBar){
                        Toast toast = Toast.makeText(getActivity(), getString(R.string.refreshed),Toast.LENGTH_SHORT);
                        toast.show();
                        optionBar = false;
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            }
        });
    }

    public void errorMessage(){
        if (getActivity() != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.ErrorDialogTheme)
                    .setCancelable(false);
            builder.setTitle(getString(R.string.error_title));
            builder.setMessage(getString(R.string.error_body));
            builder.setPositiveButton(getString(R.string.error_try_again),
                    new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id) {
                            getInstitutesAndGroups();
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void setName(TextView field, String[] className, String[] classType, int number){
        if (classType[number].equals("―")){
            field.setText(className[number]);
        } else {
            String type;
            switch (classType[number]){
                case "12.0":
                    type = "ПР + ЛАБ";
                    break;
                case "0.0":
                    type = "ЛК";
                    break;
                case "1.0":
                    type = "ПР";
                    break;
                case "2.0":
                    type = "ЛАБ";
                    break;
                default:
                    type = classType[number];
                    break;
            }
            field.setText(className[number] + ", " + type);
        }
    }

    public void setToday(){
        Calendar calendar = Calendar.getInstance();
        today = (calendar.get(Calendar.DAY_OF_WEEK)) - 2;
        if (today == -1){
            today = 0;
        }
    }

    public void AllClear(){
        Global.institutes.clear();
        Global.institutesCompiled.clear();
        Global.institutesTranslited.clear();
        Global.groupsCompiled.clear();
        Global.groups.clear();
        Global.groupsTranslited.clear();
    }

    public void showProgressDialog(){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMax(100);
        progressDialog.setMessage(getString(R.string.data_loading));
        progressDialog.setTitle(getString(R.string.data_loading_title));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void startTimers(){
        Timer timer = new Timer();
        MyTimerTask task = new MyTimerTask();

        TimeManager manager = new TimeManager();
        manager.setTime();
        manager.createDates();

        switch (manager.getCurrentClass()){
            case 0:
                timer.schedule(task, TimeManager.firstClassStartDate);
                break;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (Global.weekNumber % 2 == 0){
            weekSelecter.setSelection(0);
        } else{
            weekSelecter.setSelection(1);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        mSwipeRefreshLayout.setEnabled(true);
        if (Global.weekNumber % 2 == 0){
            weekSelecter.setSelection(0);
        } else{
            weekSelecter.setSelection(1);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        AllClear();
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setRefreshing(false);
    }
    @Override
    public void onStop() {
        super.onStop();
        AllClear();
    }
    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings_menu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case (R.id.action_refresh_all):
                getAndSortSchedule();
                optionBar = true;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                ma.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(getActivity(), "Works!",Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }catch (NullPointerException e){

            }
        }
    }
}