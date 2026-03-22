package ro.utcn.taskManagement.logic;

import ro.utcn.taskManagement.model.Employee;
import ro.utcn.taskManagement.model.Task;

import java.util.*;

public class Utility {
    // Filters all employees who have a work duration greater than 40 hours,
    // sorts them in ascending order according to the work duration,
    // displays their names in the console, and returns the sorted list for GUI integration.
    // param->tm The TaskManagement instance. return->A sorted List of Employees with more than 40 hours of work.
    public static List<Employee> filtersEmployee(TaskManagement tm){
        List<Employee> filteredList=new ArrayList<Employee>();
        Map<Employee, Integer> durations=new HashMap<>();

        for(Employee e: tm.getTaskMap().keySet()){
            int duration=tm.calculateEmployeeWorkDuration(e.getIdEmployee());
            if(duration>40){
                durations.put(e,duration);
                filteredList.add(e);
            }
        }

        filteredList.sort(new Comparator<Employee>() {
            @Override
            public int compare(Employee e1, Employee e2) {
                return durations.get(e1)-durations.get(e2);
            }
        });

        System.out.println("The employees with the work duration > 40:");
        if(filteredList.isEmpty()){
            System.out.println("There is no employee with the work duration > 40");
        }
        for(Employee e: filteredList){
            System.out.println(e.getName()+ " has " + durations.get(e)+ " completed hours.");
        }

        return filteredList;
    }

    // Computes the statistics of completed and uncompleted tasks for each employee.
    // param->tm The TaskManagement instance containing the data.
    // return->A Map linking employee names to their task statistics.
    public static Map<String, Map<String, Integer>> computesTasks(TaskManagement tm){
        Map<String, Map<String, Integer>> resStats=new HashMap<>();

        for(Map.Entry<Employee, List<Task>> entry: tm.getTaskMap().entrySet()){
            String name=entry.getKey().getName();
            Map<String, Integer> taskCount =new HashMap<>();
            int completedCount =0;
            int uncompletedCount =0;
            for(Task task: entry.getValue()){
                if(task.getStatusTask().equalsIgnoreCase("Completed")){
                    completedCount++;
                }else uncompletedCount++;
            }
            taskCount.put("Completed", completedCount);
            taskCount.put("Uncompleted", uncompletedCount);

            resStats.put(name, taskCount);
        }
        return resStats;
    }
}
