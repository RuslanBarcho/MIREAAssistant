package radonsoft.mireaassistant.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import radonsoft.mireaassistant.MainActivity;
import radonsoft.mireaassistant.R;


public class Schedule extends Fragment {
    private View mRootView;
    private Spinner daySelecter;
    private TextView test;
    private String[] days = {"Monday", "Tuesday", "Wednesday","Thursday","Friday","Saturday"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        daySelecter = (Spinner) mRootView.findViewById(R.id.spinner);
        test = (TextView) mRootView.findViewById(R.id.textView48);
        ((MainActivity) getActivity()).setActionBarTitle("Schedule");
        addItemsOnSpinner(days, daySelecter);

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

}
