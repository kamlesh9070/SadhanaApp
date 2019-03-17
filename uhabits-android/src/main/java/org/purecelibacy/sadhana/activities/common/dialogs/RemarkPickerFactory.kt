/*
 * Copyright (C) 2017 Álinson Santos Xavier
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
 * with this program. If not, see .
 */

package org.purecelibacy.sadhana.activities.common.dialogs

import android.content.*
import android.support.v7.app.*
import android.text.*
import android.view.*
import android.view.inputmethod.*
import android.widget.*
import org.purecelibacy.androidbase.activities.*
import org.purecelibacy.androidbase.utils.*
import org.purecelibacy.sadhana.*
import org.purecelibacy.sadhana.activities.habits.edit.views.ExampleEditText
import org.purecelibacy.sadhana.core.models.Habit
import org.purecelibacy.sadhana.core.ui.screens.habits.list.*
import javax.inject.*

class RemarkPickerFactory
@Inject constructor(
        @ActivityContext private val context: Context
) {
    fun create(remark: String?,
               habit: Habit?,
               callback: ListHabitsBehavior.RemarkPickerCallback): AlertDialog {

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.remark_picker_dialog, null)

        val tvRemark = view.findViewById(R.id.remark) as EditText;


        var title = "Remark"

        if(remark != null)
            tvRemark.setText(remark);

        val dialog = AlertDialog.Builder(context)
                .setView(view)
                .setTitle(title)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    callback.onRemarkPicked(tvRemark.text.toString().trim())
                }.create()

        return dialog
    }

}
