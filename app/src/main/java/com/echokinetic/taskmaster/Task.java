package com.echokinetic.taskmaster;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.amazonaws.amplify.generated.graphql.ListTasksQuery;

import java.math.BigInteger;

@Entity
public class Task {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String title;
    private String body;
    private int dueDate;

    public String getDynamoDbId() {
        return dynamoDbId;
    }

    public void setDynamoDbId(String dynamoDbId) {
        this.dynamoDbId = dynamoDbId;
    }

    private String dynamoDbId;

    @TypeConverters(TaskStateChange.class)
    private TaskState state;

    @Ignore
    public Task(String title, String body)
    {
        this.title = title;
        this.body = body;
        this.state = TaskState.NEW;
        this.dueDate = 0;
    }

    @Ignore
    public Task(String title, String body, TaskState state)
    {
        this.title = title;
        this.body = body;
        this.state = state;
        this.dueDate = 0;
    }

    @Ignore
    public Task(String title, String body, TaskState state, int dueDate)
    {
        this.title = title;
        this.body = body;
        this.state = state;
        this.dueDate = dueDate;
    }

    public Task(ListTasksQuery.Item item)
    {
        this.dynamoDbId = item.id();
        this.title = item.title();
        this.body = item.body();
        this.state = TaskStateChange.newState(item.state().ordinal());
    }

    public Task(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }



    public void setState(TaskState state) {
        this.state = state;
    }

    public TaskState getState() {
        return state;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDueDate() {
        return dueDate;
    }

    public void setDueDate(int dueDate) {
        this.dueDate = dueDate;
    }
}