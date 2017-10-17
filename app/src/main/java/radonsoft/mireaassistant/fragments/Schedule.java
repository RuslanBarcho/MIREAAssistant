package radonsoft.mireaassistant.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
    
    private TextView classNameOne, classNameTwo, classNameThree, classNameFour, classNameFive , classNameSix;
    private TextView classRoomOne, classRoomTwo, classRoomThree, classRoomFour, classRoomFive, classRoomSix;
    private TextView classTeacherOne, classTeacherTwo, classTeacherThree, classTeacherFour, classTeacherFive, classTeacherSix;

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
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swiperefresh);
        daySelecter = (Spinner) mRootView.findViewById(R.id.spinner);
        weekSelecter = (Spinner) mRootView.findViewById(R.id.spinner1);
        days = getResources().getStringArray(R.array.schedule_days);
        weeks = getResources().getStringArray(R.array.schedule_weeks);

        mainlayout = (LinearLayout) mRootView.findViewById(R.id.mainLayout);

        classNameOne = (TextView) mRootView.findViewById(R.id.textView48);
        classNameTwo = (TextView) mRootView.findViewById(R.id.textView53);
        classNameThree = (TextView) mRootView.findViewById(R.id.textView58);
        classNameFour = (TextView) mRootView.findViewById(R.id.textView43);
        classNameFive = (TextView) mRootView.findViewById(R.id.textView33);
        classNameSix = (TextView) mRootView.findViewById(R.id.textView38);

        classRoomOne = (TextView) mRootView.findViewById(R.id.textView49);
        classRoomTwo = (TextView) mRootView.findViewById(R.id.textView54);
        classRoomThree = (TextView) mRootView.findViewById(R.id.textView61);
        classRoomFour = (TextView) mRootView.findViewById(R.id.textView44);
        classRoomFive = (TextView) mRootView.findViewById(R.id.textView34);
        classRoomSix = (TextView) mRootView.findViewById(R.id.textView39);

        classTeacherOne = (TextView) mRootView.findViewById(R.id.textView50);
        classTeacherTwo = (TextView) mRootView.findViewById(R.id.textView55);
        classTeacherThree = (TextView) mRootView.findViewById(R.id.textView62);
        classTeacherFour = (TextView) mRootView.findViewById(R.id.textView45);
        classTeacherFive = (TextView) mRootView.findViewById(R.id.textView35);
        classTeacherSix = (TextView) mRootView.findViewById(R.id.textView40);
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
            sortContentByTodayEven(today);
        } else{
            sortContentByTodayOdd(today);
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
                        sortContentByTodayEven(today);
                    } else{
                        sortContentByTodayOdd(today);
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
                    sortContentByTodayEven(today);
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
                    sortContentByTodayOdd(today);
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

    public void setContentOdd(int first, int second, int third, int fourth, int fifth, int sixth){
        setName(classNameOne, Global.scheduleNamesOddString, Global.scheduleTypeOddString, first);
        setName(classNameTwo, Global.scheduleNamesOddString, Global.scheduleTypeOddString, second);
        setName(classNameThree, Global.scheduleNamesOddString, Global.scheduleTypeOddString, third);
        setName(classNameFour, Global.scheduleNamesOddString, Global.scheduleTypeOddString, fourth);
        setName(classNameFive, Global.scheduleNamesOddString, Global.scheduleTypeOddString, fifth);
        setName(classNameSix, Global.scheduleNamesOddString, Global.scheduleTypeOddString, sixth);

        classRoomOne.setText(Global.scheduleRoomsOddString[first]);
        classRoomTwo.setText(Global.scheduleRoomsOddString[second]);
        classRoomThree.setText(Global.scheduleRoomsOddString[third]);
        classRoomFour.setText(Global.scheduleRoomsOddString[fourth]);
        classRoomFive.setText(Global.scheduleRoomsOddString[fifth]);
        classRoomSix.setText(Global.scheduleRoomsOddString[sixth]);

        classTeacherOne.setText(Global.scheduleTeachersOddString[first]);
        classTeacherTwo.setText(Global.scheduleTeachersOddString[second]);
        classTeacherThree.setText(Global.scheduleTeachersOddString[third]);
        classTeacherFour.setText(Global.scheduleTeachersOddString[fourth]);
        classTeacherFive.setText(Global.scheduleTeachersOddString[fifth]);
        classTeacherSix.setText(Global.scheduleTeachersOddString[sixth]);
    }

    public void sortContentByTodayOdd(int day){
        switch (day){
            case 0:
                setContentOdd(0,1,2,3,4,5);
                break;
            case 1:
                setContentOdd(6,7,8,9,10,11);
                break;
            case 2:
                setContentOdd(12,13,14,15,16,17);
                break;
            case 3:
                setContentOdd(18,19,20,21,22,23);
                break;
            case 4:
                setContentOdd(24,25,26,27,28,29);
                break;
            case 5:
                setContentOdd(30,31,32,33,34,35);
                break;
            default:
                setContentOdd(0,1,2,3,4,5);
                break;
        }
    }

    public void setContentEven(int first, int second, int third, int fourth, int fifth, int sixth){
        setName(classNameOne, Global.scheduleNamesEvenString, Global.scheduleTypeEvenString, first);
        setName(classNameTwo, Global.scheduleNamesEvenString, Global.scheduleTypeEvenString, second);
        setName(classNameThree, Global.scheduleNamesEvenString, Global.scheduleTypeEvenString, third);
        setName(classNameFour, Global.scheduleNamesEvenString, Global.scheduleTypeEvenString, fourth);
        setName(classNameFive, Global.scheduleNamesEvenString, Global.scheduleTypeEvenString, fifth);
        setName(classNameSix, Global.scheduleNamesEvenString, Global.scheduleTypeEvenString, sixth);

        classRoomOne.setText(Global.scheduleRoomsEvenString[first]);
        classRoomTwo.setText(Global.scheduleRoomsEvenString[second]);
        classRoomThree.setText(Global.scheduleRoomsEvenString[third]);
        classRoomFour.setText(Global.scheduleRoomsEvenString[fourth]);
        classRoomFive.setText(Global.scheduleRoomsEvenString[fifth]);
        classRoomSix.setText(Global.scheduleRoomsEvenString[sixth]);

        classTeacherOne.setText(Global.scheduleTeachersEvenString[first]);
        classTeacherTwo.setText(Global.scheduleTeachersEvenString[second]);
        classTeacherThree.setText(Global.scheduleTeachersEvenString[third]);
        classTeacherFour.setText(Global.scheduleTeachersEvenString[fourth]);
        classTeacherFive.setText(Global.scheduleTeachersEvenString[fifth]);
        classTeacherSix.setText(Global.scheduleTeachersEvenString[sixth]);
    }

    public void sortContentByTodayEven(int day){
        switch (day){
            case 0:
                setContentEven(0,1,2,3,4,5);
                break;
            case 1:
                setContentEven(6,7,8,9,10,11);
                break;
            case 2:
                setContentEven(12,13,14,15,16,17);
                break;
            case 3:
                setContentEven(18,19,20,21,22,23);
                break;
            case 4:
                setContentEven(24,25,26,27,28,29);
                break;
            case 5:
                setContentEven(30,31,32,33,34,35);
                break;
            default:
                setContentEven(0,1,2,3,4,5);
                break;
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