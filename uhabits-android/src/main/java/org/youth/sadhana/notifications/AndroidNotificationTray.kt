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

package org.youth.sadhana.notifications

import android.app.*
import android.content.*
import android.graphics.BitmapFactory.*
import android.support.v4.app.*
import android.support.v4.app.NotificationCompat.*
import org.youth.androidbase.*
import org.youth.sadhana.R
import org.youth.sadhana.core.*
import org.youth.sadhana.core.models.*
import org.youth.sadhana.core.preferences.*
import org.youth.sadhana.core.ui.*
import org.youth.sadhana.intents.*
import javax.inject.*

@AppScope
class AndroidNotificationTray
@Inject constructor(
        @AppContext private val context: Context,
        private val pendingIntents: PendingIntentFactory,
        private val preferences: Preferences,
        private val ringtoneManager: RingtoneManager
) : NotificationTray.SystemTray {

    override fun removeNotification(id: Int) {
        NotificationManagerCompat.from(context).cancel(id)
    }

    override fun showNotification(habit: Habit,
                                  notificationId: Int,
                                  timestamp: Timestamp,
                                  reminderTime: Long) {

        var checkAction : Action? = Action(
                R.drawable.ic_action_check,
                context.getString(R.string.check),
                pendingIntents.addCheckmark(habit, timestamp))

        val snoozeAction = Action(
                R.drawable.ic_action_snooze,
                context.getString(R.string.snooze),
                pendingIntents.snoozeNotification(habit))


        val wearableBg = decodeResource(context.resources, R.drawable.stripe)

        // Even though the set of actions is the same on the phone and
        // on the watch, Pebble requires us to add them to the
        // WearableExtender.
        val wearableExtender = WearableExtender()
                .setBackground(wearableBg)
                .addAction(snoozeAction)

        if(!habit.isNumerical)
            wearableExtender.addAction(checkAction)

        val builder = NotificationCompat.Builder(context)
        builder.setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(habit.name)
                .setContentText(habit.description)
                .setContentIntent(pendingIntents.showHabit(habit))
                .setDeleteIntent(pendingIntents.dismissNotification(habit))
                //.addAction(checkAction)
                .addAction(snoozeAction)
                .setSound(ringtoneManager.getURI())
                .extend(wearableExtender)
                .setWhen(reminderTime)
                .setShowWhen(true)
                .setOngoing(preferences.shouldMakeNotificationsSticky())

        if(!habit.isNumerical)
            builder.addAction(checkAction)

        val notification = builder.build()

        val notificationManager = context.getSystemService(
                Activity.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(notificationId, notification)
    }
}
