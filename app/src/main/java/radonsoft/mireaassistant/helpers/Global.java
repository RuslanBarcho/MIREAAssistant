package radonsoft.mireaassistant.helpers;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import radonsoft.mireaassistant.forms.ScheduleForm;
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
    public static int loginID = 0;
    public static int weekNumber;

    public void getScheduleOdd(Observer<Odd> observer) {
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
                .subscribeWith(observer);
//                .subscribe((Odd odd) ->{
//                    Log.i("Schedule", odd.getName().toString());
//                } , error -> {
//                    Log.e("Schedule", error.toString(), error);
//                });
    }
}
