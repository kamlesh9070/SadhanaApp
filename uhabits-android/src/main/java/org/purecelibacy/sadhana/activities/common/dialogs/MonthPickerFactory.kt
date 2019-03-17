package org.purecelibacy.sadhana.activities.common.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.support.annotation.ColorInt
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import org.purecelibacy.androidbase.activities.ActivityContext
import org.purecelibacy.sadhana.core.ui.screens.habits.list.ListHabitsBehavior
import javax.inject.Inject
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import java.util.*
import org.purecelibacy.sadhana.*

/**
 * Created by Kamlesh on 03-09-2017.
 */
class MonthPickerFactory
@Inject constructor(
        @ActivityContext private val context: Context
) {

    private val MAX_YEAR = 2099

    private val PICKER_DISPLAY_MONTHS_NAMES = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    fun create(sadhanaFileName: String,
               unit: String,
               callback: ListHabitsBehavior.MonthPickerCallback): Dialog {
        val builder = AlertDialog.Builder(context,R.style.YourDialogStyle)
        //val builder = android.app.AlertDialog.Builder(context,R.style.YourDialogStyle)
        val inflater = LayoutInflater.from(context)
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH,-1);
        val dialog = inflater.inflate(R.layout.month_picker_dialog, null)
        val fileName = dialog.findViewById(R.id.file_name) as EditText
        fileName.setText(sadhanaFileName)
        fileName.setTextColor(R.attr.highContrastReverseTextColor)
        val monthPicker = dialog.findViewById(R.id.picker_month) as NumberPicker
        val yearPicker = dialog.findViewById(R.id.picker_year) as NumberPicker
        monthPicker.displayedValues = PICKER_DISPLAY_MONTHS_NAMES
        val filePath = dialog.findViewById(R.id.file_path) as TextView
        filePath.text = unit;
        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.value = cal.get(Calendar.MONTH) + 1
        val year = cal.get(Calendar.YEAR)
        yearPicker.minValue = year - 3
        yearPicker.maxValue = year + 1
        yearPicker.value = year



        builder.setView(dialog)
                // Add action buttons
                .setPositiveButton(Html.fromHtml("<font color='#FF7F27'>OK</font>"), DialogInterface.OnClickListener { dialog, id -> callback.onDateSet(yearPicker.value, monthPicker.value, fileName.text.toString()) })
                .setNegativeButton(Html.fromHtml("<font color='#FF7F27'>Cancel</font>"), DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })


        val alertDialog = builder.create()
        alertDialog.show()
        val nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        nbutton.setBackgroundColor(Color.WHITE)
        nbutton.setTextColor(Color.BLACK)
        val pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
        pbutton.setBackgroundColor(Color.WHITE)
        pbutton.setTextColor(Color.BLACK)

        setDividerColor(monthPicker, Color.BLUE)
        setDividerColor(yearPicker, Color.BLUE)
        setNumberPickerTextColor(monthPicker, Color.BLUE)
        setNumberPickerTextColor(yearPicker, Color.BLUE)

        return alertDialog
    }

    private fun setDividerColor(picker: NumberPicker, color: Int) {
        try {
            val pickerFields = NumberPicker::class.java.declaredFields
            for (pf in pickerFields) {
                if (pf.name == "mSelectionDivider") {
                    pf.isAccessible = true
                    try {
                        val colorDrawable = ColorDrawable(color)
                        pf.set(picker, colorDrawable)
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                    } catch (e: Resources.NotFoundException) {
                        e.printStackTrace()
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    }

                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()

        }

    }

    fun setNumberPickerTextColor(numberPicker: NumberPicker, color: Int): Boolean {
        val count = numberPicker.childCount
        for (i in 0..count - 1) {
            val child = numberPicker.getChildAt(i)
            if (child is EditText) {
                try {
                    val selectorWheelPaintField = numberPicker.javaClass
                            .getDeclaredField("mSelectorWheelPaint")
                    selectorWheelPaintField.isAccessible = true
                    (selectorWheelPaintField.get(numberPicker) as Paint).color = Color.GRAY
                    val colors = ColorStateList(
                            arrayOf(intArrayOf(android.R.attr.state_selected), intArrayOf(-android.R.attr.state_selected)),
                            intArrayOf(color, color)
                    )
                    child.setTextColor(colors)
                    numberPicker.invalidate()
                    return true
                } catch (e: NoSuchFieldException) {
                    Log.w("setNumbPickerTextColor", e)
                } catch (e: IllegalAccessException) {
                    Log.w("setNumbPickerTextColor", e)
                } catch (e: IllegalArgumentException) {
                    Log.w("setNumbPickerTextColor", e)
                }

            }
        }
        return false
    }
}