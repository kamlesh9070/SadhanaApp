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

package org.youth.sadhana.activities.common.dialogs;

import org.youth.sadhana.core.ui.callbacks.*;
import org.youth.sadhana.utils.*;

/**
 * Dialog that allows the user to choose a color.
 */
public class ColorPickerDialog extends com.android.colorpicker.ColorPickerDialog
{
    public void setListener(OnColorPickedCallback callback)
    {
        super.setOnColorSelectedListener(c ->
        {
            c = PaletteUtils.colorToPaletteIndex(getContext(), c);
            callback.onColorPicked(c);
        });
    }
}
