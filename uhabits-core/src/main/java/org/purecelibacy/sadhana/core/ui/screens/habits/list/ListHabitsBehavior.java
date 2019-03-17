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
import org.purecelibacy.sadhana.core.commands.*;
import org.purecelibacy.sadhana.core.models.*;
import org.purecelibacy.sadhana.core.preferences.*;
import org.purecelibacy.sadhana.core.tasks.*;
import org.purecelibacy.sadhana.core.utils.*;
import java.io.*;
import java.util.*;

import javax.inject.*;

public class ListHabitsBehavior
{
    @NonNull
    private final HabitList habitList;

    @NonNull
    private final DirFinder dirFinder;

    @NonNull
    private final TaskRunner taskRunner;

    @NonNull
    private final Screen screen;

    @NonNull
    private final CommandRunner commandRunner;

    @NonNull
    private final Preferences prefs;

    @NonNull
    private final BugReporter bugReporter;

    @Inject
    public ListHabitsBehavior(@NonNull HabitList habitList,
                              @NonNull DirFinder dirFinder,
                              @NonNull TaskRunner taskRunner,
                              @NonNull Screen screen,
                              @NonNull CommandRunner commandRunner,
                              @NonNull Preferences prefs,
                              @NonNull BugReporter bugReporter)
    {
        this.habitList = habitList;
        this.dirFinder = dirFinder;
        this.taskRunner = taskRunner;
        this.screen = screen;
        this.commandRunner = commandRunner;
        this.prefs = prefs;
        this.bugReporter = bugReporter;
    }

    public void onClickHabit(@NonNull Habit h)
    {
        screen.showHabitScreen(h);
    }

    public void onEdit(@NonNull Habit habit, Timestamp timestamp, boolean isCheckmark)
    {
        CheckmarkList checkmarks = habit.getCheckmarks();
        double oldValue = checkmarks.getValues(timestamp, timestamp)[0];

        Repetition repetition = habit.getRepetitions().getByTimestamp(timestamp);
        String remark = null;
        if(repetition != null)
            remark = repetition.getRemark();
        if(isCheckmark) {
            final String tempRemark = remark;
            screen.showRemarkPicker(tempRemark,habit, (enteredremark) -> {
                    commandRunner.execute(
                            new CreateRepetitionCommand(habit, timestamp, (int) Checkmark.CHECKED_EXPLICITLY, enteredremark),
                            habit.getId());
            });
        } else {
            screen.showNumberPicker(oldValue / 1000, habit.getUnit(), remark,habit, (newValue,enteredremark) ->
            {
                newValue = Math.round(newValue * 1000);
                commandRunner.execute(
                        new CreateRepetitionCommand(habit, timestamp, (int) newValue, enteredremark),
                        habit.getId());
            });
        }
    }

    public void onToggle(@NonNull Habit habit, Timestamp timestamp, boolean withRemark)
    {
        if(withRemark) {
            Repetition repetition = habit.getRepetitions().getByTimestamp(timestamp);
            String remark = null;
            if(repetition != null) {
                remark = repetition.getRemark();
            }
            final String tempRemark = remark;
            screen.showRemarkPicker(tempRemark,habit, (enteredremark) -> {
                if(tempRemark != null) {
                    commandRunner.execute(
                            new CreateRepetitionCommand(habit, timestamp, (int) Checkmark.CHECKED_EXPLICITLY, enteredremark),
                            habit.getId());
                } else {
                    executeToggleCommand(habit,timestamp,enteredremark);
                }
            });
        } else {
            String remark = null;
            if(habit.getRepetitions()!= null && habit.getRepetitions().getByTimestamp(timestamp) != null)
                remark = habit.getRepetitions().getByTimestamp(timestamp).getRemark();
            executeToggleCommand(habit,timestamp,remark);
        }

    }

    private void executeToggleCommand(Habit habit, Timestamp timestamp, String enteredremark) {
        commandRunner.execute(
                new ToggleRepetitionCommand(habitList, habit, timestamp, enteredremark),
                habit.getId());
    }

    public void onExportExcel() {

    }

