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

package org.purecelibacy.sadhana.core.commands;

import android.support.annotation.*;

import org.purecelibacy.sadhana.core.models.*;

/**
 * Command to toggle a repetition.
 */
public class ToggleRepetitionCommand extends Command
{
    @NonNull
    private HabitList list;

    final Timestamp timestamp;

    @NonNull
    final Habit habit;

    String remark;

    public ToggleRepetitionCommand(@NonNull HabitList list,
                                   @NonNull Habit habit,
                                   Timestamp timestamp)
    {
        super();
        this.list = list;
        this.timestamp = timestamp;
        this.habit = habit;
    }

    public ToggleRepetitionCommand(@NonNull HabitList list,
                                   @NonNull Habit habit,
                                   Timestamp timestamp,
                                   String remark)
    {
        super();
        this.list = list;
        this.timestamp = timestamp;
        this.habit = habit;
        this.remark = remark;
    }

    @Override
    public void execute()
    {
        habit.getRepetitions().toggle(timestamp,remark);
        list.update(habit);
    }

    @NonNull
    public Habit getHabit()
    {
        return habit;
    }

    @Override
    @NonNull
    public Record toRecord()
    {
        return new Record(this);
    }

    @Override
    public void undo()
    {
        execute();
    }

    public static class Record
    {
        @NonNull
        public String id;

        @NonNull
        public String event = "Toggle";

        public long habit;

        public long repTimestamp;
        public String remark;

        public Record(@NonNull ToggleRepetitionCommand command)
        {
            id = command.getId();
            Long habitId = command.habit.getId();
            if (habitId == null) throw new RuntimeException("Habit not saved");

            this.repTimestamp = command.timestamp.getUnixTime();
            this.habit = habitId;
            this.remark = command.remark;
        }

        public ToggleRepetitionCommand toCommand(@NonNull HabitList habitList)
        {
            Habit h = habitList.getById(habit);
            if (h == null) throw new HabitNotFoundException();

            ToggleRepetitionCommand command;
            command = new ToggleRepetitionCommand(
                habitList, h, new Timestamp(repTimestamp), remark);
            command.setId(id);
            return command;
        }
    }
}