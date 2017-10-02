package radonsoft.mireaassistant.helpers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import radonsoft.mireaassistant.forms.ScheduleForm;
import radonsoft.mireaassistant.model.Group;
import radonsoft.mireaassistant.model.RequestWrapper;
import radonsoft.mireaassistant.model.schedule.Even;
import radonsoft.mireaassistant.model.schedule.Odd;
import radonsoft.mireaassistant.model.schedule.Response;
import radonsoft.mireaassistant.model.schedule.Schedule;
import radonsoft.mireaassistant.model.schedule.Schedule_;
import radonsoft.mireaassistant.network.InstitutesService;
import radonsoft.mireaassistant.network.NetworkSingleton;
import radonsoft.mireaassistant.network.ScheduleService;

/**
 * Created by Ruslan on 08.09.17.
 */
public class Global {
    public static int settingsDialogResume;

    public static boolean groupsSolo = false;

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

    public static String[] scheduleNamesOddStringBackup;
    public static String[] scheduleRoomsOddStringBackup;
    public static String[] scheduleTeachersOddStringBackup;
    public static String[] scheduleTypeOddStringBackup;

    public static String[] scheduleNamesEvenStringBackup;
    public static String[] scheduleRoomsEvenStringBackup;
    public static String[] scheduleTeachersEvenStringBackup;
    public static String[] scheduleTypeEvenStringBackup;

    public static ArrayList<String> institutes = new ArrayList<>();
    public static ArrayList<String> institutesCompiled = new ArrayList<>();
    public static ArrayList<String> institutesTranslited = new ArrayList<>();
    public static String[] institutesString;
    public static String[] institutesStringIntegers;
    public static ArrayList<String> groups = new ArrayList<>();
    public static ArrayList<String> groupsCompiled = new ArrayList<>();
    public static ArrayList<String> groupsTranslited = new ArrayList<>();
    public static String[] groupsString;
    public static String[] groupsStringTranslited;

    public void restoreSchedule(){
        for ( int i = 0; i < scheduleNamesOddStringBackup.length; i++){
            scheduleNamesOdd.add(scheduleNamesOddStringBackup[i]);
            scheduleRoomsOdd.add(scheduleRoomsOddStringBackup[i]);
            scheduleTeachersOdd.add(scheduleTeachersOddStringBackup[i]);
            scheduleTypeOdd.add(scheduleTypeOddStringBackup[i]);

            scheduleNamesEven.add(scheduleNamesEvenStringBackup[i]);
            scheduleRoomsEven.add(scheduleRoomsEvenStringBackup[i]);
            scheduleTeachersEven.add(scheduleTeachersEvenStringBackup[i]);
            scheduleTypeEven.add(scheduleTypeEvenStringBackup[i]);
        }
    }

    public void backupSchedule(){
        Global.scheduleNamesOddStringBackup = Global.scheduleNamesOdd.toArray(new String[Global.scheduleNamesOdd.size()]);
        Global.scheduleRoomsOddStringBackup = Global.scheduleRoomsOdd.toArray(new String[Global.scheduleRoomsOdd.size()]);
        Global.scheduleTeachersOddStringBackup = Global.scheduleTeachersOdd.toArray(new String[Global.scheduleTeachersOdd.size()]);
        Global.scheduleTypeOddStringBackup = Global.scheduleTypeOdd.toArray(new String[Global.scheduleTypeOdd.size()]);

        Global.scheduleNamesEvenStringBackup = Global.scheduleNamesEven.toArray(new String[Global.scheduleNamesEven.size()]);
        Global.scheduleRoomsEvenStringBackup = Global.scheduleRoomsEven.toArray(new String[Global.scheduleRoomsEven.size()]);
        Global.scheduleTeachersEvenStringBackup = Global.scheduleTeachersEven.toArray(new String[Global.scheduleTeachersEven.size()]);
        Global.scheduleTypeEvenStringBackup = Global.scheduleTypeEven.toArray(new String[Global.scheduleTypeEven.size()]);
    }

    public int term(){
        String construct;
        GregorianCalendar calendar = new GregorianCalendar();
        GregorianCalendar toCompare = new GregorianCalendar();
        toCompare.set(calendar.get(Calendar.YEAR), Calendar.AUGUST, 15, 9, 0, 0);
        construct = String.valueOf(calendar.get(Calendar.YEAR));
        construct = construct.substring(2,4);
        Date dateOne = calendar.getTime();
        Date dateTwo = toCompare.getTime();
        if (dateOne.compareTo(dateTwo) > 0){
            construct = construct + "2";
        } else {
            construct = construct + "1";
        }
        return Integer.valueOf(construct);
    }

    public void getScheduleOdd(Observer<Odd> observer) {
        NetworkSingleton.getRetrofit().create(ScheduleService.class)
                .getScheduleName(new ScheduleForm(term(), instituteID, groupID))
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

    public void getGroupsAndInsts(Observer<Group> observer){
        NetworkSingleton.getRetrofit().create(InstitutesService.class)
                .getInstitutes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(RequestWrapper::getResponse)
                .map(radonsoft.mireaassistant.model.Response::getGroups)
                .toObservable()
                .flatMap(Observable::fromIterable)
                .subscribeWith(observer);
    }

    public void getScheduleEven(Observer<Even> observer) {
        NetworkSingleton.getRetrofit().create(ScheduleService.class)
                .getScheduleName(new ScheduleForm(term(), instituteID, groupID))
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
