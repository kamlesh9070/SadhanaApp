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

package org.youth.sadhana

import dagger.*
import org.youth.androidbase.activities.*
import org.youth.sadhana.activities.*
import org.youth.sadhana.activities.about.*
import org.youth.sadhana.activities.habits.list.*
import org.youth.sadhana.activities.habits.show.*
import org.youth.sadhana.core.ui.screens.habits.list.*
import org.mockito.Mockito.*

@Module
class TestModule {
    @Provides fun ListHabitsBehavior() = mock(ListHabitsBehavior::class.java)
}

@ActivityScope
@Component(modules = arrayOf(
        ActivityContextModule::class,
        AboutModule::class,
        HabitsActivityModule::class,
        ListHabitsModule::class,
        ShowHabitModule::class,
        HabitModule::class,
        TestModule::class
), dependencies = arrayOf(HabitsApplicationComponent::class))
interface HabitsActivityTestComponent {
    fun getCheckmarkPanelViewFactory(): CheckmarkPanelViewFactory
    fun getHabitCardViewFactory(): HabitCardViewFactory
    fun getCheckmarkButtonViewFactory(): CheckmarkButtonViewFactory
    fun getNumberButtonViewFactory(): NumberButtonViewFactory
    fun getNumberPanelViewFactory(): NumberPanelViewFactory
}