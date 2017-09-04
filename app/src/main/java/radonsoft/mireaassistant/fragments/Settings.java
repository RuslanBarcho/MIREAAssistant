package radonsoft.mireaassistant.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import radonsoft.mireaassistant.MainActivity;
import radonsoft.mireaassistant.R;


public class Settings extends Fragment {

    private View mRootView;
    private FrameLayout chooseGroup;
    private FrameLayout chooseInstitute;
    public int valueIDInt;
    MainActivity ma;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_settings, container, false);
        ((MainActivity) getActivity()).setActionBarTitle("Settings");
        chooseGroup = (FrameLayout) mRootView.findViewById(R.id.frameLayout);
        chooseInstitute = (FrameLayout) mRootView.findViewById(R.id.frameLayout2);
        ma = new MainActivity();
        chooseGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ma.getGroupList();
            showGroupChooseDialog();
            }
        });
        chooseInstitute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo: parse institutes and groups via ArrayLists, add SharedPreferences
                ma.getInstituteList();
                ma.compileInstituteList(ma.institute);
                showInstituteChooseDialog();
            }
        });
        return mRootView;
    }

    public void showGroupChooseDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("choose");
        builder.setItems(ma.groupsString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                valueIDInt = which;

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showInstituteChooseDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("choose");
        builder.setItems(ma.instituteString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                valueIDInt = which;

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        ma.getGroupList();
        ma.getInstituteList();
    }
}
