/*
 * Copyright (C) 2017 Álinson Santos Xavier <isoron@gmail.com>
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

package org.purecelibacy.androidbase;

import android.content.*;
import android.os.Environment;
import android.support.annotation.*;
import android.support.v4.content.*;
import android.util.*;

import org.purecelibacy.androidbase.utils.FileUtils;

import java.io.*;

import javax.inject.*;

public class AndroidDirFinder
{
    @NonNull
    private Context context;

    @Nullable
    public File getFilesDir(@Nullable String relativePath)
    {
        File externalFilesDirs[] = //Environment.getExternalStorageDirectory()+ "/attendance_taker/";
            ContextCompat.getExternalFilesDirs(context, null);
        if (externalFilesDirs == null)
        {
            Log.e("BaseSystem",
                "getFilesDir: getExternalFilesDirs returned null");
            return null;
        }

        File dir = new File(Environment.getExternalStorageDirectory() + "/Sadhana/"+relativePath);
        dir.mkdir();
        return dir;
        //return new File(Environment.getExternalStorageDirectory()+ "/mba_sadhana/");
        //return FileUtils.getDir(externalFilesDirs, relativePath);
    }

    @Inject
    public AndroidDirFinder(@NonNull @AppContext Context context)
    {
        this.context = context;
    }
}
