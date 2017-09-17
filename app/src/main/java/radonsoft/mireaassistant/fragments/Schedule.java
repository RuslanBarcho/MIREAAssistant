package radonsoft.mireaassistant.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import radonsoft.mireaassistant.MainActivity;
import radonsoft.mireaassistant.R;
import radonsoft.mireaassistant.helpers.ConvertStrings;
import radonsoft.mireaassistant.helpers.Global;
import radonsoft.mireaassistant.model.Group;
import radonsoft.mireaassistant.model.RequestWrapper;
import radonsoft.mireaassistant.model.Response;
import radonsoft.mireaassistant.model.schedule.Even;
import radonsoft.mireaassistant.model.schedule.Odd;
import radonsoft.mireaassistant.network.GroupsService;
import radonsoft.mireaassistant.network.InstitutesService;
import radonsoft.mireaassistant.network.NetworkSingleton;

public class Schedule extends Fragment {
    //Views
    LinearLayout mainlayout;
    private View mRootView;
    private Spinner daySelecter;
    private TextView test;
    private TextView classNameOne, classNameTwo, classNameThree, classNameFour, classNameFive , classNameSix;
    private TextView classRoomOne, classRoomTwo, classRoomThree, classRoomFour, classRoomFive, classRoomSix;
    private TextView classTeacherOne, classTeacherTwo, classTeacherThree, classTeacherFour, classTeacherFive, classTeacherSix;

    private int today;
    private int todaySelected;
    private int checkNull;

    public ArrayList<String> institutes = new ArrayList();
    public ArrayList<String> institutesCompiled = new ArrayList();
    public ArrayList<String> institutesTranslited = new ArrayList();
    public String[] institutesString;

    public ArrayList<String> groups = new ArrayList();
    public ArrayList<String> groupsCompiled = new ArrayList();
    public ArrayList<String> groupsTranslited = new ArrayList();
    public String[] groupsStringID;
    public String[] groupsString;

