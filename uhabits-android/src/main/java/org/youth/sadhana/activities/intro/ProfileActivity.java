package org.youth.sadhana.activities.intro;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.os.*;

import org.youth.sadhana.HabitsApplication;
import org.youth.sadhana.HabitsApplicationComponent;
import org.youth.sadhana.R;
import org.youth.sadhana.core.preferences.Preferences;

/**
 * Created by Kamlesh on 17-09-2017.
 */

public class ProfileActivity extends FragmentActivity  {

    protected HabitsApplicationComponent component;

    protected Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDependencies();
        setContentView(R.layout.profile);
    }

    private void initDependencies()
    {
        Context appContext = getApplicationContext();
        HabitsApplication app = (HabitsApplication) appContext;
        component = app.getComponent();
        preferences = component.getPreferences();
        LoginFragment.setPreferences(preferences);
    }
    @Override
    public void onBackPressed() {
        // Simply Do noting!
        if(!LoginFragment.isFirstTime)
            super.onBackPressed();
    }

}
