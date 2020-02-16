package com.echokinetic.taskmaster;

import androidx.room.TypeConverter;

import static com.echokinetic.taskmaster.TaskState.ASSIGNED;
import static com.echokinetic.taskmaster.TaskState.COMPLETE;
import static com.echokinetic.taskmaster.TaskState.HIGH_PRIORITY;
import static com.echokinetic.taskmaster.TaskState.IN_PROGRESS;
import static com.echokinetic.taskmaster.TaskState.NEW;

public class TaskStateChange
{
    @TypeConverter
    public static TaskState newState(int state)
    {
        if(state == NEW.getStatus_Code())
        {
            return NEW;
        }
        else if(state == ASSIGNED.getStatus_Code())
        {
            return ASSIGNED;
        }
        else if(state == IN_PROGRESS.getStatus_Code())
        {
            return IN_PROGRESS;
        }
        else if(state == COMPLETE.getStatus_Code())
        {
            return COMPLETE;
        }
        else if(state == HIGH_PRIORITY.getStatus_Code())
        {
            return HIGH_PRIORITY;
        }
        else
        {
            throw new IllegalArgumentException("INVALID STATUS");
        }
    }

    @TypeConverter
    public static int toInt(TaskState state)
    {
        return state.getStatus_Code();
    }
}
