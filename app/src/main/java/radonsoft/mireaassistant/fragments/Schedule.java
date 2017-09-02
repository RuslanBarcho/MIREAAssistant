package radonsoft.mireaassistant.fragments;

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
import radonsoft.mireaassistant.model.Group;
import radonsoft.mireaassistant.model.RequestWrapper;
import radonsoft.mireaassistant.model.Response;
import radonsoft.mireaassistant.network.GroupsService;
import radonsoft.mireaassistant.network.NetworkSingleton;


public class Schedule extends Fragment {
    private View mRootView;
    private Spinner daySelecter;
    private TextView test;
    private int today;
    private String[] days = {"Monday", "Tuesday", "Wednesday","Thursday","Friday","Saturday"};
    private ArrayList<String> groups = new ArrayList();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        MainActivity ma = new MainActivity();
        daySelecter = (Spinner) mRootView.findViewById(R.id.spinner);
        test = (TextView) mRootView.findViewById(R.id.textView48);
        ((MainActivity) getActivity()).setActionBarTitle("Schedule");
        addItemsOnSpinner(days, daySelecter);
        setToday();
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
        NetworkSingleton.getRetrofit().create(GroupsService.class)
                .getGroups()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(RequestWrapper::getResponse)
                .map(Response::getGroups)
                .toObservable()
                .flatMap(Observable::fromIterable)
                .doOnNext(g -> groups.add(g.getGroup()))
                .subscribe((Group group) -> {
                    Log.i("Schedule", group.getGroup());
                }, error -> {
                    Log.e("Schedule", error.toString(), error);
                });
    }
    @Override
    public void onStart() {
        super.onStart();
        setToday();
        daySelecter.setSelection(today);
    }
}
