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

package org.purecelibacy.sadhana.activities.habits.list

import android.view.*
import dagger.*
import org.purecelibacy.androidbase.activities.*
import org.purecelibacy.sadhana.*
import org.purecelibacy.sadhana.activities.habits.list.views.*
import org.purecelibacy.sadhana.core.commands.*
import org.purecelibacy.sadhana.core.ui.screens.habits.list.*
import javax.inject.*

@ActivityScope
class ListHabitsSelectionMenu @Inject constructor(
        private val screen: ListHabitsScreen,
        private val listAdapter: HabitCardListAdapter,
        var commandRunner: CommandRunner,
        private val behavior: ListHabitsSelectionMenuBehavior,
        private val listController: Lazy<HabitCardListController>
) : BaseSelectionMenu() {

    override fun onFinish() {
        listController.get().onSelectionFinished()
        super.onFinish()
    }

    override fun onItemClicked(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit_habit -> {
                behavior.onEditHabits()
                return true
            }

            R.id.action_archive_habit -> {
                behavior.onArchiveHabits()
                return true
            }

            R.id.action_unarchive_habit -> {
                behavior.onUnarchiveHabits()
                return true
            }

            R.id.action_delete -> {
                behavior.onDeleteHabits()
                return true
            }

            R.id.action_color -> {
                behavior.onChangeColor()
                return true
            }

            else -> return false
        }
    }

    override fun onPrepare(menu: Menu): Boolean {
        val itemEdit = menu.findItem(R.id.action_edit_habit)
        val itemColor = menu.findItem(R.id.action_color)
        val itemArchive = menu.findItem(R.id.action_archive_habit)
        val itemUnarchive = menu.findItem(R.id.action_unarchive_habit)
        val itemDelete = menu.findItem(R.id.action_delete)

        itemColor.isVisible = true
        //itemEdit.isVisible = behavior.canEdit()
        itemEdit.isVisible = true
        //itemArchive.isVisible = behavior.canArchive()
        itemArchive.isVisible = false
        //itemUnarchive.isVisible = behavior.canUnarchive()
        itemUnarchive.isVisible = false
        setTitle(Integer.toString(listAdapter.selected.size))

        itemDelete.isVisible = true;
        return true
    }

    fun onSelectionStart() = screen.startSelection()
    fun onSelectionChange() = invalidate()
    fun onSelectionFinish() = finish()
    override fun getResourceId() = R.menu.list_habits_selection
}
