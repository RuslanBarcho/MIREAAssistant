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
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

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


public class Settings extends Fragment {

    private View mRootView;
    private FrameLayout chooseGroup;
    private FrameLayout chooseInstitute;
    private FrameLayout chooseWeekType;
    private FrameLayout about;
    private TextView instituteViewer;
    private TextView groupViewer;
    private TextView weekViewer;
    MainActivity ma;

    private boolean groupsSolo = false;
    private boolean institutesSolo = false;

    //vars for get data
    public ArrayList<String> institutes = new ArrayList<>();
    public ArrayList<String> institutesCompiled = new ArrayList<>();
    public ArrayList<String> institutesTranslited = new ArrayList<>();
    public String instituteNameTranslited;
    public String[] institutesString;

    public ArrayList<String> groups = new ArrayList<>();
    public ArrayList<String> groupsCompiled = new ArrayList<>();
    public ArrayList<String> groupsTranslited = new ArrayList<>();
    public String[] groupsString;
    public String[] groupsStringTranslited;

    //backup vars
    //public ArrayList<String> institutesBackup = new ArrayList<>();
    //public ArrayList<String> institutesCompiledBackup = new ArrayList<>();
    //public ArrayList<String> institutesTranslitedBackup = new ArrayList<>();
    //public ArrayList<String> groupsBackup = new ArrayList<>();
    //public ArrayList<String> groupsCompiledBackup = new ArrayList<>();
    //public ArrayList<String> groupsTranslitedBackup = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_settings, container, false);
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.action_settings));
        chooseGroup = (FrameLayout) mRootView.findViewById(R.id.frameLayout);
        chooseInstitute = (FrameLayout) mRootView.findViewById(R.id.frameLayout2);
        chooseWeekType = (FrameLayout) mRootView.findViewById(R.id.frameLayout3);
        about = (FrameLayout) mRootView.findViewById(R.id.frameLayout4);

        instituteViewer = (TextView) mRootView.findViewById(R.id.textView13);
        groupViewer = (TextView) mRootView.findViewById(R.id.textView9);
        weekViewer = (TextView) mRootView.findViewById(R.id.textView16);

        ma = new MainActivity();
        ma.fragmentID = 4;

        convertInstToString(Global.instituteID);
        instituteViewer.setText(instituteNameTranslited);

        groupViewer.setText(Global.groupID);
        if (Global.weekNumber % 2 == 0){
            weekViewer.setText("Четная");
        } else{
            weekViewer.setText("Нечетная");
        }

        chooseGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSoloInstituteList();
            }
        });

        chooseWeekType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (weekViewer.getText().equals("Четная")) {
                    Global.weekNumber = Global.weekNumber +1;
                    weekViewer.setText("Нечетная");
                } else {
                    Global.weekNumber = Global.weekNumber -1;
                    weekViewer.setText("Четная");
                }
            }
        });

        chooseWeekType.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ma.getWeekNumber();
                if (Global.weekNumber % 2 == 0){
                    weekViewer.setText("Четная");
                } else{
                    weekViewer.setText("Нечетная");
                }
                return true;
            }
        });

        chooseInstitute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo: parse institutes and groups via ArrayLists, add SharedPreferences
                getInstituteList();
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutMessage();
            }
        });
        return mRootView;
    }

    public void showGroupChooseDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.choose_group));
                if (!groupsSolo){
                    builder.setCancelable(false);
                } else {
                    builder.setNegativeButton(getString(R.string.about_close), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        } });
                }
                builder.setItems(groupsStringTranslited, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (hasConnection(getContext())) {
                            Global.groupID = groupsString[which];
                            groupViewer.setText(Global.groupID);
                            scheduleClear();
                            Global global = new Global();

                            global.getScheduleEven(new DisposableObserver<Even>() {
                                @Override
                                public void onNext(@NonNull Even even) {
                                    Log.i("Schedule", even.getName().toString());
                                    Global.scheduleNamesEven.add(even.getName().toString());
                                    if (even.getRoom() == null) {
                                        Global.scheduleRoomsEven.add("―");
                                    } else {
                                        Global.scheduleRoomsEven.add(even.getRoom().toString());
                                    }
                                    if (even.getTeacher() == null) {
                                        Global.scheduleTeachersEven.add("―");
                                    } else {
                                        Global.scheduleTeachersEven.add(even.getTeacher().toString());
                                    }
                                    if (even.getType() == null) {
                                        Global.scheduleTypeEven.add("―");
                                    } else {
                                        Global.scheduleTypeEven.add(even.getType().toString());
                                    }
                                }

                                @Override
                                public void onError(@NonNull Throwable error) {
                                    Log.e("Schedule", error.toString(), error);
                                }

                                @Override
                                public void onComplete() {

                                }
                            });

                            global.getScheduleOdd(new DisposableObserver<Odd>() {
                                @Override
                                public void onNext(@NonNull Odd odd) {
                                    Log.i("Schedule", odd.getName().toString());
                                    Global.scheduleNamesOdd.add(odd.getName().toString());
                                    if (odd.getRoom() == null) {
                                        Global.scheduleRoomsOdd.add("―");
                                    } else {
                                        Global.scheduleRoomsOdd.add(odd.getRoom().toString());
                                    }
                                    if (odd.getTeacher() == null) {
                                        Global.scheduleTeachersOdd.add("―");
                                    } else {
                                        Global.scheduleTeachersOdd.add(odd.getTeacher().toString());
                                    }
                                    if (odd.getType() == null) {
                                        Global.scheduleTypeOdd.add("―");
                                    } else {
                                        Global.scheduleTypeOdd.add(odd.getType().toString());
                                    }
                                }

                                @Override
                                public void onError(@NonNull Throwable error) {
                                    Log.e("Schedule", error.toString(), error);
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                        } else{

                        }
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showInstituteChooseDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.choose_institute))
                //.setCancelable(false)
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
                        convertInstToString(Global.instituteID);
                        instituteViewer.setText(instituteNameTranslited);
                        getGroupList();
                    }
                })
            .setNegativeButton(getString(R.string.about_close), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            } });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void getInstituteList(){
        AllClear();
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

    public void getSoloInstituteList(){
        AllClear();
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
                    institutesSolo = true;
                    errorMessage();
                }, () -> {
                    int i;
                    for (i = 0; i < institutesCompiled.size(); i++){
                        stringConverter.instituteNumber = institutesCompiled.get(i);
                        stringConverter.convertInstitutes();
                        institutesTranslited.add(stringConverter.instituteOutput);
                    }
                    institutesString = institutesTranslited.toArray(new String[institutesTranslited.size()]);
                    getSoloGroupList();
                });
    }

    public void getSoloGroupList(){
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
                    groupsSolo = true;
                    showGroupChooseDialog();
                });
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
                    groupsSolo = false;
                    showGroupChooseDialog();
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
        groupsStringTranslited = groupsTranslited.toArray(new String[groupsTranslited.size()]);
        groupsString = groupsCompiled.toArray(new String[groupsCompiled.size()]);
    }


    public void convertInstToString(int input){
        ConvertStrings stringConverter = new ConvertStrings();
        stringConverter.instituteNumber = String.valueOf(input);
        stringConverter.convertInstitutes();
        instituteNameTranslited = stringConverter.instituteOutput;
    }

    public void AllClear(){
        institutes.clear();
        institutesCompiled.clear();
        institutesTranslited.clear();
        groupsCompiled.clear();
        groups.clear();
        groupsTranslited.clear();
    }

    public void scheduleClear(){
        Global.scheduleNamesOdd.clear();
        Global.scheduleNamesEven.clear();
        Global.scheduleTeachersOdd.clear();
        Global.scheduleTeachersEven.clear();
        Global.scheduleRoomsOdd.clear();
        Global.scheduleRoomsEven.clear();
        Global.scheduleTypeOdd.clear();
        Global.scheduleTypeEven.clear();
    }

    public static boolean hasConnection(final Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }

    public void aboutMessage(){
        if (getActivity() != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.ErrorDialogTheme);
            builder.setTitle(getString(R.string.about_title));
            builder.setMessage(getString(R.string.about_content));
            builder.setPositiveButton(getString(R.string.about_close),
                    new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void errorMessage(){
        if (getActivity() != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.ErrorDialogTheme)
                    .setCancelable(false)
                    .setTitle(getString(R.string.error_title))
                    .setMessage(getString(R.string.error_body))
                    .setPositiveButton(getString(R.string.error_try_again),
                    new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id) {
                            if (institutesSolo){
                                getSoloInstituteList();
                                institutesSolo = false;
                            } else {
                                getInstituteList();
                            }
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton(getString(R.string.about_close), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        } });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
