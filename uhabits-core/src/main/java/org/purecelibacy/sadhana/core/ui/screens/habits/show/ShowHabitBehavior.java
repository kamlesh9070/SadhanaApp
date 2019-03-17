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

package org.purecelibacy.sadhana.core.ui.screens.habits.show;

import android.support.annotation.*;

import org.purecelibacy.sadhana.core.commands.*;
import org.purecelibacy.sadhana.core.models.*;

import javax.inject.*;

public class ShowHabitBehavior
{
    private HabitList habitList;

    @NonNull
    private final Habit habit;

    @NonNull
    private final CommandRunner commandRunner;

    @NonNull
    private Screen screen;

    @Inject
    public ShowHabitBehavior(@NonNull HabitList habitList,
                             @NonNull CommandRunner commandRunner,
                             @NonNull Habit habit,
                             @NonNull Screen screen)
    {
        this.habitList = habitList;
        this.habit = habit;
        this.commandRunner = commandRunner;
        this.screen = screen;
    }

    public void onEditHistory()
    {
        screen.showEditHistoryScreen();
    }

    public void onToggleCheckmark(Timestamp timestamp)
    {
        String remark = null;
        if(habit.getRepetitions() != null && habit.getRepetitions().getByTimestamp(timestamp) != null)
            remark = habit.getRepetitions().getByTimestamp(timestamp).getRemark();
        commandRunner.execute(
            new ToggleRepetitionCommand(habitList, habit, timestamp, remark), null);
    }

    public interface Screen
    {
        void showEditHistoryScreen();
    }
}
