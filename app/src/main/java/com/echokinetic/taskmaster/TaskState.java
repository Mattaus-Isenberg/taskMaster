package com.echokinetic.taskmaster;

public enum TaskState
{
    NEW(0), ASSIGNED(1), IN_PROGRESS(2), COMPLETE(3), HIGH_PRIORITY(4);


    private int status_Code;

    TaskState(int status_Code)
    {
        this.status_Code = status_Code;
    }

    public int getStatus_Code() {
        return status_Code;
    }
}
