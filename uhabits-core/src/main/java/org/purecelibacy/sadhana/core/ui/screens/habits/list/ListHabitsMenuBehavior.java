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

package org.purecelibacy.sadhana.core.ui.screens.habits.list;


import android.support.annotation.*;

import com.sun.jndi.toolkit.url.Uri;

import org.purecelibacy.sadhana.core.Config;
import org.purecelibacy.sadhana.core.models.*;
import org.purecelibacy.sadhana.core.preferences.*;
import org.purecelibacy.sadhana.core.tasks.ExportCSVTask;
import org.purecelibacy.sadhana.core.tasks.TaskRunner;
import org.purecelibacy.sadhana.core.ui.*;
import org.purecelibacy.sadhana.core.utils.DateUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.inject.*;

public class ListHabitsMenuBehavior
{
    @NonNull
    private final HabitList habitList;

    @NonNull
    private final Screen screen;

    @NonNull
    private final Adapter adapter;

    @NonNull
    private final Preferences preferences;

    @NonNull
    private final ThemeSwitcher themeSwitcher;

    private boolean showCompleted;

    private boolean showArchived;

    @NonNull
    private final ListHabitsBehavior.DirFinder dirFinder;

    @NonNull
    private final TaskRunner taskRunner;


    @Inject
    public ListHabitsMenuBehavior(@NonNull Screen screen,
                                  @NonNull Adapter adapter,
                                  @NonNull Preferences preferences,
                                  @NonNull ThemeSwitcher themeSwitcher,
                                  @NonNull HabitList habitList,
                                  @NonNull ListHabitsBehavior.DirFinder dirFinder,
                                  @NonNull TaskRunner taskRunner)
    {
        this.screen = screen;
        this.adapter = adapter;
        this.preferences = preferences;
        this.themeSwitcher = themeSwitcher;
        this.habitList = habitList;
        this.taskRunner = taskRunner;
        this.dirFinder = dirFinder;
        showCompleted = preferences.getShowCompleted();
        showArchived = preferences.getShowArchived();
        updateAdapterFilter();
    }

    public void onCreateHabit()
    {
        screen.showCreateHabitScreen();
    }

    public void onViewFAQ()
    {
        screen.showFAQScreen();
    }

    public void onViewAbout()
    {
        screen.showAboutScreen();
    }

    public void onViewProfile() {
        screen.showProfileScreen();
    }

    public void onViewSettings()
    {
        screen.showSettingsScreen();
    }

    public void onToggleShowArchived()
    {
        showArchived = !showArchived;
        preferences.setShowArchived(showArchived);
        updateAdapterFilter();
    }

    public void onToggleShowCompleted()
    {
        showCompleted = !showCompleted;
        preferences.setShowCompleted(showCompleted);
        updateAdapterFilter();
    }

    public void onSortByColor()
    {
        adapter.setOrder(HabitList.Order.BY_COLOR);
    }

    public void onSortByManually()
    {
        adapter.setOrder(HabitList.Order.BY_POSITION);
    }

    public void onSortByScore()
    {
        adapter.setOrder(HabitList.Order.BY_SCORE);
    }

    public void onSortByName()
    {
        adapter.setOrder(HabitList.Order.BY_NAME);
    }

    public void onToggleNightMode()
    {
        themeSwitcher.toggleNightMode();
        screen.applyTheme();
    }

    public static boolean isShare = false;


    private Habit getHabit(HabitList habitList,String habitName) {
        Habit habit = null;
        for(Habit temp : habitList) {
            if(temp.getName().equalsIgnoreCase(habitName)) {
                habit = temp;
                break;
            }
        }
        return habit;
    }

    public void onExportExcel() {

    }

    public void onExportCSV()
    {
        List<Habit> selected = new LinkedList<>();
        List<String> sadhanas = Config.SADHANAS;
        for (String sadhana : sadhanas) {
            Habit habit = getHabit(habitList,sadhana);
            if(habit != null)
                selected.add(habit);
        }
        File outputDir = dirFinder.getCSVOutputDir();
        String sadhanaFileName = "";
        if(preferences.getFirstName() != null)
            sadhanaFileName = "" + preferences.getFirstName() + "_" + preferences.getLastName() + "_" + preferences.getMahatmaId();
        screen.showMonthPicker(sadhanaFileName, outputDir.getAbsolutePath(), new ListHabitsBehavior.MonthPickerCallback() {
            @Override
            public void onDateSet(int year, int month, String saveFilename) {
                GregorianCalendar startCal = DateUtils.getStartOfTodayCalendar();
                startCal.set(Calendar.MONTH, month-1);
                startCal.set(Calendar.YEAR, year);
                startCal.set(GregorianCalendar.DAY_OF_MONTH,1);
                Timestamp fromDate = new Timestamp(startCal);
                startCal.set(GregorianCalendar.DAY_OF_MONTH, startCal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
                Timestamp toDate = new Timestamp(startCal);
                outputDir.mkdir();
                taskRunner.execute(
                        new ExportCSVTask(habitList, selected, outputDir, filename ->
                        {
                            if (filename != null) {
                                if(isShare)
                                    screen.showSendFileScreen(filename);
                                else
                                    screen.showMessage(ListHabitsBehavior.Message.EXPORT_SUCCESSFUL);
                            }
                            else screen.showMessage(ListHabitsBehavior.Message.COULD_NOT_EXPORT);
                        },fromDate,toDate,saveFilename,preferences,true));
            }
        });






        /*taskRunner.execute(
                new ExportCSVTask(habitList, selected, outputDir, filename ->
                {
                    if (filename != null) screen.showSendFileScreen(filename);
                    else screen.showMessage(Message.COULD_NOT_EXPORT);
                }));*/
    }

    private void updateAdapterFilter()
    {
        adapter.setFilter(new HabitMatcherBuilder()
            .setArchivedAllowed(showArchived)
            .setCompletedAllowed(showCompleted)
            .build());
        adapter.refresh();
    }

    public interface Adapter
    {
        void refresh();

        void setFilter(HabitMatcher build);

        void setOrder(HabitList.Order order);
    }

    public interface Screen
    {
        void applyTheme();

        void showAboutScreen();

        void showProfileScreen();

        void showCreateHabitScreen();

        void showFAQScreen();

        void showSettingsScreen();

        void showMonthPicker(String fileName,
                             @NonNull String outputDirPath,
                             @NonNull ListHabitsBehavior.MonthPickerCallback callback);

        void showMessage(@NonNull ListHabitsBehavior.Message m);

        void showSendFileScreen(@NonNull String filename);
    }
}
