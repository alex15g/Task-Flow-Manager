package ro.utcn.taskManagement.logic;

import ro.utcn.taskManagement.model.Employee;
import ro.utcn.taskManagement.model.Task;

import java.util.*;

public class Utility {
    public static void filtersEmployee(TaskManagement tm){
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
    }

    public static Map<String, Map<String, Integer>> computesTasks(TaskManagement tm){
        Map<String, Map<String, Integer>> rezStats=new HashMap<>();


        for(Map.Entry<Employee, List<Task>> entry: tm.getTaskMap().entrySet()){
            String name=entry.getKey().getName();
            Map<String, Integer> taskCon=new HashMap<>();
            int completedCon=0;
            int uncompletedCon=0;
            for(Task task: entry.getValue()){
                if(task.getStatusTask().equalsIgnoreCase("Completed")){
                    completedCon++;
                }else uncompletedCon++;
            }
            taskCon.put("Completed", completedCon);
            taskCon.put("Uncompleted", uncompletedCon);

            rezStats.put(name,taskCon);
        }
        return rezStats;
    }
}
