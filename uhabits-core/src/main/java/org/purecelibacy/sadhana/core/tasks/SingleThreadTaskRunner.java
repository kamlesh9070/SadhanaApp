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

package org.purecelibacy.sadhana.core.tasks;

import java.util.*;

public class SingleThreadTaskRunner implements TaskRunner
{
    private List<Listener> listeners = new LinkedList<>();

    @Override
    public void addListener(Listener listener)
    {
        listeners.add(listener);
    }

    @Override
    public void execute(Task task)
    {
        for(Listener l : listeners) l.onTaskStarted(task);
        task.onAttached(this);
        task.onPreExecute();
        task.doInBackground();
        task.onPostExecute();
        for(Listener l : listeners) l.onTaskFinished(task);
    }

    @Override
    public int getActiveTaskCount()
    {
        return 0;
    }

    @Override
    public void publishProgress(Task task, int progress)
    {
        task.onProgressUpdate(progress);
    }

    @Override
    public void removeListener(Listener listener)
    {
        listeners.remove(listener);
    }
}