    public void onExportCSV()
    {
        List<Habit> selected = new LinkedList<>();
        for (Habit h : habitList) selected.add(h);
        File outputDir = dirFinder.getCSVOutputDir();
        screen.showMonthPicker(""+12,outputDir.getAbsolutePath() , new MonthPickerCallback() {
            @Override
            public void onDateSet(int year, int month, String dayOfMonth) {
                GregorianCalendar startCal = DateUtils.getStartOfTodayCalendar();
                startCal.set(Calendar.MONTH, month-1);
                startCal.set(Calendar.YEAR,year);
                startCal.set(GregorianCalendar.DAY_OF_MONTH,1);
                Timestamp fromDate = new Timestamp(startCal);
                startCal.set(GregorianCalendar.DAY_OF_MONTH, startCal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
                Timestamp toDate = new Timestamp(startCal);
                taskRunner.execute(
                        new ExportCSVTask(habitList, selected, outputDir, filename ->
                        {
                            if (filename != null) screen.showSendFileScreen(filename);
                            else screen.showMessage(Message.COULD_NOT_EXPORT);
                        },fromDate,toDate));
            }
        });





        /*taskRunner.execute(
                new ExportCSVTask(habitList, selected, outputDir, filename ->
                {
                    if (filename != null) screen.showSendFileScreen(filename);
                    else screen.showMessage(Message.COULD_NOT_EXPORT);
                }));*/
    }

    /*private DatePickerDialog createDialogWithoutDateField() {
        DatePickerDialog dpd = new DatePickerDialog(this, null, 2014, 1, 24);
        try {
            java.lang.reflect.Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);
                    java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                    for (java.lang.reflect.Field datePickerField : datePickerFields) {
                        Log.i("test", datePickerField.getName());
                        if ("mDaySpinner".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
        }
        return dpd;
    }
*/
    public void onFirstRun()
    {
        //prefs.setFirstRun(false);
        prefs.updateLastHint(-1, DateUtils.getToday());
        screen.showIntroScreen();
        screen.showMessage(Message.DATABASE_REPAIRED);
    }

    public void onReorderHabit(@NonNull Habit from, @NonNull Habit to)
    {
        taskRunner.execute(() -> habitList.reorder(from, to));
    }

    public void onRepairDB()
    {
        taskRunner.execute(() ->
        {
            habitList.repair();
            screen.showMessage(Message.DATABASE_REPAIRED);
        });
    }

    public void onSendBugReport()
    {
        bugReporter.dumpBugReportToFile();

        try
        {
            String log = bugReporter.getBugReport();
            screen.showSendBugReportToDeveloperScreen(log);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            screen.showMessage(Message.COULD_NOT_GENERATE_BUG_REPORT);
        }
    }

    public void onStartup()
    {
        prefs.incrementLaunchCount();
        if (prefs.isFirstRun()) onFirstRun();
    }


    public enum Message
    {
        COULD_NOT_EXPORT, IMPORT_SUCCESSFUL, IMPORT_FAILED, DATABASE_REPAIRED,
        COULD_NOT_GENERATE_BUG_REPORT, FILE_NOT_RECOGNIZED , EXPORT_SUCCESSFUL
    }

    public interface BugReporter
    {
        void dumpBugReportToFile();

        String getBugReport() throws IOException;
    }

    public interface DirFinder
    {
        File getCSVOutputDir();
    }

    public interface NumberPickerCallback
    {
        void onNumberPicked(double newValue, String remark);
    }

    public interface RemarkPickerCallback {
        void onRemarkPicked(String remark);
    }

    public interface MonthPickerCallback
    {
        void onDateSet(int year, int month, String fileName);
    }

    public interface Screen
    {
        void showHabitScreen(@NonNull Habit h);

        void showIntroScreen();

        void showMessage(@NonNull Message m);

        void showNumberPicker(double value,
                              @NonNull String unit,
                              String remark,
                              Habit selectHabit,
                              @NonNull NumberPickerCallback callback);
        void showRemarkPicker(String remark,
                              Habit selectHabit,
                              @NonNull RemarkPickerCallback callback);

        void showMonthPicker(String sadhanaFileName,
                              @NonNull String unit,
                              @NonNull ListHabitsBehavior.MonthPickerCallback callback);

        void showSendBugReportToDeveloperScreen(String log);

        void showSendFileScreen(@NonNull String filename);
    }
}
