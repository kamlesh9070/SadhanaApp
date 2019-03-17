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

package org.purecelibacy.sadhana.core.models.memory;

import android.support.annotation.*;

import org.purecelibacy.sadhana.core.Config;
import org.purecelibacy.sadhana.core.models.*;
import org.purecelibacy.sadhana.core.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory implementation of {@link CheckmarkList}.
 */
public class MemoryCheckmarkList extends CheckmarkList
{
    ArrayList<Checkmark> list;

    public MemoryCheckmarkList(Habit habit)
    {
        super(habit);
        list = new ArrayList<>();
    }

    @Override
    public void add(List<Checkmark> checkmarks)
    {
        list.addAll(checkmarks);
        Collections.sort(list,
            (c1, c2) -> c2.getTimestamp().compare(c1.getTimestamp()));
    }



    public synchronized Map<String,Integer> getGroupByMonth() {

        Map<String,Integer> monthGroupBy = new LinkedHashMap<String,Integer>();
        for(Checkmark checkmark : list) {
            String month = Config.monthSDF.format(checkmark.getTimestamp().toJavaDate());
            if(monthGroupBy.containsKey(month)) {
                int count = (int)monthGroupBy.get(month);
                monthGroupBy.put(month,++count);
            } else {
                monthGroupBy.put(month,1);
            }
        }
        System.out.println(monthGroupBy);
        return monthGroupBy;
    }

    public List<Checkmark> groupBy(DateUtils.TruncateField field)
    {
        HashMap<Timestamp, Integer> groups = getGroupedValues(field);
        List<Checkmark> checkmarks = groupsToAvgScores(groups);
        Collections.sort(checkmarks, (s1, s2) -> s2.compareNewer(s1));
        return checkmarks;
    }

    @Override
    @NonNull
    public List<Checkmark> toList()
    {

        return new LinkedList<>(list);
    }

    @NonNull
    private HashMap<Timestamp, Integer> getGroupedValues(DateUtils.TruncateField field)
    {
        HashMap<Timestamp, Integer> groups = new HashMap<>();

        for (Checkmark s : list)
        {
            int targetValue = 1;
            if(habit.isNumerical())
                targetValue = (int)habit.getTargetValue();
            if(s.getValue() >= targetValue) {
                Timestamp groupTimestamp = new Timestamp(
                        DateUtils.truncate(field, s.getTimestamp().getUnixTime()));

                if (!groups.containsKey(groupTimestamp)) {
                    groups.put(groupTimestamp, 1);
                }
                else
                    groups.put(groupTimestamp,groups.get(groupTimestamp)+1);
            }

        }

        return groups;
    }

    @NonNull
    private List<Checkmark> groupsToAvgScores(HashMap<Timestamp, Integer> groups)
    {
        List<Checkmark> scores = new LinkedList<>();

        for (Timestamp timestamp : groups.keySet())
        {
/*
            Integer meanValue = 1;
            ArrayList<Integer> groupValues = groups.get(timestamp);

            for (Integer v : groupValues) meanValue += v;
            meanValue /= groupValues.size();
*/

            scores.add(new Checkmark(timestamp, groups.get(timestamp)));
        }

        return scores;
    }

    @NonNull
    @Override
    public synchronized List<Checkmark> getByInterval(Timestamp from,
                                                      Timestamp to)
    {
        compute();
        getGroupByMonth();
        Timestamp newestComputed = new Timestamp(0);
        Timestamp oldestComputed = new Timestamp(0).plus(1000000);

        Checkmark newest = getNewestComputed();
        Checkmark oldest = getOldestComputed();
        if(newest != null) newestComputed = newest.getTimestamp();
        if(oldest != null) oldestComputed = oldest.getTimestamp();

        List<Checkmark> filtered = new ArrayList<>(
            Math.max(0, oldestComputed.daysUntil(newestComputed) + 1));

        for(int i = 0; i <= from.daysUntil(to); i++)
        {
            Timestamp t = to.minus(i);
            if(t.isNewerThan(newestComputed) || t.isOlderThan(oldestComputed))
                filtered.add(new Checkmark(t, Checkmark.UNCHECKED));
            else
                filtered.add(list.get(t.daysUntil(newestComputed)));
        }

        return filtered;
    }

    @Override
    public void invalidateNewerThan(Timestamp timestamp)
    {
        list.clear();
        observable.notifyListeners();
    }

    @Override
    @Nullable
    protected Checkmark getOldestComputed()
    {
        if(list.isEmpty()) return null;
        return list.get(list.size()-1);
    }

    @Override
    @Nullable
    protected Checkmark getNewestComputed()
    {
        if(list.isEmpty()) return null;
        return list.get(0);
    }

}
