/*
 * Copyright (C) 2017 Alinson Santos Xavier <isoron@gmail.com>
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
 *
 *
 */

package org.youth.sadhana.core.models.sqlite.records;

import org.youth.sadhana.core.database.*;
import org.youth.sadhana.core.models.*;

/**
 * The SQLite database record corresponding to a {@link Repetition}.
 */
@Table(name = "Repetitions")
public class RepetitionRecord
{
    public HabitRecord habit;

    @Column(name = "habit")
    public Long habit_id;

    @Column
    public Long timestamp;

    @Column
    public Integer value;

    @Column
    public Long id;

    @Column
    public String remark;

    public void copyFrom(Repetition repetition)
    {
        timestamp = repetition.getTimestamp().getUnixTime();
        value = repetition.getValue();
        remark = repetition.getRemark();
    }

    public Repetition toRepetition()
    {
        return new Repetition(new Timestamp(timestamp), value, remark);
    }
}
