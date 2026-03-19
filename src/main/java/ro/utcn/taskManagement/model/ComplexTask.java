package ro.utcn.taskManagement.model;

import java.util.ArrayList;
import java.util.List;

public final class ComplexTask extends Task{
    private List<Task> subTasks;
    public ComplexTask(int idTask, String statusTask) {
        super(idTask, statusTask);
        this.subTasks = new ArrayList<>();
    }

    public void addTask(Task task){
        this.subTasks.add(task);
    }
    public void deleteTask(Task task){
        this.subTasks.remove(task);
    }

    public int estimateDuration(){
        int duration = 0;
        for(Task t : this.subTasks){
            duration += t.estimateDuration();
        }
        return duration;
    }

    public String toString(){
        return "Complex Task: "+super.toString()+", Subtasks count: "+subTasks.size();
    }
}
