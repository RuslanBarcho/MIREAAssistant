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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import radonsoft.mireaassistant.MainActivity;
import radonsoft.mireaassistant.R;
import radonsoft.mireaassistant.helpers.ConvertStrings;
import radonsoft.mireaassistant.helpers.Global;
import radonsoft.mireaassistant.model.Group;
import radonsoft.mireaassistant.model.schedule.Even;
import radonsoft.mireaassistant.model.schedule.Odd;

public class Settings extends Fragment {

    View mRootView;
    FrameLayout chooseGroup;
    FrameLayout chooseInstitute;
    FrameLayout chooseWeekType;
    FrameLayout about;
    private TextView instituteViewer;
    private TextView groupViewer;
    private TextView weekViewer;
    MainActivity ma;

    private boolean groupsSolo = false;

    private int buttonClicked;
    private int restartDialog;

    //vars
    public String instituteNameTranslited;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
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

        ConvertStrings converter = new ConvertStrings();
        converter.translitInput = Global.groupID;
        converter.translitGroups();
        groupViewer.setText(converter.translitOutput);

        if (Global.weekNumber % 2 == 0){
            weekViewer.setText("Четная");
        } else{
            weekViewer.setText("Нечетная");
        }

        chooseGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClicked = 1;
                getInstitutesAndGroups();
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
                buttonClicked = 0;
                getInstitutesAndGroups();
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

    public void getInstitutesAndGroups(){
        AllClear();
        ConvertStrings stringConverter = new ConvertStrings();
        Global global = new Global();
        global.getGroupsAndInsts(new DisposableObserver<Group>() {
            @Override
            public void onNext(@NonNull Group group) {
                Log.i("Group", group.getGroup());
                Log.i("Institute", String.valueOf(group.getInstitute()));
                Global.institutes.add(String.valueOf(group.getInstitute()));
                Global.groups.add(group.getGroup());
                if (!Global.institutesCompiled.contains(String.valueOf(group.getInstitute()))){
                    Global.institutesCompiled.add(String.valueOf(group.getInstitute()));
                }
            }
            @Override
            public void onError(@NonNull Throwable error) {
                Log.e("Schedule", error.toString(), error);
                errorMessage();
            }
            @Override
            public void onComplete() {
                switch (buttonClicked){
                    case 0:
                        int i;
                        Collections.sort(Global.institutesCompiled, new Comparator<String>() {
                            @Override
                            public int compare(String s1, String s2) {
                                return s1.compareToIgnoreCase(s2);
                            }
                        });
                        for (i = 0; i < Global.institutesCompiled.size(); i++){
                            stringConverter.instituteNumber = Global.institutesCompiled.get(i);
                            stringConverter.convertInstitutes();
                            Global.institutesTranslited.add(stringConverter.instituteOutput);
                        }
                        Global.institutesStringIntegers = Global.institutesCompiled.toArray(new String[Global.institutesCompiled.size()]);
                        Global.institutesString = Global.institutesTranslited.toArray(new String[Global.institutesTranslited.size()]);
                        showInstituteChooseDialog();
                        break;
                    case 1:
                        sortGroups(Global.groups, Global.institutes, String.valueOf(Global.instituteID));
                        groupsSolo = true;
                        showGroupChooseDialog();
                        break;
                }
            }
        });
    }

    public void showInstituteChooseDialog(){
        Global.settingsDialogResume= 1;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.choose_institute))
                //.setCancelable(false)
                .setItems(Global.institutesString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Global.instituteID = Integer.valueOf(Global.institutesStringIntegers[which]);
                        convertInstToString(Global.instituteID);
                        instituteViewer.setText(instituteNameTranslited);
                        sortGroups(Global.groups, Global.institutes, String.valueOf(Global.instituteID));
                        groupsSolo = false;
                        Global.settingsDialogResume= 0;
                        showGroupChooseDialog();
                    }
                })
                .setNegativeButton(getString(R.string.about_close), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    restartDialog = 0;
                    } });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void sortGroups(ArrayList<String> toSort, ArrayList<String> fullInstitutesList, String instituteID){
        int i;
        for (i = 0; i < Global.groups.size(); i++){
            if (fullInstitutesList.get(i).equals(instituteID) & !Global.groupsCompiled.contains(toSort.get(i))){
                Global.groupsCompiled.add(toSort.get(i));
            }
        }
        Collections.sort(Global.groupsCompiled, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });
        ConvertStrings transliter = new ConvertStrings();
        for (i = 0; i<Global.groupsCompiled.size(); i++){
            transliter.translitInput =Global.groupsCompiled.get(i);
            transliter.translitGroups();
            Global.groupsTranslited.add(transliter.translitOutput);
        }
        Global.groupsStringTranslited = Global.groupsTranslited.toArray(new String[Global.groupsTranslited.size()]);
        Global.groupsString = Global.groupsCompiled.toArray(new String[Global.groupsCompiled.size()]);
    }

    public void showGroupChooseDialog(){
        if (getActivity() != null) {
            Global.settingsDialogResume = 2;
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.choose_group));
            if (!groupsSolo) {
                builder.setCancelable(false);
            } else {
                builder.setNegativeButton(getString(R.string.about_close), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            }
            builder.setItems(Global.groupsStringTranslited, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Global.settingsDialogResume = 0;
                    Global.groupID = Global.groupsString[which];
                    ConvertStrings converter = new ConvertStrings();
                    converter.translitInput = Global.groupID;
                    converter.translitGroups();
                    groupViewer.setText(converter.translitOutput);
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
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void convertInstToString(int input){
        ConvertStrings stringConverter = new ConvertStrings();
        stringConverter.instituteNumber = String.valueOf(input);
        stringConverter.convertInstitutes();
        instituteNameTranslited = stringConverter.instituteOutput;
    }

    public void AllClear(){
        Global.institutes.clear();
        Global.institutesCompiled.clear();
        Global.institutesTranslited.clear();
        Global.groupsCompiled.clear();
        Global.groups.clear();
        Global.groupsTranslited.clear();
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
                            getInstitutesAndGroups();
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
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings_menu, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
