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

package org.purecelibacy.sadhana.activities.habits.edit;


import android.content.*;
import android.content.res.Resources;
import android.os.*;
import android.support.annotation.*;
import android.support.v7.app.*;
import android.text.format.*;
import android.view.*;

import com.android.datetimepicker.time.*;

import org.purecelibacy.sadhana.*;
import org.purecelibacy.sadhana.R;
import org.purecelibacy.sadhana.activities.*;
import org.purecelibacy.sadhana.activities.common.dialogs.*;
import org.purecelibacy.sadhana.activities.habits.edit.views.*;
import org.purecelibacy.sadhana.core.commands.*;
import org.purecelibacy.sadhana.core.models.*;
import org.purecelibacy.sadhana.core.preferences.*;
import org.purecelibacy.sadhana.core.utils.SadhanaUtils;

import butterknife.*;

import static android.view.View.GONE;
import static org.purecelibacy.sadhana.core.ui.ThemeSwitcher.THEME_LIGHT;

public class EditHabitDialog extends AppCompatDialogFragment
{
    public static final String BUNDLE_HABIT_ID = "habitId";

    public static final String BUNDLE_HABIT_TYPE = "habitType";

    protected Habit originalHabit;

    protected Preferences prefs;

    protected CommandRunner commandRunner;

    protected HabitList habitList;

    protected HabitsApplicationComponent component;

    protected ModelFactory modelFactory;

    @BindView(R.id.namePanel)
    NameDescriptionPanel namePanel;

    @BindView(R.id.reminderPanel)
    ReminderPanel reminderPanel;

    @BindView(R.id.frequencyPanel)
    FrequencyPanel frequencyPanel;

    @BindView(R.id.targetPanel)
    TargetPanel targetPanel;

    private ColorPickerDialogFactory colorPickerDialogFactory;

    @Override
    public int getTheme()
    {
        HabitsApplicationComponent component =
            ((HabitsApplication) getContext().getApplicationContext()).getComponent();

        if(component.getPreferences().getTheme() == THEME_LIGHT)
            return R.style.DialogWithTitle;
        else
            return R.style.DarkDialogWithTitle;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        HabitsActivity activity = (HabitsActivity) getActivity();
        colorPickerDialogFactory =
            activity.getComponent().getColorPickerDialogFactory();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view;
        view = inflater.inflate(R.layout.edit_habit, container, false);

        initDependencies();
        ButterKnife.bind(this, view);

        originalHabit = parseHabitFromArguments();
        getDialog().setTitle(getTitle());


        populateForm();
        setupReminderController();
        setupNameController();

        return view;
    }

    protected int getTitle()
    {
        if (originalHabit != null) return R.string.edit_habit;
        else return R.string.create_habit;
    }

    protected void saveHabit(@NonNull Habit habit)
    {
        if (originalHabit == null)
        {
            commandRunner.execute(component
                .getCreateHabitCommandFactory()
                .create(habitList, habit), null);
        }
        else
        {
            habit.setPosition(originalHabit.getPosition());
            commandRunner.execute(component.getEditHabitCommandFactory().
                create(habitList, originalHabit, habit), originalHabit.getId());
        }
    }

    private int getTypeFromArguments()
    {
        Bundle bundle = getArguments();
        return getArguments().getInt(BUNDLE_HABIT_TYPE);
    }

    private void initDependencies()
    {
        Context appContext = getContext().getApplicationContext();
        HabitsApplication app = (HabitsApplication) appContext;

        component = app.getComponent();
        prefs = component.getPreferences();
        habitList = component.getHabitList();
        commandRunner = component.getCommandRunner();
        modelFactory = component.getModelFactory();
    }

    @OnClick(R.id.buttonDiscard)
    void onButtonDiscardClick()
    {
        dismiss();
    }

