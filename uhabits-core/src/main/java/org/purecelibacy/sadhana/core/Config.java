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

package org.purecelibacy.sadhana.core;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Config
{
    public static final String DATABASE_FILENAME = "MBASadhanaAppDB.db";
    public static int DATABASE_VERSION = 23;

    public static final List<String> SADHANAS = new ArrayList<String>();

    public static final String SEVA_NAME = "Seva";
    public static final String VANCHAN_NAME = "Vanchan";

    static {
        String[] tempSadhanas = new String[]{"Samayik","Vanchan","Vidhi","G. Satsang","Seva"};
        for(String sadhana : tempSadhanas)
            SADHANAS.add(sadhana);
    }

    public static final SimpleDateFormat monthSDF = new SimpleDateFormat("MMM yy");

    public static final int total_RowNum = 34-1;

    public static final int SEVA_REMARK_COLUMN = 6;
}
