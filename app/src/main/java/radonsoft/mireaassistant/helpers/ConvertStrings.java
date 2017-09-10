package radonsoft.mireaassistant.helpers;


import android.content.res.Resources;

import radonsoft.mireaassistant.R;

public class ConvertStrings {
    public String instituteNumber;
    public String instituteOutput;
    public void convertInstitutes(){
        switch (instituteNumber){
            case "0":
                instituteOutput = "Институт информационных технологий" ;
                break;
            case "1":
                instituteOutput = "Физико-технологический институт";
                break;
            case "3":
                instituteOutput = "Институт кибернетики";
                break;
            case "2":
                instituteOutput = "Институт инновационных технологий и государственного управления";
                break;
            case "4":
                instituteOutput = "Институт комплексной безопасности и специального приборостроения";
                break;
            case "5":
                instituteOutput = "Институт радиотехнических и телекоммуникацонных систем";
                break;
            case "7":
                instituteOutput = "Институт управления и стратегического развития организаций";
                break;
            default:
                instituteOutput = String.valueOf(instituteNumber);
                break;
        }
    }
}
