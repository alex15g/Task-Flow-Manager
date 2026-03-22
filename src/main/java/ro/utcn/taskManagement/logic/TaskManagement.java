package ro.utcn.taskManagement.logic;

import ro.utcn.taskManagement.dataaccess.SerializationOperations;
import ro.utcn.taskManagement.model.ComplexTask;
import ro.utcn.taskManagement.model.Employee;
import ro.utcn.taskManagement.model.Task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ro.utcn.taskManagement.dataaccess.SerializationOperations.loadDatabase;
import static ro.utcn.taskManagement.dataaccess.SerializationOperations.saveDatabase;

public class TaskManagement implements Serializable {
    private Map<Employee, List<Task>> taskMap;

    public TaskManagement() {
        taskMap = new HashMap<Employee, List<Task>>();
    }

    // Adds a new employee to the system if the ID is unique
    public void addEmployee(Employee e) {
        if(searchEmployee(e.getIdEmployee()) == null) {
            taskMap.put(e, new ArrayList<>());
        }
    }

    // Searches for an employee by their unique ID
    public Employee searchEmployee(int idEmployee){
        for(Employee e: taskMap.keySet()){
            if(e.getIdEmployee()==idEmployee){
                return e;
            }
        }
        return null;
    }

    // Links a new root-level task to a specific employee
    public void assignTaskToEmployee(int idEmployee, Task task) {
        Employee e=searchEmployee(idEmployee);
        if(e!=null){
            taskMap.get(e).add(task);
        }
    }

    // Calculates total hours only for tasks marked as "Completed"
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

    // Recursively searches for a task by its ID within a given list of tasks
    // and their  sub-tasks (DFS)
    private Task findTaskRecursively(List<Task> tasks, int targetId) {
        if (tasks == null) return null;
        for (Task t : tasks) {
            if (t.getIdTask() == targetId) return t; // found

            // If it is complex, dig deeper into subtasks
            if (t instanceof ComplexTask) {
                Task foundInside = findTaskRecursively(((ComplexTask) t).getSubTasks(), targetId);
                if (foundInside != null) return foundInside;
            }
        }
        return null; //not found
    }

    public void modifyTaskStatus(int idEmployee, int idTask, String newStatus) throws Exception {
        Employee e = searchEmployee(idEmployee);
        if (e == null) throw new Exception("Employee not found!");

        List<Task> tasks = taskMap.get(e);
        if (tasks == null || tasks.isEmpty()) throw new Exception("No assigned tasks!");

        Task targetTask = findTaskRecursively(tasks, idTask);

        if (targetTask != null) {
            targetTask.setStatusTask(newStatus);
        } else {
            throw new Exception("Task with ID " + idTask + " not found!");
        }
    }

    public Map<Employee, List<Task>> getTaskMap() {
        return taskMap;
    }


    //Adds a sub-task to an existing ComplexTask for a specific employee.
    public void addSubTaskToComplexTask(int idEmp, int idComplex, Task subTask) throws Exception {
        Employee emp = searchEmployee(idEmp);
        if (emp == null) throw new Exception("Employee not found!");

        List<Task> tasks = this.getTaskMap().get(emp);
        if (tasks == null || tasks.isEmpty()) throw new Exception("No assigned tasks!");

        // Delegate the deep search to the recursive helper method
        Task parent = findTaskRecursively(tasks, idComplex);
        if (parent == null) throw new Exception("Parent Task not found!");

        // Verify the Composite pattern constraint before adding
        if (parent instanceof ComplexTask) {
            ((ComplexTask) parent).addTask(subTask);
        } else {
            throw new Exception("Parent is not a ComplexTask!");
        }
    }

    // Methods to respect the Layered Architecture
    // to restore the data from a file(deserialization)
    public static TaskManagement loadSystem(String fileName) {
        return loadDatabase(fileName);
    }

    // saves the current data from the system to a file(serialization)
    public void saveSystem(String fileName) {
        saveDatabase(this, fileName);
    }
}
