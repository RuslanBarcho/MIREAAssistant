package radonsoft.mireaassistant.helpers;

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
    public static GregorianCalendar fourthClassStart = new GregorianCalendar();
    public static GregorianCalendar fourthClassEnd = new GregorianCalendar();
    public static GregorianCalendar fifthClassStart = new GregorianCalendar();
    public static GregorianCalendar fifthClassEnd = new GregorianCalendar();

    public static Date firstClassStartDate, firstClassEndDate;

    public void setTime(){
        firstClassStart.set(setToday.get(Calendar.YEAR), setToday.get(Calendar.MONTH), setToday.get(Calendar.DAY_OF_MONTH), 9, 0, 0);
        firstClassEnd.set(setToday.get(Calendar.YEAR), setToday.get(Calendar.MONTH), setToday.get(Calendar.DAY_OF_MONTH), 10, 30, 0);

        secondClassStart.set(setToday.get(Calendar.YEAR), setToday.get(Calendar.MONTH), setToday.get(Calendar.DAY_OF_MONTH), 10, 40, 0);
        secondClassEnd.set(setToday.get(Calendar.YEAR), setToday.get(Calendar.MONTH), setToday.get(Calendar.DAY_OF_MONTH), 12, 10, 0);

        thirdClassStart.set(setToday.get(Calendar.YEAR), setToday.get(Calendar.MONTH), setToday.get(Calendar.DAY_OF_MONTH), 13, 0, 0);
        thirdClassEnd.set(setToday.get(Calendar.YEAR), setToday.get(Calendar.MONTH), setToday.get(Calendar.DAY_OF_MONTH), 14, 30, 0);

        fourthClassStart.set(setToday.get(Calendar.YEAR), setToday.get(Calendar.MONTH), setToday.get(Calendar.DAY_OF_MONTH), 14, 40, 0);
        fourthClassEnd.set(setToday.get(Calendar.YEAR), setToday.get(Calendar.MONTH), setToday.get(Calendar.DAY_OF_MONTH), 16, 10, 0);

        fifthClassStart.set(setToday.get(Calendar.YEAR), setToday.get(Calendar.MONTH), setToday.get(Calendar.DAY_OF_MONTH), 16, 20, 0);
        fifthClassEnd.set(setToday.get(Calendar.YEAR), setToday.get(Calendar.MONTH), setToday.get(Calendar.DAY_OF_MONTH), 17, 50, 0);
    }

    public void createDates(){
        firstClassStartDate = firstClassStart.getTime();
        firstClassEndDate = firstClassEnd.getTime();
    }

    public int getCurrentClass(){
        Date toCompare = setToday.getTime();
        if (firstClassStartDate.compareTo(toCompare) > 0){
            return 0;
        } else if (firstClassEndDate.compareTo(toCompare) > 0){
            return 1;
        } else {
            return 7;
        }
        /**
         * Syntaxis:
         * 0 - Time before first class
         * 7 - Time after classes
         * 1 - Class (FE first)
         * 12 - Break between classes (FE break between first and second)
         */
    }

    public boolean interval(GregorianCalendar now, GregorianCalendar timerStart){
        Date start = timerStart.getTime();
        Date toCompare = now.getTime();
        return  (start.compareTo(toCompare) > 0);
    }
}