/*
 * Copyright (C) 2017 Álinson Santos Xavier <isoron@gmail.com>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.purecelibacy.sadhana.activities.about;

import android.support.annotation.*;
import android.widget.*;

import org.purecelibacy.androidbase.activities.*;
import org.purecelibacy.sadhana.R;
import org.purecelibacy.sadhana.core.ui.screens.about.*;
import org.purecelibacy.sadhana.intents.*;

import javax.inject.*;

import static org.purecelibacy.sadhana.core.ui.screens.about.AboutBehavior.Message.*;

public class AboutScreen extends BaseScreen implements AboutBehavior.Screen
{
    @NonNull
    private final IntentFactory intents;

    @Inject
    public AboutScreen(@NonNull BaseActivity activity,
                       @NonNull IntentFactory intents)
    {
        super(activity);
        this.intents = intents;
    }

    @Override
    public void showMessage(AboutBehavior.Message message)
    {
        if (message == YOU_ARE_NOW_A_DEVELOPER) Toast
            .makeText(activity, "You are now a developer", Toast.LENGTH_LONG)
            .show();
    }

    @Override
    public void showRateAppWebsite()
    {
        activity.startActivity(intents.rateApp(activity));
    }

    @Override
    public void showSendFeedbackScreen()
    {
        activity.startActivity(intents.sendFeedback(activity));
    }

    public void showSendBugReportToDeveloperScreen(String log) {
        int to = R.string.bugReportTo;
        int subject = R.string.bugReportSubject;
        showSendEmailScreen(to, subject, log);
    }

    @Override
    public void showSourceCodeWebsite()
    {
        activity.startActivity(intents.viewSourceCode(activity));
    }

    @Override
    public void showTranslationWebsite()
    {
        activity.startActivity(intents.helpTranslate(activity));
    }
}
