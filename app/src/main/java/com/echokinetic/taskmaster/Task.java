package com.echokinetic.taskmaster;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.amazonaws.amplify.generated.graphql.ListTasksQuery;


@Entity
public class Task {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String title;
    private String body;
    private int dueDate;
    private String unique_File_ID;
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
        this.unique_File_ID = "";

    }

    @Ignore
    public Task(String title, String body, TaskState state)
    {
        this.title = title;
        this.body = body;
        this.state = state;
        this.dueDate = 0;
        this.unique_File_ID = "";
    }

    @Ignore
    public Task(String title, String body, TaskState state, int dueDate)
    {
        this.title = title;
        this.body = body;
        this.state = state;
        this.dueDate = dueDate;
        this.unique_File_ID = "";
    }

    @Ignore
    public Task(String title, String body, TaskState state, int dueDate, String uniqueID)
    {
        this.title = title;
        this.body = body;
        this.state = state;
        this.dueDate = dueDate;
        this.unique_File_ID = uniqueID;
    }

    @Ignore
    public Task(ListTasksQuery.Item item)
    {
        this.dynamoDbId = item.id();
        this.title = item.title();
        this.body = item.body();
        this.state = TaskStateChange.newState(item.state().ordinal());
        this.unique_File_ID = item.unique_File_ID();
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

    public String getDynamoDbId() {
        return dynamoDbId;
    }

    public void setDynamoDbId(String dynamoDbId) {
        this.dynamoDbId = dynamoDbId;
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

    public String getUnique_File_ID() {
        return unique_File_ID;
    }

    public void setUnique_File_ID(String unique_File_ID) {
        this.unique_File_ID = unique_File_ID;
    }
}