package radonsoft.mireaassistant.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import radonsoft.mireaassistant.MainActivity;
import radonsoft.mireaassistant.R;
import radonsoft.mireaassistant.forms.ScheduleForm;
import radonsoft.mireaassistant.helpers.Global;
import radonsoft.mireaassistant.model.schedule.Odd;
import radonsoft.mireaassistant.model.schedule.Response;
import radonsoft.mireaassistant.model.schedule.Schedule;
import radonsoft.mireaassistant.model.schedule.Schedule_;
import radonsoft.mireaassistant.network.NetworkSingleton;
import radonsoft.mireaassistant.network.ScheduleService;


public class VRAccess extends Fragment {
    private View mRootView;
    public Button getInstitutesBtn;
    public Button getGroupsBtn;
    public Button clear;
    public TextView output;
    public ArrayList<String> oddNames = new ArrayList();
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
                //Global.scheduleNamesOddString = Global.scheduleNamesOdd.toArray(new String[Global.scheduleNamesOdd.size()]);
                //output.setText(String.valueOf(Global.scheduleNamesOdd));
                output.setText(String.valueOf(Global.scheduleRoomsOdd));
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
                Date date = new Date();
                output.setText(date.toString().substring(30, 34));
                Calendar calendar = Calendar.getInstance();
                output.setText(String.valueOf(calendar.get(Calendar.YEAR)));
            }
        });

        return mRootView;
    }

    public void getSchedule() {
        NetworkSingleton.getRetrofit().create(ScheduleService.class)
                .getScheduleName(new ScheduleForm(172, 0, "ikbo-02-17"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Schedule::getResponse)
                .map(Response::getSchedule)
                .map(Schedule_::getDays)
                .flatMapObservable(Observable::fromIterable)
                .flatMap(Observable::fromIterable)
                .map(day -> {
                    Odd odd = day.getOdd();
                    if (odd.getName() != null) {
                        return odd;
                    } else {
                        char dash = '-';
                      return new Odd(dash, dash, dash, dash);
                    }
                })
                .subscribe((Odd odd) ->{
                    Log.i("Schedule", odd.getName().toString());
                } , error -> {
                    Log.e("Schedule", error.toString(), error);
                });
    }

}