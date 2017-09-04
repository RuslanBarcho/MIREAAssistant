package radonsoft.mireaassistant.fragments;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_settings, container, false);
        ((MainActivity) getActivity()).setActionBarTitle("Settings");
        chooseGroup = (FrameLayout) mRootView.findViewById(R.id.frameLayout);
        chooseGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return mRootView;
    }
}
