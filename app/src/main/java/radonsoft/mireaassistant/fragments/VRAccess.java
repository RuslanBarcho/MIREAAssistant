package radonsoft.mireaassistant.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import radonsoft.mireaassistant.MainActivity;
import radonsoft.mireaassistant.R;
import radonsoft.mireaassistant.helpers.Global;


public class VRAccess extends Fragment {
    private View mRootView;
    public Button getInstitutesBtn;
    public Button getGroupsBtn;
    public Button clear;
    public TextView output;

    MainActivity ma;

    public String[] institutes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ma = new MainActivity();
        mRootView = inflater.inflate(R.layout.fragment_vraccess, container, false);
        ((MainActivity) getActivity()).setActionBarTitle("Access to VR lab");

        getGroupsBtn = (Button) mRootView.findViewById(R.id.button);
        getInstitutesBtn = (Button) mRootView.findViewById(R.id.button2);
        clear = (Button) mRootView.findViewById(R.id.button3);
        output = (TextView) mRootView.findViewById(R.id.textView18);

        getGroupsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                output.setText("");
            }
        });

        getInstitutesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.loginID = 3;
            }
        });

        return mRootView;
    }

    public void getInsts(){
        ma.getInstituteList();
        institutes = ma.instituteStringtestall;
    }

}