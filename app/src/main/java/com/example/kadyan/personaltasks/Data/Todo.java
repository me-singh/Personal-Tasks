package com.example.kadyan.personaltasks.Data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

public class Todo implements Parcelable {

    private Long todoId;

    private String title;
    private String description;
    private long timeOfAddition;

    private Long dueDate;
    private Long dueTime;

    private boolean isImportant;
    private boolean isCompleted;

//    private String reminder;
//    private String repeat;

    public Todo(){
        title = "";
        description = "";
        timeOfAddition = Calendar.getInstance().getTimeInMillis();
        dueDate = null;
        dueTime = null;
        isImportant = false;
        isCompleted = false;
    }

    public Todo(String title, String description, long timeOfAddition, long dueDate,
                long dueTime, boolean isImportant, boolean isCompleted) {
        this.title = title;
        this.description = description;
        this.timeOfAddition = timeOfAddition;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.isImportant = isImportant;
        this.isCompleted = isCompleted;
    }

    protected Todo(Parcel in) {
        if (in.readByte() == 0) {
            todoId = null;
        } else {
            todoId = in.readLong();
        }
        title = in.readString();
        description = in.readString();
        timeOfAddition = in.readLong();
        if (in.readByte() == 0) {
            dueDate = null;
        } else {
            dueDate = in.readLong();
        }
        if (in.readByte() == 0) {
            dueTime = null;
        } else {
            dueTime = in.readLong();
        }
        isImportant = in.readByte() != 0;
        isCompleted = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (todoId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(todoId);
        }
        dest.writeString(title);
        dest.writeString(description);
        dest.writeLong(timeOfAddition);
        if (dueDate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(dueDate);
        }
        if (dueTime == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(dueTime);
        }
        dest.writeByte((byte) (isImportant ? 1 : 0));
        dest.writeByte((byte) (isCompleted ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Todo> CREATOR = new Creator<Todo>() {
        @Override
        public Todo createFromParcel(Parcel in) {
            return new Todo(in);
        }

        @Override
        public Todo[] newArray(int size) {
            return new Todo[size];
        }
    };

    public Long getTodoId() {
        return todoId;
    }

    public void setTodoId(Long todoId) {
        this.todoId = todoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimeOfAddition() {
        return timeOfAddition;
    }

    public void setTimeOfAddition(long timeOfAddition) {
        this.timeOfAddition = timeOfAddition;
    }

    public Long getDueDate() {
        return dueDate;
    }

    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
    }

    public Long getDueTime() {
        return dueTime;
    }

    public void setDueTime(Long dueTime) {
        this.dueTime = dueTime;
    }

    public boolean isImportant() {
        return isImportant;
    }

    public void setImportant(boolean important) {
        isImportant = important;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
