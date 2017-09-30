package radonsoft.mireaassistant.helpers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Ruslan Vinter on 25.09.2017.
 */
public class TimeManager  {

    public static GregorianCalendar setToday = new GregorianCalendar();
    public static GregorianCalendar firstClassStart = new GregorianCalendar();
    public static GregorianCalendar firstClassEnd = new GregorianCalendar();
    public static GregorianCalendar secondClassStart = new GregorianCalendar();
    public static GregorianCalendar secondClassEnd = new GregorianCalendar();
    public static GregorianCalendar thirdClassStart = new GregorianCalendar();
    public static GregorianCalendar thirdClassEnd = new GregorianCalendar();

    public static Date firstClassStartDate, firstClassEndDate;

    public void setTime(){
        firstClassStart.set(setToday.get(Calendar.YEAR), setToday.get(Calendar.MONTH), setToday.get(Calendar.DAY_OF_MONTH), 9, 0, 0);
        firstClassEnd.set(setToday.get(Calendar.YEAR), setToday.get(Calendar.MONTH), setToday.get(Calendar.DAY_OF_MONTH), 10, 30, 0);

        secondClassStart.set(setToday.get(Calendar.YEAR), setToday.get(Calendar.MONTH), setToday.get(Calendar.DAY_OF_MONTH), 10, 40, 0);
        secondClassEnd.set(setToday.get(Calendar.YEAR), setToday.get(Calendar.MONTH), setToday.get(Calendar.DAY_OF_MONTH), 12, 10, 0);
    }

    public void createDates(){
        firstClassStartDate = firstClassStart.getTime();
        firstClassEndDate = firstClassEnd.getTime();
    }

    public boolean interval(GregorianCalendar now, GregorianCalendar timerStart){
        Date start = timerStart.getTime();
        Date toCompare = now.getTime();
        return  (start.compareTo(toCompare) > 0);
    }
}