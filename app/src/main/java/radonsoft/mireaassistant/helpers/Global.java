package radonsoft.mireaassistant.helpers;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import radonsoft.mireaassistant.forms.ScheduleForm;
import radonsoft.mireaassistant.model.schedule.Even;
import radonsoft.mireaassistant.model.schedule.Odd;
import radonsoft.mireaassistant.model.schedule.Response;
import radonsoft.mireaassistant.model.schedule.Schedule;
import radonsoft.mireaassistant.model.schedule.Schedule_;
import radonsoft.mireaassistant.network.NetworkSingleton;
import radonsoft.mireaassistant.network.ScheduleService;

/**
 * Created by Ruslan on 08.09.17.
 */
public class Global {
    public static int settingsDialogResume;

    public static int loginID = 0;
    public static int weekNumber;

    public static int instituteID;
    public static String groupID;

    public static ArrayList<String> scheduleNamesOdd = new ArrayList<>();
    public static ArrayList<String> scheduleRoomsOdd = new ArrayList<>();
    public static ArrayList<String> scheduleTeachersOdd = new ArrayList<>();
    public static ArrayList<String> scheduleTypeOdd = new ArrayList<>();

    public static ArrayList<String> scheduleNamesEven = new ArrayList<>();
    public static ArrayList<String> scheduleRoomsEven = new ArrayList<>();
    public static ArrayList<String> scheduleTeachersEven = new ArrayList<>();
    public static ArrayList<String> scheduleTypeEven = new ArrayList<>();

    public static String[] scheduleNamesOddString;
    public static String[] scheduleRoomsOddString;
    public static String[] scheduleTeachersOddString;
    public static String[] scheduleTypeOddString;

    public static String[] scheduleNamesEvenString;
    public static String[] scheduleRoomsEvenString;
    public static String[] scheduleTeachersEvenString;
    public static String[] scheduleTypeEvenString;

    public static ArrayList<String> institutes = new ArrayList<>();
    public static ArrayList<String> institutesCompiled = new ArrayList<>();
    public static ArrayList<String> institutesTranslited = new ArrayList<>();
    public static String instituteNameTranslited;
    public static String[] institutesString;

    public static ArrayList<String> groups = new ArrayList<>();
    public static ArrayList<String> groupsCompiled = new ArrayList<>();
    public static ArrayList<String> groupsTranslited = new ArrayList<>();
    public static String[] groupsString;
    public static String[] groupsStringTranslited;

    public void getScheduleOdd(Observer<Odd> observer) {
        NetworkSingleton.getRetrofit().create(ScheduleService.class)
                .getScheduleName(new ScheduleForm(172, instituteID, groupID))
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
                        char dash = '―';
                        return new Odd(dash, dash, dash, dash);
                    }
                })
                .subscribeWith(observer);
//                .subscribe((Odd odd) ->{
//                    Log.i("Schedule", odd.getName().toString());
//                } , error -> {
//                    Log.e("Schedule", error.toString(), error);
//                });
    }

    public void getScheduleEven(Observer<Even> observer) {
        NetworkSingleton.getRetrofit().create(ScheduleService.class)
                .getScheduleName(new ScheduleForm(172, instituteID, groupID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Schedule::getResponse)
                .map(Response::getSchedule)
                .map(Schedule_::getDays)
                .flatMapObservable(Observable::fromIterable)
                .flatMap(Observable::fromIterable)
                .map(day -> {
                    Even even = day.getEven();
                    if (even.getName() != null) {
                        return even;
                    } else {
                        char dash = '―';
                        return new Even(dash, dash, dash, dash);
                    }
                })
                .subscribeWith(observer);
//                .subscribe((Odd odd) ->{
//                    Log.i("Schedule", odd.getName().toString());
//                } , error -> {
//                    Log.e("Schedule", error.toString(), error);
//                });
    }
}
