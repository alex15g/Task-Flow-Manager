package ro.utcn.taskManagement.model;

import java.io.Serializable;

public sealed abstract class Task implements Serializable permits SimpleTask, ComplexTask {
    private int idTask;
    private String statusTask;

    public Task(int idTask, String statusTask) {
        this.idTask = idTask;
        this.statusTask = statusTask;
    }

    public abstract int estimateDuration();

    public int getIdTask() {
        return idTask;
    }
    public void setIdTask(int idTask) {
        this.idTask = idTask;
    }
    public String getStatusTask() {
        return statusTask;
    }
    public void setStatusTask(String statusTask) {
        this.statusTask = statusTask;
    }

    public String toString(){
        return "ID: " + idTask + " Status: " + statusTask;
    };
}
