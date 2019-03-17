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

package org.purecelibacy.sadhana.activities.habits.list.views

import android.content.*
import android.graphics.*
import android.support.graphics.drawable.VectorDrawableCompat
import android.text.*
import android.view.*
import android.view.View.MeasureSpec.*
import com.google.auto.factory.*
import org.purecelibacy.androidbase.activities.*
import org.purecelibacy.sadhana.*
import org.purecelibacy.sadhana.core.models.Checkmark.*
import org.purecelibacy.sadhana.core.preferences.*
import org.purecelibacy.sadhana.utils.*
import org.apache.poi.hssf.util.HSSFColor.GREEN
import org.purecelibacy.sadhana.activities.common.dialogs.RemarkPickerFactory
import org.purecelibacy.sadhana.core.commands.CreateRepetitionCommand


@AutoFactory
class CheckmarkButtonView(
        @Provided @ActivityContext context: Context,
        @Provided val preferences: Preferences
) : View(context),
    View.OnClickListener,
    View.OnLongClickListener {

    var color: Int = Color.BLACK
        set(value) {
            field = value
            invalidate()
        }

    var value: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    @javax.annotation.Nullable
    var remark : String? = ""
        set(remark: String?) {
            field = remark
            invalidate()
        }

    var onToggle: (withRemark: Boolean) -> Unit = {withRemark ->  }
    var onEdit: () -> Unit = {}
    private var drawer = Drawer()

    init {
        isFocusable = false
        setOnClickListener(this)
        setOnLongClickListener(this)
    }

    fun performToggle(withRemark: Boolean) {
        onToggle(withRemark)
        value = when (value) {
            CHECKED_EXPLICITLY -> UNCHECKED
            else -> CHECKED_EXPLICITLY
        }
        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        invalidate()
    }

    override fun onClick(v: View) {
        if (preferences.isShortToggleEnabled) performToggle(false)
        else showMessage(R.string.long_press_to_toggle)
    }

    override fun onLongClick(v: View): Boolean {
        if(preferences.isShortToggleEnabled) {
            when (value) {
                UNCHECKED -> {
                    performToggle(true)
                }
                else -> {
                    onEdit()
                }
            }
        } else {
            performToggle(false)
        }
        /*var withRemark = false
        if (preferences.isShortToggleEnabled)
            withRemark = true
        performToggle(withRemark)*/
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawer.draw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = resources.getDimensionPixelSize(R.dimen.checkmarkHeight)
        val width = resources.getDimensionPixelSize(R.dimen.checkmarkWidth)
        super.onMeasure(width.toMeasureSpec(EXACTLY),
                        height.toMeasureSpec(EXACTLY))
    }

    private inner class Drawer {
        private val rect = RectF()
        private val lowContrastColor = sres.getColor(R.attr.lowContrastTextColor)

        private var paint = TextPaint().apply {
            typeface = getFontAwesome()
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            textSize = dim(R.dimen.smallTextSize)
        }

        fun draw(canvas: Canvas) {
            paint.color = when (value) {
                CHECKED_EXPLICITLY -> color
                else -> lowContrastColor
            }
            val id = when (value) {
                UNCHECKED -> R.string.fa_times
                else -> R.string.fa_check
            }
            val label = resources.getString(id)
            val em = paint.measureText("m")

            rect.set(0f, 0f, width.toFloat(), height.toFloat())
            rect.offset(0f, 0.4f * em)
            canvas.drawText(label, rect.centerX(), rect.centerY(), paint)
            if(value > 0) {
                var circlePaint = Paint()
                circlePaint.setStrokeWidth(2f)
                circlePaint.style = Paint.Style.STROKE
                circlePaint.color = color;
                rect.offset(0f, -0.4f * em)
                var dim = (1.2 * em).toFloat();
                canvas.drawCircle(rect.centerX(),rect.centerY(),dim ,circlePaint);
            }
            if(remark != null && !remark.isNullOrEmpty()) {
                var circlePaint = Paint()
                circlePaint.setStrokeWidth(3f)
                circlePaint.style = Paint.Style.FILL_AND_STROKE
                circlePaint.color = color;
                rect.offset(0f, +0.8f * em)
                var dim = (0.2 * em).toFloat();
                canvas.drawCircle(rect.centerX(),rect.centerY(),dim ,circlePaint);
            }
        }
    }

}
