package ro.utcn.taskManagement;

import ro.utcn.taskManagement.logic.TaskManagement;
import ro.utcn.taskManagement.logic.Utility;
import ro.utcn.taskManagement.model.*;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        TaskManagement tm = new TaskManagement();

        Employee alex = new Employee(1, "Alex");
        tm.addEmployee(alex);

        //TEST 1: COMPOSITE PATTERN & DURATION
        // Level 1: Root task
        ComplexTask root = new ComplexTask(100, "Completed");
        tm.assignTaskToEmployee(1, root);

        // Level 2: Nested complex task
        ComplexTask subComplex = new ComplexTask(200, "Completed");
        tm.addSubTaskToComplexTask(1, 100, subComplex);

        // Level 3: Simple tasks (5h + 4h)
        tm.addSubTaskToComplexTask(1, 200, new SimpleTask("Completed", 301, 10, 15));
        tm.addSubTaskToComplexTask(1, 200, new SimpleTask("Completed", 302, 22, 2));

        System.out.println("TEST 1: Total Duration");
        System.out.println("Expected: 9 | Actual: " + tm.calculateEmployeeWorkDuration(1));

        // TEST 2: STATUS IMPACT
        System.out.println("\nTEST 2: Status Change Check ");
        // Changing status shouldn't affect duration
        tm.modifyTaskStatus(1, 301, "In Progress");
        System.out.println("Expected (still 9): 9 | Actual: " + tm.calculateEmployeeWorkDuration(1));

        //TEST 3: SORTING
        System.out.println("\nTEST 3: Sorting Validation");
        Employee ion = new Employee(2, "Ion"); // 50h
        Employee ana = new Employee(3, "Ana"); // 45h
        tm.addEmployee(ion);
        tm.addEmployee(ana);

        tm.assignTaskToEmployee(2, new SimpleTask("Completed", 401, 0, 25));
        tm.assignTaskToEmployee(2, new SimpleTask("Completed", 402, 0, 25));
        tm.assignTaskToEmployee(3, new SimpleTask("Completed", 501, 0, 20));
        tm.assignTaskToEmployee(3, new SimpleTask("Completed", 502, 0, 25));

        // Utility method prints and returns sorted list
        List<Employee> filtered = Utility.filtersEmployee(tm);
        if(filtered.size() >= 2) {
            System.out.println("First: " + filtered.get(0).getName() + " (Expected: Ana)");
            System.out.println("Second: " + filtered.get(1).getName() + " (Expected: Ion)");
        }

        // TEST 4: SERIALIZATION
        System.out.println("\n TEST 4: Serialization Check");
        tm.saveSystem("final_test.ser");
        TaskManagement loaded = TaskManagement.loadSystem("final_test.ser");

        try {
            int restoredDuration = loaded.calculateEmployeeWorkDuration(1);
            System.out.println("Restored duration: " + restoredDuration + " (Expected: 9)");

            if(restoredDuration == 9) {
                System.out.println("SUCCESS: Object graph restored correctly.");
            } else {
                System.out.println("FAILURE: Data mismatch.");
            }
        } catch (Exception e) {
            System.out.println("ERROR: Loading failed - " + e.getMessage());
        }

        //TEST 5: EDGE CASES & ERROR HANDLING
        System.out.println("\nTEST 5: Error Handling & Edge Cases");

        // 5.1 Duplicate Employee Check
        int initialSize = tm.getTaskMap().size();
        tm.addEmployee(new Employee(1, "Clone of Alex")); // ID 1 already exists
        if(tm.getTaskMap().size() == initialSize) {
            System.out.println("Success: Duplicate employee ID blocked.");
        } else {
            System.out.println("FAILURE: Duplicate ID allowed!");
        }

        // 5.2 Invalid Task Search
        try {
            // Task 999 does not exist
            tm.modifyTaskStatus(1, 999, "Completed");
            System.out.println("Check: System handled non-existent task ID.");
        } catch (Exception e) {
            System.out.println("Caught Expected Error: " + e.getMessage());
        }

        // 5.3 Empty System Utility Check
        TaskManagement emptySystem = new TaskManagement();
        try {
            Utility.filtersEmployee(emptySystem);
            Utility.computesTasks(emptySystem);
            System.out.println("Success: Utility methods handled empty system without crashing.");
        } catch (Exception e) {
            System.out.println("FAILURE: Utility crashed on empty system! " + e.getMessage());
        }
    }
}