    @OnClick(R.id.buttonSave)
    void onSaveButtonClick()
    {
        int type = getTypeFromArguments();
        boolean isCreateAction = false;
        if(originalHabit == null)
            isCreateAction = true;
        if (!namePanel.validate()) return;

        boolean isInValidName = false;
        boolean isAlreadyPresent = false;
        for(Habit habit:habitList) {
            if(habit.getName().equalsIgnoreCase(namePanel.getName())) {
                isAlreadyPresent = true;
                break;
            }
        }

        if(isCreateAction) {
            if(isAlreadyPresent)
                isInValidName = true;
        } else {
            if(!originalHabit.getName().equalsIgnoreCase(namePanel.getName())) {
                if(isAlreadyPresent) {
                    isInValidName = true;
                }
            }
        }
        if(isInValidName) {
            Resources res = getResources();
            namePanel.setTvNameError(res.getString(R.string.validation_name_should_not_be_same));
            return;
        }

        if (type == Habit.YES_NO_HABIT && !frequencyPanel.validate()) return;
        if (type == Habit.NUMBER_HABIT && !targetPanel.validate()) return;

        Habit habit = modelFactory.buildHabit();
        habit.setName(namePanel.getName());
        habit.setDescription(namePanel.getDescription());
        habit.setColor(namePanel.getColor());
        habit.setReminder(reminderPanel.getReminder());
        habit.setFrequency(frequencyPanel.getFrequency());
        habit.setUnit(targetPanel.getUnit());
        habit.setTargetValue(targetPanel.getTargetValue());
        habit.setType(type);

        saveHabit(habit);
        dismiss();
    }

    @Nullable
    private Habit parseHabitFromArguments()
    {
        Bundle arguments = getArguments();
        if (arguments == null) return null;

        Long id = (Long) arguments.get(BUNDLE_HABIT_ID);
        if (id == null) return null;

        Habit habit = habitList.getById(id);
        if (habit == null) throw new IllegalStateException();

        return habit;
    }

    private void populateForm()
    {
        Habit habit = modelFactory.buildHabit();
        habit.setFrequency(Frequency.DAILY);
        habit.setColor(prefs.getDefaultHabitColor(habit.getColor()));
        habit.setType(getTypeFromArguments());
        habit.setTargetValue(1);
        if (originalHabit != null) habit.copyFrom(originalHabit);

        if(originalHabit != null && SadhanaUtils.isContainSadhanaIgnoreCase(originalHabit.getName())) {
            namePanel.disableNameView();
        }

        //if (habit.isNumerical()) frequencyPanel.setVisibility(GONE);
        //else targetPanel.setVisibility(GONE);
        if (!habit.isNumerical())
            targetPanel.setVisibility(GONE);

        namePanel.populateFrom(habit);
        frequencyPanel.setFrequency(habit.getFrequency());
        targetPanel.setTargetValue(habit.getTargetValue());
        targetPanel.setUnit(habit.getUnit());
        if (habit.hasReminder()) reminderPanel.setReminder(habit.getReminder());
    }

    private void setupNameController()
    {
        namePanel.setController(new NameDescriptionPanel.Controller()
        {
            @Override
            public void onColorPickerClicked(int previousColor)
            {
                ColorPickerDialog picker =
                    colorPickerDialogFactory.create(previousColor);

                picker.setListener(c ->
                {
                    prefs.setDefaultHabitColor(c);
                    namePanel.setColor(c);
                });

                picker.show(getFragmentManager(), "picker");
            }
        });
    }

    private void setupReminderController()
    {
        reminderPanel.setController(new ReminderPanel.Controller()
        {
            @Override
            public void onTimeClicked(int currentHour, int currentMin)
            {
                TimePickerDialog timePicker;
                boolean is24HourMode = DateFormat.is24HourFormat(getContext());
                timePicker =
                    TimePickerDialog.newInstance(reminderPanel, currentHour,
                        currentMin, is24HourMode);
                timePicker.show(getFragmentManager(), "timePicker");
            }

            @Override
            public void onWeekdayClicked(WeekdayList currentDays)
            {
                WeekdayPickerDialog dialog = new WeekdayPickerDialog();
                dialog.setListener(reminderPanel);
                dialog.setSelectedDays(currentDays);
                dialog.show(getFragmentManager(), "weekdayPicker");
            }
        });
    }
}