    MainActivity ma;
    String[] days = {"Monday", "Tuesday", "Wednesday","Thursday","Friday","Saturday"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        //initialize activity
        ma = new MainActivity();
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.schedule));
        //Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        //initialize elements
        daySelecter = (Spinner) mRootView.findViewById(R.id.spinner);
        //test = (TextView) mRootView.findViewById(R.id.textView48);
        days = getResources().getStringArray(R.array.schedule_days);

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

        //set content
        addItemsOnSpinner(days, daySelecter);
        setToday();
        //start dialog if it's first app running

        ma.getWeekNumber();

        if (Global.loginID == 0){
            mainlayout.setVisibility(View.GONE);
            getInstituteList();
        } else {
            checkNull = 6;
            if (Global.weekNumber % 2 == 0){
                Global.scheduleNamesEvenString = Global.scheduleNamesEven.toArray(new String[Global.scheduleNamesEven.size()]);
                Global.scheduleRoomsEvenString = Global.scheduleRoomsEven.toArray(new String[Global.scheduleRoomsEven.size()]);
                sortContentByTodayEven(today);
            } else{
                Global.scheduleNamesOddString = Global.scheduleNamesOdd.toArray(new String[Global.scheduleNamesOdd.size()]);
                Global.scheduleRoomsOddString = Global.scheduleRoomsOdd.toArray(new String[Global.scheduleRoomsOdd.size()]);
                sortContentByTodayOdd(today);
            }
        }
        //after content set things
        ma.fragmentID = 1;
        daySelecter.setSelection(today);
        long curTime = System.currentTimeMillis();
        return mRootView;
    }

    public void addItemsOnSpinner(final String[] toAdd, Spinner toAddIn){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_item, toAdd);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        toAddIn.setAdapter(dataAdapter);
        toAddIn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                //ToDo change schedule
                todaySelected = selectedItemPosition;
                if (checkNull == 6) {
                    if (Global.weekNumber % 2 == 0){
                        sortContentByTodayEven(todaySelected);
                    } else{
                        sortContentByTodayOdd(todaySelected);
                    }
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
                //nothing to do
            }
        });
    }

    public void getInstituteList(){
        ConvertStrings stringConverter = new ConvertStrings();
        NetworkSingleton.getRetrofit().create(InstitutesService.class)
                .getInstitutes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(RequestWrapper::getResponse)
                .map(Response::getGroups)
                .toObservable()
                .flatMap(Observable::fromIterable)
                .map(Group::getInstitute)
                .subscribe((institute) -> {
                    institutes.add(String.valueOf(institute));
                    if (institutesCompiled.contains(String.valueOf(institute))){

                    } else{
                        institutesCompiled.add(String.valueOf(institute));
                    }

                }, error -> {
                    Log.e("inst", error.toString(), error);
                    errorMessage();
                }, () -> {
                    int i;
                    for (i = 0; i < institutesCompiled.size(); i++){
                        stringConverter.instituteNumber = institutesCompiled.get(i);
                        stringConverter.convertInstitutes();
                        institutesTranslited.add(stringConverter.instituteOutput);
                    }
                    institutesString = institutesTranslited.toArray(new String[institutesTranslited.size()]);
                    showInstituteChooseDialog();
                    });
    }

    public void sortGroups(ArrayList<String> toSort, ArrayList<String> fullInstitutesList, String instituteID){
        int i;
        for (i = 0; i < groups.size(); i++){
            if (fullInstitutesList.get(i).equals(instituteID) & !groupsCompiled.contains(toSort.get(i))){
                groupsCompiled.add(toSort.get(i));
            }
        }
        ConvertStrings transliter = new ConvertStrings();
        for (i = 0; i<groupsCompiled.size(); i++){
            transliter.translitInput =groupsCompiled.get(i);
            transliter.translitGroups();
            groupsTranslited.add(transliter.translitOutput);
        }
        groupsString = groupsTranslited.toArray(new String[groupsTranslited.size()]);
        groupsStringID = groupsCompiled.toArray(new String[groupsCompiled.size()]);
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
                .subscribe((Group group) -> {
                    Log.i("Schedule", group.getGroup());
                    groups.add(group.getGroup());
                }, error -> {
                    Log.e("Schedule", error.toString(), error);
                }, () ->{
                    sortGroups(groups, institutes, String.valueOf(Global.instituteID));
                        showGroupChooseDialog();
                });
    }

    public void showInstituteChooseDialog(){
        if (getActivity() != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getString(R.string.choose_institute))
                    .setCancelable(false)
                    .setItems(institutesString, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Global.instituteID = which + 1;
                            if (Global.instituteID == 6){
                                Global.instituteID = 7;
                            }
                            else{
                                if (Global.instituteID == 7){
                                    Global.instituteID = 0;
                                }
                            }
                            getGroupList();
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
                    .setItems(groupsString, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Global.scheduleNamesOdd.clear();
                            Global.loginID = 3;
                            Global.groupID = groupsStringID[which];
                            Global global = new Global();

                            global.getScheduleEven(new DisposableObserver<Even>() {
                                @Override
                                public void onNext(@NonNull Even even) {
                                    Log.i("Schedule", even.getName().toString());
                                    Global.scheduleNamesEven.add(even.getName().toString());
                                    if (even.getRoom() == null) {
                                        Global.scheduleRoomsEven.add("-");
                                    } else {
                                        Global.scheduleRoomsEven.add(even.getRoom().toString());
                                    }
                                }

                                @Override
                                public void onError(@NonNull Throwable error) {
                                    Log.e("Schedule", error.toString(), error);
                                }

                                @Override
                                public void onComplete() {
                                    if (Global.weekNumber % 2 == 0) {
                                        Global.scheduleNamesEvenString = Global.scheduleNamesEven.toArray(new String[Global.scheduleNamesEven.size()]);
                                        Global.scheduleRoomsEvenString = Global.scheduleRoomsEven.toArray(new String[Global.scheduleRoomsEven.size()]);
                                        sortContentByTodayOdd(today);
                                        checkNull = 6;
                                        mainlayout.setVisibility(View.VISIBLE);
                                    }
                                }
                            });

                            global.getScheduleOdd(new DisposableObserver<Odd>() {
                                @Override
                                public void onNext(@NonNull Odd odd) {
                                    Log.i("Schedule", odd.getName().toString());
                                    Global.scheduleNamesOdd.add(odd.getName().toString());
                                    if (odd.getRoom() == null) {
                                        Global.scheduleRoomsOdd.add("-");
                                    } else {
                                        Global.scheduleRoomsOdd.add(odd.getRoom().toString());
                                    }
                                }

                                @Override
                                public void onError(@NonNull Throwable error) {
                                    Log.e("Schedule", error.toString(), error);
                                }

                                @Override
                                public void onComplete() {
                                    if (Global.weekNumber % 2 == 0) {

                                    } else {
                                        Global.scheduleNamesOddString = Global.scheduleNamesOdd.toArray(new String[Global.scheduleNamesOdd.size()]);
                                        Global.scheduleRoomsOddString = Global.scheduleRoomsOdd.toArray(new String[Global.scheduleRoomsOdd.size()]);
                                        sortContentByTodayOdd(today);
                                        checkNull = 6;
                                        mainlayout.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void errorMessage(){
        if (getActivity() != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.ErrorDialogTheme);
            builder.setTitle(getString(R.string.error_title));
            builder.setMessage(getString(R.string.error_body));
            builder.setPositiveButton(getString(R.string.error_try_again),
                    new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id) {
                            getInstituteList();
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void setContentOdd(int first, int second, int third, int fourth, int fifth, int sixth){
        classNameOne.setText(Global.scheduleNamesOddString[first]);
        classNameTwo.setText(Global.scheduleNamesOddString[second]);
        classNameThree.setText(Global.scheduleNamesOddString[third]);
        classNameFour.setText(Global.scheduleNamesOddString[fourth]);
        classNameFive.setText(Global.scheduleNamesOddString[fifth]);
        classNameSix.setText(Global.scheduleNamesOddString[sixth]);

        classRoomOne.setText(Global.scheduleRoomsOddString[first]);
        classRoomTwo.setText(Global.scheduleRoomsOddString[second]);
        classRoomThree.setText(Global.scheduleRoomsOddString[third]);
        classRoomFour.setText(Global.scheduleRoomsOddString[fourth]);
        classRoomFive.setText(Global.scheduleRoomsOddString[fifth]);
        classRoomSix.setText(Global.scheduleRoomsOddString[sixth]);
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
        classNameOne.setText(Global.scheduleNamesEvenString[first]);
        classNameTwo.setText(Global.scheduleNamesEvenString[second]);
        classNameThree.setText(Global.scheduleNamesEvenString[third]);
        classNameFour.setText(Global.scheduleNamesEvenString[fourth]);
        classNameFive.setText(Global.scheduleNamesEvenString[fifth]);
        classNameSix.setText(Global.scheduleNamesEvenString[sixth]);

        classRoomOne.setText(Global.scheduleRoomsEvenString[first]);
        classRoomTwo.setText(Global.scheduleRoomsEvenString[second]);
        classRoomThree.setText(Global.scheduleRoomsEvenString[third]);
        classRoomFour.setText(Global.scheduleRoomsEvenString[fourth]);
        classRoomFive.setText(Global.scheduleRoomsEvenString[fifth]);
        classRoomSix.setText(Global.scheduleRoomsEvenString[sixth]);
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

    @Override
    public void onResume() {
        super.onResume();
        setToday();
        daySelecter.setSelection(today);
    }

    @Override
    public void onStart() {
        super.onStart();
        setToday();
        daySelecter.setSelection(today);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

}
