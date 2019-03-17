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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Spinner;
import android.widget.TextView;

import org.purecelibacy.sadhana.HabitsApplication;
import org.purecelibacy.sadhana.R;
import org.purecelibacy.sadhana.activities.common.views.CheckmarkSummaryChart;
import org.purecelibacy.sadhana.core.models.Checkmark;
import org.purecelibacy.sadhana.core.models.CheckmarkList;
import org.purecelibacy.sadhana.core.preferences.Preferences;
import org.purecelibacy.sadhana.core.tasks.Task;
import org.purecelibacy.sadhana.core.tasks.TaskRunner;
import org.purecelibacy.sadhana.core.utils.DateUtils;
import org.purecelibacy.sadhana.utils.PaletteUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;

public class CheckmarkSummaryCard extends HabitCard
{
    public static final int[] BUCKET_SIZES = { 7, 31, 92, 365 };

    @BindView(R.id.spinner)
    Spinner spinner;

    @BindView(R.id.checkmarkView)
    CheckmarkSummaryChart chart;

    @BindView(R.id.title)
    TextView title;

    private int bucketSize;

    @Nullable
    private TaskRunner taskRunner;

    @Nullable
    private Preferences prefs;

    public CheckmarkSummaryCard(Context context)
    {
        super(context);
        init();
    }

    public CheckmarkSummaryCard(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    @NonNull
    public static DateUtils.TruncateField getTruncateField(int bucketSize)
    {
        if (bucketSize == 7) return DateUtils.TruncateField.WEEK_NUMBER;
        if (bucketSize == 31) return DateUtils.TruncateField.MONTH;
        if (bucketSize == 92) return DateUtils.TruncateField.QUARTER;
        if (bucketSize == 365) return DateUtils.TruncateField.YEAR;

        Log.e("CheckmarkCard",
            String.format("Unknown bucket size: %d", bucketSize));

        return DateUtils.TruncateField.MONTH;
    }

    @OnItemSelected(R.id.spinner)
    public void onItemSelected(int position)
    {
        setBucketSizeFromPosition(position);
        HabitsApplication app =
            (HabitsApplication) getContext().getApplicationContext();
        app.getComponent().getWidgetUpdater().updateWidgets();
        refreshData();
    }

    @Override
    protected void refreshData()
    {
        if(taskRunner == null) return;
        taskRunner.execute(new RefreshTask());
    }

    private int getDefaultSpinnerPosition()
    {
        if(prefs == null) return 0;
        return prefs.getDefaultCheckmarkSpinnerPosition();
    }

    private void init()
    {
        Context appContext = getContext().getApplicationContext();
        if (appContext instanceof HabitsApplication)
        {
            HabitsApplication app = (HabitsApplication) appContext;
            taskRunner = app.getComponent().getTaskRunner();
            prefs = app.getComponent().getPreferences();
        }

        inflate(getContext(), R.layout.show_habit_checkmark_summary, this);
        ButterKnife.bind(this);

        int defaultPosition = getDefaultSpinnerPosition();
        setBucketSizeFromPosition(defaultPosition);
        spinner.setSelection(defaultPosition);

        if (isInEditMode())
        {
            spinner.setVisibility(GONE);
            title.setTextColor(PaletteUtils.getAndroidTestColor(1));
            chart.setColor(PaletteUtils.getAndroidTestColor(1));
            chart.populateWithRandomData();
        }
    }

    private void setBucketSizeFromPosition(int position)
    {
        if(prefs == null) return;
        prefs.setDefaultCheckmarkSpinnerPosition(position);
        bucketSize = BUCKET_SIZES[position];
    }

    private class RefreshTask implements Task
    {
        @Override
        public void doInBackground()
        {
            List<Checkmark> checkmarks;
            CheckmarkList checkmarkList = getHabit().getCheckmarks();
            if (bucketSize == 1) checkmarks = checkmarkList.toList();
            else checkmarks = checkmarkList.groupBy(getTruncateField(bucketSize));

            chart.setCheckmarks(checkmarks);
            chart.setBucketSize(bucketSize);
        }

        @Override
        public void onPreExecute()
        {
            int color =
                PaletteUtils.getColor(getContext(), getHabit().getColor());
            title.setTextColor(color);
            chart.setColor(color);
        }
    }
}
