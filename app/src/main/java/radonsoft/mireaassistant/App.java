package radonsoft.mireaassistant;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by Ruslan on 16.09.17.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
