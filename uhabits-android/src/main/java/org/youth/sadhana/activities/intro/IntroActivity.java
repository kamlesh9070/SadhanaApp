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

package org.youth.sadhana.activities.intro;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.os.*;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;


import com.github.paolorotolo.appintro.*;

import org.youth.sadhana.HabitsApplication;
import org.youth.sadhana.HabitsApplicationComponent;
import org.youth.sadhana.R;
import org.youth.sadhana.core.commands.CommandRunner;
import org.youth.sadhana.core.models.Frequency;
import org.youth.sadhana.core.models.Habit;
import org.youth.sadhana.core.models.HabitList;
import org.youth.sadhana.core.models.ModelFactory;
import org.youth.sadhana.core.preferences.Preferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * Activity that introduces the app to the user, shown only after the app is
 * launched for the first time.
 */
public class IntroActivity extends AppIntro2
{
    protected CommandRunner commandRunner;

    protected HabitList habitList;

    protected HabitsApplicationComponent component;

    protected ModelFactory modelFactory;

    protected Preferences preferences;


    private void initDependencies()
    {
        Context appContext = getApplicationContext();
        HabitsApplication app = (HabitsApplication) appContext;
        component = app.getComponent();
        habitList = component.getHabitList();
        commandRunner = component.getCommandRunner();
        modelFactory = component.getModelFactory();
        preferences = component.getPreferences();
    }


    @Override
    public void init(Bundle savedInstanceState)
    {
        showStatusBar(false);

        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_title_1),
            getString(R.string.intro_description_1), R.drawable.intro_icon_1_old,
            Color.parseColor("#D4A017")));
        System.out.println(getApplicationContext());
        if(getApplicationContext() instanceof HabitsApplication) {
            HabitsApplication habitsApplication = (HabitsApplication)getApplicationContext();
            System.out.println(habitsApplication.getComponent());
            System.out.println(habitsApplication.getComponent().getCommandRunner());
        }
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_title_2),
            getString(R.string.intro_description_2), R.drawable.intro_icon_2,
            Color.parseColor("#54C571")));

        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_title_4),
            getString(R.string.intro_description_4), R.drawable.intro_icon_4,
            Color.parseColor("#357EC7")));

        initDependencies();
        performLoadInitData();
        //createDialog().show();
    }

    @Override
    public void onBackPressed() {
        // Simply Do noting!
    }

    AlertDialog createDialog(){
        Context context = getBaseContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
        View dialog = LayoutInflater.from(context).inflate(R.layout.month_picker_dialog, null);
        Calendar cal = Calendar.getInstance();
        NumberPicker monthPicker = (NumberPicker) dialog.findViewById(R.id.picker_month);
        NumberPicker yearPicker = (NumberPicker) dialog.findViewById(R.id.picker_year);
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        //monthPicker.value = cal.get(Calendar.MONTH) + 1;

/*
        in year = cal.get(Calendar.YEAR);
        yearPicker.minValue = year - 3
        yearPicker.maxValue = year
        yearPicker.value = year
*/



        return builder.create();
    }



    void performLoadInitData() {
        Object[][] habits = new Object[4][];
        // Also change in Config.java
        Object[] aarti = new Object[]{"Aarti","પરિવાર સાથે આરતી કરીએ.\nDo Aarti with your Family.\nअपने  परिवार  के  साथ  आरती करे.",0};
        Object[] satsang = new Object[]{"Satsang/Book Reading","સત્સંગ વિડિયો 10 મિનિટ માટે/દાદાની પુસ્તકનું વાંચન.\nSatsang Videos for 10mins/Dada's Book Reading.\nसत्संग  वीडियो  १० मिनट  तक / दादाजी की पुस्तक पठना.",5};
        //Object[] vanchan = new Object[]{"Vanchan","Have you completed min. 2 pages Vanchan?",19};
        Object[] vinay = new Object[]{"Ashirwad of Mom Dad","માતા - પિતા ને પગે લાગવું.\nTouch Feets and take Ashirwad from Mother-Father.\nमाता  पिता  के  पैर  छुए.",11};
        Object[] noMobile = new Object[]{"Pratikraman","માતા - પિતા/ મિત્રો ના પ્રતિક્રમણ.\nDo - Pratikraman related to Parents/Friends.\nमाता-पिता / दोस्तों  के  प्रतिक्रमण.",8};
        habits[0] = vinay;
        habits[1] = aarti;
        habits[2] = satsang;
        //habits[2] = vanchan;
        habits[3] = noMobile;
        int i = 0;

        List<String> createdHabitName = new ArrayList<String>();
        Iterator<Habit> habitIterator = habitList.iterator();

        while(habitIterator.hasNext()) {
            Habit habit = habitIterator.next();
            createdHabitName.add(habit.getName());
        }


        for (Object[] habitData : habits) {
            if(!createdHabitName.contains(String.valueOf(habitData[0]))) {
                Habit habit = modelFactory.buildHabit();
                habit.setName(String.valueOf(habitData[0]));
                habit.setDescription(String.valueOf(habitData[1]));
                habit.setColor(Integer.valueOf((int)habitData[2]));
                //habit.setReminder(reminderPanel.getReminder());
                habit.setFrequency(Frequency.DAILY);
                //habit.setUnit("7");
                //habit.setTargetValue(7);
                if("Seva".equalsIgnoreCase(habit.getName()) || "Vanchan".equalsIgnoreCase(habit.getName())) {
                    habit.setType(Habit.NUMBER_HABIT);
                    if("Seva".equalsIgnoreCase(habit.getName()))
                        habit.setTargetValue(1);
                    else if ("Vanchan".equalsIgnoreCase(habit.getName()))
                        habit.setTargetValue(2);
                    else
                        habit.setTargetValue(1);
                }

                else
                    habit.setType(Habit.YES_NO_HABIT);
                habit.setPosition(i);
                i++;
                saveHabit(habit);
            }
        }

        Activity activity = getParent();

/*
        ViewGroup viewGroup = (ViewGroup) activity.findViewById(android.R.id.content).getParent();
        View view = viewGroup.getChildAt(0);
        checkPermissionForExternalDummy(view);
*/


    }

  /*  *//**
     * Checks permission to access external storage.
     *//*
    void checkPermissionForExternalDummy(View view) {
        PermissionProcessor permissionProcessor = new PermissionProcessor(getParent(), view);
        permissionProcessor.setPermissionGrantListener(new PermissionGrantListener() {
            public void OnGranted() {
            }
        });
        permissionProcessor.askForPermissionExternalStorage();
    }*/

    protected void saveHabit(@NonNull Habit habit)
    {

        commandRunner.execute(component
                    .getCreateHabitCommandFactory()
                    .create(habitList, habit), null);
    }

    @Override
    public void onNextPressed()
    {
    }

    @Override
    public void onDonePressed()
    {
        Intent intent = new Intent(this, ProfileActivity.class);
        LoginFragment.setFirstTime(true);
        LoginFragment.setPreferences(preferences);
        startActivity(intent);
        finish();

    }

    @Override
    public void onSlideChanged()
    {
    }
}
