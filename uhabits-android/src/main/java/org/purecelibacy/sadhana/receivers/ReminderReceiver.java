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

package org.purecelibacy.sadhana.receivers;

import android.content.*;
import android.util.*;

import org.purecelibacy.sadhana.*;
import org.purecelibacy.sadhana.core.models.*;
import org.purecelibacy.sadhana.core.models.memory.MemoryModelFactory;
import org.purecelibacy.sadhana.core.models.sqlite.SQLModelFactory;
import org.purecelibacy.sadhana.core.utils.*;
import org.purecelibacy.sadhana.utils.DatabaseUtils;
import org.purecelibacy.sadhana.database.*;

import dagger.*;

import static android.content.ContentUris.*;

/**
 * The Android BroadcastReceiver for Loop Habit Tracker.
 * <p>
 * All broadcast messages are received and processed by this class.
 */
public class ReminderReceiver extends BroadcastReceiver
{
    public static final String ACTION_DISMISS_REMINDER =
        "org.purecelibacy.sadhana.ACTION_DISMISS_REMINDER";

    public static final String ACTION_SHOW_REMINDER =
        "org.purecelibacy.sadhana.ACTION_SHOW_REMINDER";

    public static final String ACTION_SNOOZE_REMINDER =
        "org.purecelibacy.sadhana.ACTION_SNOOZE_REMINDER";

    private static final String TAG = "ReminderReceiver";

    AndroidDatabase androidDatabase = null;

    /*@Override
    protected void finalize() throws Throwable {
        super.finalize();
        Log.i(TAG, "Finalize called :"+androidDatabase);
        if(androidDatabase != null)
            androidDatabase.close();
    }*/

    @Override
    public void onReceive(final Context context, Intent intent)
    {
        try {
            HabitsApplication app =
                    (HabitsApplication) context.getApplicationContext();

            ReminderComponent component = DaggerReminderReceiver_ReminderComponent
                    .builder()
                    .habitsApplicationComponent(app.getComponent())
                    .build();

            HabitList habits = app.getComponent().getHabitList();
            ReminderController reminderController =
                    component.getReminderController();

            System.out.println("################################## Inside ReminderReceiver");
            Log.i(TAG, String.format("Received intent: %s", intent.toString()));

            Habit habit = null;
            long today = DateUtils.getStartOfToday();

            if (intent.getData() != null)
                habit = habits.getById(parseId(intent.getData()));
            final Long timestamp = intent.getLongExtra("timestamp", today);
            final Long reminderTime = intent.getLongExtra("reminderTime", today);

            Log.i(TAG, "Habits:" + habits);
            for (Habit h : habits) {
                Log.i(TAG, "h:" + h);
            }

            if (habits.size() < 1) {
                Log.i(TAG, "Habit is null:");
                if (androidDatabase == null)
                    androidDatabase = new AndroidDatabase(DatabaseUtils.openDatabase());
                SQLModelFactory sqlModelFactory = new SQLModelFactory(androidDatabase);
                habits = sqlModelFactory.buildHabitList();
                for (Habit h : habits) {
                    sqlModelFactory.buildCheckmarkList(h);
                    sqlModelFactory.buildRepetitionList(h);
                }
                //habits = modelFactory.buildHabitList();
                habit = habits.getById(parseId(intent.getData()));

            }

            Log.i(TAG, "Habits:" + habits);
            for (Habit h : habits) {
                Log.i(TAG, "New h:" + h);
            }

            Log.i(TAG, "id:" + intent.getData() + "Habit:" + habit);
            try {
                switch (intent.getAction()) {
                    case ACTION_SHOW_REMINDER:
                        if (habit == null) return;
                        reminderController.onShowReminder(habit,
                                new Timestamp(timestamp), reminderTime);
                    /*if(androidDatabase != null) {
                        androidDatabase.close();
                        androidDatabase = null;
                    }*/
                        break;

                    case ACTION_DISMISS_REMINDER:
                        if (habit == null) return;
                        reminderController.onDismiss(habit);
                        break;

                    case ACTION_SNOOZE_REMINDER:
                        if (habit == null) return;
                        reminderController.onSnooze(habit);
                        break;

                    case Intent.ACTION_BOOT_COMPLETED:
                        reminderController.onBootCompleted();
                        break;
                }
            } catch (RuntimeException e) {
                Log.e(TAG, "could not process intent", e);
            }
        }catch(Exception e) {
            Log.e(TAG, "could not process intent", e);
        }
    }

    @ReceiverScope
    @Component(dependencies = HabitsApplicationComponent.class)
    interface ReminderComponent
    {
        ReminderController getReminderController();
    }
}
