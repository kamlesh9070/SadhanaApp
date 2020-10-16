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

package org.youth.sadhana.activities.habits.list

import android.content.*
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build.VERSION.*
import android.os.Build.VERSION_CODES.*
import android.support.v7.widget.Toolbar
import android.view.ViewGroup.LayoutParams.*
import android.widget.*
import org.youth.androidbase.activities.*
import org.youth.sadhana.*
import org.youth.sadhana.activities.common.views.*
import org.youth.sadhana.activities.habits.list.views.*
import org.youth.sadhana.core.models.*
import org.youth.sadhana.core.preferences.*
import org.youth.sadhana.core.tasks.*
import org.youth.sadhana.core.ui.screens.habits.list.HintListFactory
import org.youth.sadhana.core.utils.*
import org.youth.sadhana.utils.*
import java.lang.Math.*
import java.util.*
import javax.inject.*

const val MAX_CHECKMARK_COUNT = 60

@ActivityScope
class ListHabitsRootView @Inject constructor(
        @ActivityContext context: Context,
        hintListFactory: HintListFactory,
        preferences: Preferences,
        midnightTimer: MidnightTimer,
        runner: TaskRunner,
        private val listAdapter: HabitCardListAdapter,
        habitCardListViewFactory: HabitCardListViewFactory
) : BaseRootView(context), ModelObservable.Listener {

    val listView: HabitCardListView = habitCardListViewFactory.create()
    val llEmpty = EmptyListView(context)
    val tbar = buildToolbar()
    val progressBar = TaskProgressBar(context, runner)
    val hintView: HintView
    val header = HeaderView(context, preferences, midnightTimer)
    val fuzionView: TextView
    init {
        val hints = resources.getStringArray(R.array.hints)
        val hintList = hintListFactory.create(hints)
        hintView = HintView(context, hintList)
        fuzionView = TextView(context).apply {
            setTextColor(Color.BLACK)
            setTypeface(null, Typeface.BOLD)
            text = "Charge Yourself Dates: 19th Oct to 25th Oct"
        }
        addView(RelativeLayout(context).apply {
            background = sres.getDrawable(R.attr.windowBackgroundColor)
            addAtTop(tbar)

            addBelow(header, tbar)
            addBelow(listView, header, height = MATCH_PARENT)
            addBelow(llEmpty, header, height = MATCH_PARENT)
            addBelow(progressBar, header) {
                it.topMargin = dp(-6.0f).toInt()
            }
            addAtBottom(fuzionView)
            addAtBottom(hintView)
            if (SDK_INT < LOLLIPOP) {
                addBelow(ShadowView(context), tbar)
                addBelow(ShadowView(context), header)
            }
        }, MATCH_PARENT, MATCH_PARENT)

        listAdapter.setListView(listView)
        initToolbar()
    }

    override fun getToolbar(): Toolbar {
        return tbar
    }

    override fun onModelChange() {
        updateEmptyView()
    }

    private fun setupControllers() {
        header.setScrollController(object : ScrollableChart.ScrollController {
            override fun onDataOffsetChanged(newDataOffset: Int) {
                listView.mydataOffset = newDataOffset
            }
        })
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setupControllers()
        listAdapter.observable.addListener(this)
    }

    override fun onDetachedFromWindow() {
        listAdapter.observable.removeListener(this)
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val count = getCheckmarkCount()
        header.buttonCount = count
        header.setMaxDataOffset(max(MAX_CHECKMARK_COUNT - count, 0))
        listView.checkmarkCount = count
        super.onSizeChanged(w, h, oldw, oldh)
    }

    private fun getCheckmarkCount(): Int {
        val nameWidth = dim(R.dimen.habitNameWidth)
        val buttonWidth = dim(R.dimen.checkmarkWidth)
        val labelWidth = max((measuredWidth / 4).toFloat(), nameWidth)
        val buttonCount = ((measuredWidth - labelWidth) / buttonWidth).toInt()
        return min(MAX_CHECKMARK_COUNT, max(0, buttonCount))
    }

    private fun updateEmptyView() {
        llEmpty.visibility = when (listAdapter.itemCount) {
            0 -> VISIBLE
            else -> GONE
        }
    }
}
