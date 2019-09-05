/*
 * Copyright (C) 2016 Álinson Santos Xavier <isoron@gmail.com>
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

package org.youth.sadhana.receivers;

import android.content.*;
import android.util.*;

import org.youth.sadhana.*;
import org.youth.sadhana.core.preferences.*;
import org.youth.sadhana.core.ui.widgets.*;
import org.youth.sadhana.intents.*;
import org.youth.sadhana.sync.*;

import dagger.*;

/**
 * The Android BroadcastReceiver for Loop Habit Tracker.
 * <p>
 * All broadcast messages are received and processed by this class.
 */
public class WidgetReceiver extends BroadcastReceiver
{
    public static final String ACTION_ADD_REPETITION =
        "org.youth.sadhana.ACTION_ADD_REPETITION";

    public static final String ACTION_DISMISS_REMINDER =
        "org.youth.sadhana.ACTION_DISMISS_REMINDER";

    public static final String ACTION_REMOVE_REPETITION =
        "org.youth.sadhana.ACTION_REMOVE_REPETITION";

    public static final String ACTION_TOGGLE_REPETITION =
            "org.youth.sadhana.ACTION_TOGGLE_REPETITION";

    @Override
    public void onReceive(final Context context, Intent intent)
    {
        HabitsApplication app =
            (HabitsApplication) context.getApplicationContext();

        WidgetComponent component = DaggerWidgetReceiver_WidgetComponent
            .builder()
            .habitsApplicationComponent(app.getComponent())
            .build();

        IntentParser parser = app.getComponent().getIntentParser();
        WidgetBehavior controller = component.getWidgetController();
        Preferences prefs = app.getComponent().getPreferences();

        if(prefs.isSyncEnabled())
            //context.startService(new Intent(context, SyncService.class));

        try
        {
            IntentParser.CheckmarkIntentData data;
            data = parser.parseCheckmarkIntent(intent);

            switch (intent.getAction())
            {
                case ACTION_ADD_REPETITION:
                    controller.onAddRepetition(data.getHabit(),
                        data.getTimestamp());
                    break;

                case ACTION_TOGGLE_REPETITION:
                    controller.onToggleRepetition(data.getHabit(),
                        data.getTimestamp());
                    break;

                case ACTION_REMOVE_REPETITION:
                    controller.onRemoveRepetition(data.getHabit(),
                        data.getTimestamp());
                    break;
            }
        }
        catch (RuntimeException e)
        {
            Log.e("WidgetReceiver", "could not process intent", e);
        }
    }

    @ReceiverScope
    @Component(dependencies = HabitsApplicationComponent.class)
    interface WidgetComponent
    {
        WidgetBehavior getWidgetController();
    }
}
