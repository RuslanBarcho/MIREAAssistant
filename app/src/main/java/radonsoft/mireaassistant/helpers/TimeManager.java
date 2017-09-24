package radonsoft.mireaassistant.helpers;

import android.app.Fragment;

import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ruska on 25.09.2017.
 */
public class TimeManager  {

    GregorianCalendar setToday = new GregorianCalendar();
    GregorianCalendar firstClassStart = new GregorianCalendar();
    GregorianCalendar firstClassEnd = new GregorianCalendar();

    public void setTime(){
        firstClassStart.set(setToday.get(Calendar.YEAR), setToday.get(Calendar.MONTH), setToday.get(Calendar.DAY_OF_MONTH), 9, 0, 0);
        firstClassEnd.set(setToday.get(Calendar.YEAR), setToday.get(Calendar.MONTH), setToday.get(Calendar.DAY_OF_MONTH), 10, 30, 0);
    }

}