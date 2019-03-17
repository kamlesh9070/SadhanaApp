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

package org.purecelibacy.sadhana;

import android.content.*;

import org.purecelibacy.androidbase.*;
import org.purecelibacy.sadhana.core.*;
import org.purecelibacy.sadhana.core.commands.*;
import org.purecelibacy.sadhana.core.io.*;
import org.purecelibacy.sadhana.core.models.*;
import org.purecelibacy.sadhana.core.preferences.*;
import org.purecelibacy.sadhana.core.reminders.*;
import org.purecelibacy.sadhana.core.tasks.*;
import org.purecelibacy.sadhana.core.ui.*;
import org.purecelibacy.sadhana.core.ui.screens.habits.list.*;
import org.purecelibacy.sadhana.core.utils.*;
import org.purecelibacy.sadhana.intents.*;
import org.purecelibacy.sadhana.sync.*;
import org.purecelibacy.sadhana.tasks.*;
import org.purecelibacy.sadhana.widgets.*;

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
