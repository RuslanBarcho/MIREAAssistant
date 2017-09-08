package radonsoft.mireaassistant.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import radonsoft.mireaassistant.MainActivity;
import radonsoft.mireaassistant.R;
import radonsoft.mireaassistant.helpers.Global;
import radonsoft.mireaassistant.model.Group;
import radonsoft.mireaassistant.model.RequestWrapper;
import radonsoft.mireaassistant.model.Response;
import radonsoft.mireaassistant.network.GroupsService;
import radonsoft.mireaassistant.network.InstitutesService;
import radonsoft.mireaassistant.network.NetworkSingleton;


public class Schedule extends Fragment {
    private View mRootView;
    private Spinner daySelecter;
    private TextView test;
    private int today;

    public ArrayList<String> institutes = new ArrayList();
    public ArrayList<String> institutesCompiled = new ArrayList();
    public String[] institutesString;

    public ArrayList<String> groups = new ArrayList();
    public ArrayList<String> groupsCompiled = new ArrayList();
    public String[] groupsString;

    public static int localLoginStatus;

    MainActivity ma;
    String[] days = {"Monday", "Tuesday", "Wednesday","Thursday","Friday","Saturday"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ma = new MainActivity();

        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.schedule));
        mRootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        //startup part
        daySelecter = (Spinner) mRootView.findViewById(R.id.spinner);
        test = (TextView) mRootView.findViewById(R.id.textView48);
        days = getResources().getStringArray(R.array.schedule_days);

        addItemsOnSpinner(days, daySelecter);
        setToday();

        if (Global.loginID == 0){
            startUp();
        }

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
            }
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });
    }

    public void startUp(){
        switch (Global.loginID) {
            case 0:
                getInstituteList();
                break;
            default:

                break;
        }
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
                .map(Group::getInstitute)
                .subscribe((institute) -> {
                    institutes.add(String.valueOf(institute));
                    if (institutesCompiled.contains(String.valueOf(institute))){

                    } else{
                        institutesCompiled.add(String.valueOf(institute));
                    }
                    institutesString = institutesCompiled.toArray(new String[institutesCompiled.size()]);
                }, error -> {
                    Log.e("inst", error.toString(), error);
                }, () -> {
                    if (Global.loginID == 0){
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
        groupsString = groupsCompiled.toArray(new String[groupsCompiled.size()]);
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
                    sortGroups(groups, institutes, String.valueOf(ma.instituteID));
                        showGroupChooseDialog();
                });
    }

    public void showInstituteChooseDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.choose_institute))
                .setCancelable(false)
                .setItems(institutesString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ma.instituteID = which + 1;
                if (ma.instituteID == 5){
                    ma.instituteID = 7;
                }
                if (ma.instituteID == 6){
                    ma.instituteID = 0;
                }
                getGroupList();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showGroupChooseDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.choose_group))
                .setCancelable(false)
                .setItems(groupsString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Global.loginID = 3;
                ma.groupID = groupsString[which];
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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
        MainActivity ma = new MainActivity();
        setToday();
        daySelecter.setSelection(today);
    }

    @Override
    public void onStart() {
        super.onStart();
        setToday();
        daySelecter.setSelection(today);

    }

}
