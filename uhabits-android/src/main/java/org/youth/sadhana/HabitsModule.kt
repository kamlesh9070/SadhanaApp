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

package org.youth.sadhana

import dagger.*
import org.youth.sadhana.core.*
import org.youth.sadhana.core.commands.*
import org.youth.sadhana.core.database.*
import org.youth.sadhana.core.models.*
import org.youth.sadhana.core.models.sqlite.*
import org.youth.sadhana.core.preferences.*
import org.youth.sadhana.core.reminders.*
import org.youth.sadhana.core.tasks.*
import org.youth.sadhana.core.ui.*
import org.youth.sadhana.database.*
import org.youth.sadhana.intents.*
import org.youth.sadhana.notifications.*
import org.youth.sadhana.preferences.*
import org.youth.sadhana.utils.*

@Module
class HabitsModule {
    @Provides
    @AppScope
    fun getPreferences(storage: SharedPreferencesStorage): Preferences {
        return Preferences(storage)
    }

    @Provides
    @AppScope
    fun getReminderScheduler(
            sys: IntentScheduler,
            commandRunner: CommandRunner,
            habitList: HabitList
    ): ReminderScheduler {
        return ReminderScheduler(commandRunner, habitList, sys)
    }

    @Provides
    @AppScope
    fun getTray(
            taskRunner: TaskRunner,
            commandRunner: CommandRunner,
            preferences: Preferences,
            screen: AndroidNotificationTray
    ): NotificationTray {
        return NotificationTray(taskRunner, commandRunner, preferences, screen)
    }

    @Provides
    @AppScope
    fun getWidgetPreferences(
            storage: SharedPreferencesStorage
    ): WidgetPreferences {
        return WidgetPreferences(storage)
    }

    @Provides
    @AppScope
    fun getModelFactory(): ModelFactory {
        return SQLModelFactory(AndroidDatabase(DatabaseUtils.openDatabase()))
    }

    @Provides
    @AppScope
    fun getHabitList(list: SQLiteHabitList): HabitList {
        return list
    }

    @Provides
    @AppScope
    fun getDatabaseOpener(opener: AndroidDatabaseOpener): DatabaseOpener {
        return opener
    }
}

