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

    // Calculates total duration by summing up the durations of all sub-tasks
    public int estimateDuration(){
        int duration = 0;
        for(Task t : this.subTasks){
            duration += t.estimateDuration();
        }
        return duration;
    }

    // Recursively calculates the TOTAL number of sub-tasks
    public int getTotalSubtasksCount() {
        int count = 0;
        for (Task t : this.subTasks) {
            count++; // Count the immediate child

            if (t instanceof ComplexTask) {
                count += ((ComplexTask) t).getTotalSubtasksCount();
            }
        }
        return count;
    }

    @Override
    public String toString() {
        return "Complex Task: " + super.toString() + ", Total subtasks: " + getTotalSubtasksCount();
    }

    public List<Task> getSubTasks() {
        return subTasks;
    }
}
