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

package org.youth.sadhana.activities.habits.show;

import android.support.annotation.*;

import org.youth.androidbase.activities.*;
import org.youth.sadhana.*;
import org.youth.sadhana.activities.common.dialogs.*;
import org.youth.sadhana.activities.habits.edit.*;
import org.youth.sadhana.core.models.*;
import org.youth.sadhana.core.ui.screens.habits.show.*;

import javax.inject.*;

import dagger.*;

@ActivityScope
public class ShowHabitScreen extends BaseScreen
    implements ShowHabitMenuBehavior.Screen,
               ShowHabitBehavior.Screen,
               HistoryEditorDialog.Controller,
               ShowHabitRootView.Controller
{
    @NonNull
    private final Habit habit;

    @NonNull
    private final EditHabitDialogFactory editHabitDialogFactory;

    private final Lazy<ShowHabitBehavior> behavior;

    @Inject
    public ShowHabitScreen(@NonNull BaseActivity activity,
                           @NonNull Habit habit,
                           @NonNull ShowHabitRootView view,
                           @NonNull ShowHabitsMenu menu,
                           @NonNull
                               EditHabitDialogFactory editHabitDialogFactory,
                           @NonNull Lazy<ShowHabitBehavior> behavior)
    {
        super(activity);
        setMenu(menu);
        setRootView(view);

        this.habit = habit;
        this.behavior = behavior;
        this.editHabitDialogFactory = editHabitDialogFactory;
        view.setController(this);
    }

    @Override
    public void onEditHistoryButtonClick()
    {
        behavior.get().onEditHistory();
    }

    @Override
    public void onToggleCheckmark(Timestamp timestamp)
    {
        behavior.get().onToggleCheckmark(timestamp);
    }

    @Override
    public void onToolbarChanged()
    {
        invalidateToolbar();
    }

    @Override
    public void reattachDialogs()
    {
        super.reattachDialogs();
        HistoryEditorDialog historyEditor = (HistoryEditorDialog) activity
            .getSupportFragmentManager()
            .findFragmentByTag("historyEditor");
        if (historyEditor != null) historyEditor.setController(this);
    }

    @Override
    public void showEditHabitScreen(@NonNull Habit habit)
    {
        activity.showDialog(editHabitDialogFactory.edit(habit), "editHabit");
    }

    @Override
    public void showEditHistoryScreen()
    {
        HistoryEditorDialog dialog = new HistoryEditorDialog();
        dialog.setHabit(habit);
        dialog.setController(this);
        dialog.show(activity.getSupportFragmentManager(), "historyEditor");
    }

    @Override
    public void showMessage(ShowHabitMenuBehavior.Message m)
    {
        switch (m)
        {
            case COULD_NOT_EXPORT:
                showMessage(R.string.could_not_export);
        }
    }
}
