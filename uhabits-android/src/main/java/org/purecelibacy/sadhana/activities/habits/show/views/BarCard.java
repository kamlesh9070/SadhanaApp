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

package org.purecelibacy.sadhana.activities.habits.show.views;

import android.content.*;
import android.support.annotation.*;
import android.util.*;
import android.widget.*;

import org.purecelibacy.sadhana.*;
import org.purecelibacy.sadhana.R;
import org.purecelibacy.sadhana.activities.common.views.*;
import org.purecelibacy.sadhana.core.models.*;
import org.purecelibacy.sadhana.core.tasks.*;
import org.purecelibacy.sadhana.core.utils.*;
import org.purecelibacy.sadhana.utils.*;

import java.util.*;

import butterknife.*;

public class BarCard extends HabitCard
{
    @BindView(R.id.barChart)
    BarChart chart;

    @BindView(R.id.title)
    TextView title;

    @Nullable
    private TaskRunner taskRunner;

    public BarCard(Context context)
    {
        super(context);
        init();
    }

    public BarCard(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    @Override
    protected void refreshData()
    {
        if(taskRunner == null) return;
        taskRunner.execute(new RefreshTask(getHabit()));
    }

    private void init()
    {
        inflate(getContext(), R.layout.show_habit_bar, this);
        ButterKnife.bind(this);

        Context appContext = getContext().getApplicationContext();
        if (appContext instanceof HabitsApplication)
        {
            HabitsApplication app = (HabitsApplication) appContext;
            taskRunner = app.getComponent().getTaskRunner();
        }

        if (isInEditMode()) initEditMode();
    }

    private void initEditMode()
    {
        int color = PaletteUtils.getAndroidTestColor(1);
        title.setTextColor(color);
        chart.setColor(color);
        chart.populateWithRandomData();
    }

    private class RefreshTask implements Task
    {
        private final Habit habit;

        public RefreshTask(Habit habit) {this.habit = habit;}

        @Override
        public void doInBackground()
        {
            Timestamp today = DateUtils.getToday();
            List<Checkmark> checkmarks =
                habit.getCheckmarks().getByInterval(Timestamp.ZERO, today);
            chart.setCheckmarks(checkmarks);
        }

        @Override
        public void onPreExecute()
        {
            int color = PaletteUtils.getColor(getContext(), habit.getColor());
            title.setTextColor(color);
            chart.setColor(color);
            chart.setTarget(habit.getTargetValue());
        }
    }
}
