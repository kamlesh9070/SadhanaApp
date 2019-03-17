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

package org.purecelibacy.sadhana.widgets

import android.content.*
import android.view.*
import android.view.ViewGroup.*
import android.view.ViewGroup.LayoutParams.*
import org.purecelibacy.sadhana.activities.common.views.*
import org.purecelibacy.sadhana.core.models.*
import org.purecelibacy.sadhana.utils.*
import org.purecelibacy.sadhana.widgets.views.*

class StreakWidget(
        context: Context,
        id: Int,
        private val habit: Habit
) : BaseWidget(context, id) {

    override fun getOnClickPendingIntent(context: Context) =
            pendingIntentFactory.showHabit(habit)

    override fun refreshData(view: View) {
        val widgetView = view as GraphWidgetView
        (widgetView.dataView as StreakChart).apply {
            setColor(PaletteUtils.getColor(context, habit.color))
            setStreaks(habit.streaks.getBest(maxStreakCount))
        }
    }

    override fun buildView(): View {
        return GraphWidgetView(context, StreakChart(context)).apply {
            setTitle(habit.name)
            layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        }
    }

    override fun getDefaultHeight() = 200
    override fun getDefaultWidth() = 200
}
