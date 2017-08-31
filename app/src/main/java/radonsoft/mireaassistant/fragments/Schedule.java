package radonsoft.mireaassistant.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import radonsoft.mireaassistant.MainActivity;
import radonsoft.mireaassistant.R;


public class Schedule extends Fragment {
    private View mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        ((MainActivity) getActivity()).setActionBarTitle("Schedule");

        return mRootView;
    }

}
