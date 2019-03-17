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
import org.purecelibacy.androidbase.activities.*
import org.purecelibacy.sadhana.*
import org.purecelibacy.sadhana.core.preferences.*
import org.purecelibacy.sadhana.core.ui.*
import org.purecelibacy.sadhana.core.ui.screens.habits.list.*
import org.purecelibacy.sadhana.utils.PermissionGrantListener
import org.purecelibacy.sadhana.utils.PermissionProcessor
import javax.inject.*

@ActivityScope
class ListHabitsMenu @Inject constructor(
        activity: BaseActivity,
        private val preferences: Preferences,
        private val themeSwitcher: ThemeSwitcher,
        private val behavior: ListHabitsMenuBehavior
) : BaseMenu(activity) {

    override fun onCreate(menu: Menu) {
        val nightModeItem = menu.findItem(R.id.actionToggleNightMode)
        val exportAsCSV = menu.findItem(R.id.actionExportSadhana)
        val hideArchivedItem = menu.findItem(R.id.actionHideArchived)
        val hideCompletedItem = menu.findItem(R.id.actionHideCompleted)
        nightModeItem.isChecked = themeSwitcher.isNightMode
        hideArchivedItem.isChecked = !preferences.showArchived
        hideCompletedItem.isChecked = !preferences.showCompleted
        val viewgroup = activity.findViewById(android.R.id.content).parent as ViewGroup
        println(viewgroup.childCount)
        val view = viewgroup.getChildAt(0)
        for (i in 0..viewgroup.childCount - 1) {
            viewgroup.getChildAt(i)
        }
        checkPermissionForExternalDummy(view);
    }


    /**
     * Checks permission to access external storage.
     */
    fun checkPermissionForExternalDummy(v: View) {
        val permissionProcessor = PermissionProcessor(activity, v)
        permissionProcessor.setPermissionGrantListener(object : PermissionGrantListener {
            override fun OnGranted() {
            }
        })
        permissionProcessor.askForPermissionExternalStorage()
    }
    /**
     * Checks permission to access external storage.
     */
    fun checkPermissionForExternal(v: View) {
        val permissionProcessor = PermissionProcessor(activity, v)
        permissionProcessor.setPermissionGrantListener(object : PermissionGrantListener {
            override fun OnGranted() {
                behavior.onExportCSV()
            }
        })
        permissionProcessor.askForPermissionExternalStorage()
    }

    fun exportAsCSV() {
        val viewgroup = activity.findViewById(android.R.id.content).parent as ViewGroup
        println(viewgroup.childCount)
        val view = viewgroup.getChildAt(0)
        for (i in 0..viewgroup.childCount - 1) {
            viewgroup.getChildAt(i)
        }
        checkPermissionForExternal(view);
    }

    override fun onItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionToggleNightMode -> {
                behavior.onToggleNightMode()
                return true
            }

            R.id.actionExportSadhana -> {
                ListHabitsMenuBehavior.isShare = false;
                exportAsCSV()
                //behavior.onExportCSV()
                return true
            }

            R.id.actionShareSadhana -> {
                ListHabitsMenuBehavior.isShare = true;
                exportAsCSV()
                //behavior.onExportCSV()
                return true
            }


            R.id.actionAdd -> {
                behavior.onCreateHabit()
                return true
            }

            R.id.actionFAQ -> {
                behavior.onViewFAQ()
                return true
            }

            R.id.actionAbout -> {
                behavior.onViewAbout()
                return true
            }

            R.id.actionAbout -> {

                return true
            }

            R.id.actionProfile -> {
                behavior.onViewProfile()
                return true
            }

            R.id.actionSettings -> {
                behavior.onViewSettings()
                return true
            }

            R.id.actionHideArchived -> {
                behavior.onToggleShowArchived()
                invalidate()
                return true
            }

            R.id.actionHideCompleted -> {
                behavior.onToggleShowCompleted()
                invalidate()
                return true
            }

            R.id.actionSortColor -> {
                behavior.onSortByColor()
                return true
            }

            R.id.actionSortManual -> {
                behavior.onSortByManually()
                return true
            }

            R.id.actionSortName -> {
                behavior.onSortByName()
                return true
            }

            R.id.actionSortScore -> {
                behavior.onSortByScore()
                return true
            }

            else -> return false
        }
    }

    override fun getMenuResourceId() = R.menu.list_habits
}
