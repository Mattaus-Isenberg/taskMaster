package com.echokinetic.taskmaster;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Task.class}, exportSchema = false, version = 2)
public abstract class TaskDB extends RoomDatabase
{
    public abstract TaskDAO taskDao();
}
