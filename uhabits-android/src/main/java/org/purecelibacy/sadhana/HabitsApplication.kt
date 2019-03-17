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

package org.purecelibacy.sadhana

import android.app.*
import android.content.*
import android.support.multidex.MultiDexApplication
import org.purecelibacy.androidbase.*
import org.purecelibacy.sadhana.core.database.*
import org.purecelibacy.sadhana.core.reminders.*
import org.purecelibacy.sadhana.core.ui.*
import org.purecelibacy.sadhana.utils.*
import org.purecelibacy.sadhana.widgets.*
import java.io.*
import android.support.multidex.MultiDex



/**
 * The Android application for Loop Habit Tracker.
 */
class HabitsApplication : MultiDexApplication()  {

    private lateinit var context: Context
    private lateinit var widgetUpdater: WidgetUpdater
    private lateinit var reminderScheduler: ReminderScheduler
    private lateinit var notificationTray: NotificationTray

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
    override fun onCreate() {
        super.onCreate()
        context = this
        HabitsApplication.component = DaggerHabitsApplicationComponent
                .builder()
                .appContextModule(AppContextModule(context))
                .build()

        if (isTestMode()) {
            val db = DatabaseUtils.getDatabaseFile(context)
            if (db.exists()) db.delete()
        }

        try {
            DatabaseUtils.initializeDatabase(context)
        } catch (e: UnsupportedDatabaseVersionException) {
            val db = DatabaseUtils.getDatabaseFile(context)
            db.renameTo(File(db.absolutePath + ".invalid"))
            DatabaseUtils.initializeDatabase(context)
        }

        widgetUpdater = component.widgetUpdater
        widgetUpdater.startListening()

        reminderScheduler = component.reminderScheduler
        reminderScheduler.startListening()

        notificationTray = component.notificationTray
        notificationTray.startListening()

        val prefs = component.preferences
        prefs.setLastAppVersion(BuildConfig.VERSION_CODE)

        val taskRunner = component.taskRunner
        taskRunner.execute {
            reminderScheduler.scheduleAll()
            widgetUpdater.updateWidgets()
        }
    }

    override fun onTerminate() {
        reminderScheduler.stopListening()
        widgetUpdater.stopListening()
        notificationTray.stopListening()
        super.onTerminate()
    }

    val component: HabitsApplicationComponent
        get() = HabitsApplication.component

    companion object {
        lateinit var component: HabitsApplicationComponent

        fun isTestMode(): Boolean {
            try {
                Class.forName("org.purecelibacy.sadhana.BaseAndroidTest")
                return true
            } catch (e: ClassNotFoundException) {
                return false
            }
        }
    }
}
