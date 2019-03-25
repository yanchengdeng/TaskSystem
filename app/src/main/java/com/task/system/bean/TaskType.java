package com.task.system.bean;

import java.io.Serializable;

public class TaskType implements Serializable {
    public int type;
    public String name;

    public TaskType(int type, String name) {
        this.type = type;
        this.name = name;
    }
}
