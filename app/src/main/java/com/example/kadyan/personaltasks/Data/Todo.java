package com.example.kadyan.personaltasks.Data;

import java.io.Serializable;

public class Todo implements Serializable {

    private String title;
    private String description;
    private String dueDate;
    private int priority;
    private long timeOfAddition;

    public Todo(String title, String description, String date, int priority, long timeOfAddition) {
        this.title = title;
        this.description = description;
        this.dueDate = date;
        this.priority = priority;
        this.timeOfAddition = timeOfAddition;
    }

    public String getDueDate() {
        return dueDate;
    }

    public int getPriority() {
        return priority;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getTimeOfAddition() {
        return timeOfAddition;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
