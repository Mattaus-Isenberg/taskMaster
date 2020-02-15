package com.echokinetic.taskmaster;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity
public class Task implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String title;
    private String body;

    @TypeConverters(TaskStateChange.class)
    private TaskState state;

    @Ignore
    public Task(String title, String body)
    {
        this.title = title;
        this.body = body;
        this.state = TaskState.NEW;
    }

    @Ignore
    public Task(String title, String body, TaskState state)
    {
        this.title = title;
        this.body = body;
        this.state = state;
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

    protected Task(Parcel in) {
        title = in.readString();
        body = in.readString();
        //state = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(body);
       // dest.writeString(state);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

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
}