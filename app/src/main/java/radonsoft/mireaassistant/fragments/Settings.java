package radonsoft.mireaassistant.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import io.reactivex.schedulers.Schedulers;
import radonsoft.mireaassistant.MainActivity;
import radonsoft.mireaassistant.R;
import radonsoft.mireaassistant.helpers.ConvertStrings;
import radonsoft.mireaassistant.helpers.Global;
import radonsoft.mireaassistant.model.Group;
import radonsoft.mireaassistant.model.RequestWrapper;
import radonsoft.mireaassistant.model.Response;
import radonsoft.mireaassistant.network.GroupsService;
import radonsoft.mireaassistant.network.InstitutesService;
import radonsoft.mireaassistant.network.NetworkSingleton;


public class Settings extends Fragment {

    private View mRootView;
    private FrameLayout chooseGroup;
    private FrameLayout chooseInstitute;
    private FrameLayout chooseWeekType;
    private TextView instituteViewer;
    private TextView groupViewer;
    private TextView weekViewer;
    MainActivity ma;

    //vars for get data
    public ArrayList<String> institutes = new ArrayList();
    public ArrayList<String> institutesCompiled = new ArrayList();
    public ArrayList<String> institutesTranslited = new ArrayList();
    public String instituteNameTranslited;
    public String[] institutesString;

    public ArrayList<String> groups = new ArrayList();
    public ArrayList<String> groupsCompiled = new ArrayList();
    public ArrayList<String> groupsTranslited = new ArrayList();
    public String[] groupsString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_settings, container, false);
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.action_settings));
        chooseGroup = (FrameLayout) mRootView.findViewById(R.id.frameLayout);
        chooseInstitute = (FrameLayout) mRootView.findViewById(R.id.frameLayout2);
        chooseWeekType = (FrameLayout) mRootView.findViewById(R.id.frameLayout3);

        instituteViewer = (TextView) mRootView.findViewById(R.id.textView13);
        groupViewer = (TextView) mRootView.findViewById(R.id.textView9);
        weekViewer = (TextView) mRootView.findViewById(R.id.textView16);

        ma = new MainActivity();
        ma.getWeekNumber();
        ma.fragmentID = 4;

        convertInstToString(ma.instituteID);
        instituteViewer.setText(instituteNameTranslited);

        groupViewer.setText(ma.groupID);
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
                    weekViewer.setText("Нечетная");
                } else {
                    weekViewer.setText("Четная");
                }
            }
        });

        chooseWeekType.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
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
        return mRootView;
    }

    public void showGroupChooseDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.choose_group))
                .setCancelable(false)
                .setItems(groupsString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ma.groupID = groupsString[which];
                        groupViewer.setText(ma.groupID);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showInstituteChooseDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.choose_institute))
                .setCancelable(false)
                .setItems(institutesString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ma.instituteID = which + 1;
                        if (ma.instituteID == 6){
                            ma.instituteID = 7;
                        }
                        else{
                            if (ma.instituteID == 7){
                                ma.instituteID = 0;
                            }
                        }
                        convertInstToString(ma.instituteID);
                        instituteViewer.setText(instituteNameTranslited);
                        getGroupList();
                    }
                });
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
                    sortGroups(groups, institutes, String.valueOf(ma.instituteID));

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
                    sortGroups(groups, institutes, String.valueOf(ma.instituteID));
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
        groupsString = groupsTranslited.toArray(new String[groupsTranslited.size()]);
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

    @Override
    public void onResume() {
        super.onResume();
    }
}
