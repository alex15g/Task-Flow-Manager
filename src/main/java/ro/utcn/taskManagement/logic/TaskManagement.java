package ro.utcn.taskManagement.logic;

import ro.utcn.taskManagement.model.Employee;
import ro.utcn.taskManagement.model.Task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManagement implements Serializable {
    private Map<Employee, List<Task>> taskMap;

    public TaskManagement() {
        taskMap = new HashMap<Employee, List<Task>>();
    }

    public void addEmployee(Employee e) {
        if(searchEmployee(e.getIdEmployee()) == null) {
            taskMap.put(e, new ArrayList<>());
        }
    }

    private Employee searchEmployee(int idEmployee){
        for(Employee e: taskMap.keySet()){
            if(e.getIdEmployee()==idEmployee){
                return e;
            }
        }
        return null;
    }

    public void assignTaskToEmployee(int idEmployee, Task task) {
        Employee e=searchEmployee(idEmployee);
        if(e!=null){
            taskMap.get(e).add(task);
        }
    }

    public int calculateEmployeeWorkDuration(int idEmployee){
        int duration=0;
        Employee e = searchEmployee(idEmployee);
        if(e!=null){
            List<Task> tasks=taskMap.get(e);
            for (Task t : tasks) {
                if ("Completed".equalsIgnoreCase(t.getStatusTask())) {
                    duration += t.estimateDuration();
                }
            }
        }
        return duration;
    }

    public void modifyTaskStatus(int idEmployee, int idTask, String newStatus) {
        Employee e=searchEmployee(idEmployee);
        if(e!=null){
            List<Task> tasks=taskMap.get(e);
            for (Task t : tasks) {
                if(t.getIdTask()==idTask){
                    t.setStatusTask(newStatus);
                    break;
                }
            }
        }
    }

    public Map<Employee, List<Task>> getTaskMap() {
        return taskMap;
    }
}
