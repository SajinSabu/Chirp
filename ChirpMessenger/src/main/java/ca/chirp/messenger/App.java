package ca.chirp.messenger;

import android.app.Application;
import android.content.res.Resources;

import com.firebase.client.Firebase;
import ca.chirp.chirpmessenger.R;

public class App extends Application {

    @Override
    public void onCreate() {
        Firebase.setAndroidContext(this);
        initSingletons(getResources());
        super.onCreate();
    }

    public void initSingletons(Resources resources) {
        MainDAO.initInstance(resources);
    }

}
