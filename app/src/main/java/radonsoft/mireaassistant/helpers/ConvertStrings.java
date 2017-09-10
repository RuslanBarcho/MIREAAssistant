package radonsoft.mireaassistant.helpers;


import android.content.res.Resources;

import radonsoft.mireaassistant.R;

public class ConvertStrings {
    int instituteNumber;
    String instituteOutput;
    public void convertInstitutes(){
        switch (instituteNumber){
            case 7:
                instituteOutput = Resources.getSystem().getString(R.string.IT);
                break;
            default:

                break;
        }
    }
}
