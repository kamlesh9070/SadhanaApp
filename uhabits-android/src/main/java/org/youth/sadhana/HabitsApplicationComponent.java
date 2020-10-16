/*
 * Copyright (C) 2016 Alinson Santos Xavier <isoron@gmail.com>
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

package org.youth.sadhana;

import android.content.*;

import org.youth.androidbase.*;
import org.youth.sadhana.core.*;
import org.youth.sadhana.core.commands.*;
import org.youth.sadhana.core.io.*;
import org.youth.sadhana.core.models.*;
import org.youth.sadhana.core.preferences.*;
import org.youth.sadhana.core.reminders.*;
import org.youth.sadhana.core.tasks.*;
import org.youth.sadhana.core.ui.*;
import org.youth.sadhana.core.ui.screens.habits.list.*;
import org.youth.sadhana.core.utils.*;
import org.youth.sadhana.intents.*;
import org.youth.sadhana.sync.*;
import org.youth.sadhana.tasks.*;
import org.youth.sadhana.widgets.*;

import dagger.*;

@AppScope
@Component(modules = {
    AppContextModule.class,
    HabitsModule.class,
    AndroidTaskRunner.class,
})
public interface HabitsApplicationComponent
{
    CommandRunner getCommandRunner();

    @AppContext
    Context getContext();

    CreateHabitCommandFactory getCreateHabitCommandFactory();

    EditHabitCommandFactory getEditHabitCommandFactory();

    GenericImporter getGenericImporter();

    HabitCardListCache getHabitCardListCache();

    HabitList getHabitList();

    HabitLogger getHabitsLogger();

    IntentFactory getIntentFactory();

    IntentParser getIntentParser();

    MidnightTimer getMidnightTimer();

    ModelFactory getModelFactory();

    NotificationTray getNotificationTray();

    PendingIntentFactory getPendingIntentFactory();

    Preferences getPreferences();

    ReminderScheduler getReminderScheduler();

    SyncManager getSyncManager();

    TaskRunner getTaskRunner();

    WidgetPreferences getWidgetPreferences();

    WidgetUpdater getWidgetUpdater();
}
