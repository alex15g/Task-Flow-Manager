package ro.utcn.taskManagement.gui;

import ro.utcn.taskManagement.logic.TaskManagement;
import ro.utcn.taskManagement.logic.Utility;
import ro.utcn.taskManagement.model.ComplexTask;
import ro.utcn.taskManagement.model.Employee;
import ro.utcn.taskManagement.model.SimpleTask;
import ro.utcn.taskManagement.model.Task;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import static ro.utcn.taskManagement.logic.Utility.filtersEmployee;

// The main GUI for the Task Management application.
// It provides tabs for managing employees, tasks, and viewing statistics.
// The application state is automatically loaded on startup and saved on exit.

public class MainFrame extends JFrame {
    private TaskManagement tm;
    private final String FILE_NAME = "database.ser";

    private DefaultTableModel employeeTableModel;
    private JTable employeeTable;

    public MainFrame() {
        tm = TaskManagement.loadSystem(FILE_NAME);

        setTitle("Task Management System");
        setSize(800, 600);
        setLocationRelativeTo(null); // O->the centre of the screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Employee Management", createEmployeeTab());
        tabbedPane.addTab("Task Management", createTaskTab());
        tabbedPane.addTab("Statistics", createStatisticsTab());


        add(tabbedPane, BorderLayout.CENTER);


        // When closing the app->ensures data persistance
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                tm.saveSystem(FILE_NAME);
            }
        });
    }

    // Creates the tab responsible for displaying employees, adding new employees,
    //and filtering them.
    //return->A JPanel containing the employee management.

    private JPanel createEmployeeTab() {
        JPanel panel = new JPanel(new BorderLayout());

        // center: employee table
        String[] columns = {"Employee ID", "Name", "Total Hours (Completed)"};
        employeeTableModel = new DefaultTableModel(columns, 0);
        employeeTable = new JTable(employeeTableModel);

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // south: controls
        JPanel bottomPanel = new JPanel(new FlowLayout());

        JTextField idField = new JTextField(5);
        JTextField nameField = new JTextField(15);
        JButton btnAddEmployee = new JButton("Add Employee");
        JButton btnFilter = new JButton("Filter > 40 hours");

        bottomPanel.add(new JLabel("ID:"));
        bottomPanel.add(idField);
        bottomPanel.add(new JLabel("Name:"));
        bottomPanel.add(nameField);
        bottomPanel.add(btnAddEmployee);
        bottomPanel.add(btnFilter);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        // 3. Action Listeners
        btnAddEmployee.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText().trim();

                if (name.isEmpty()) {
                    throw new Exception("Name cannot be empty.");
                }

                Employee empNew = new Employee(id, name);
                tm.addEmployee(empNew);
                refreshEmployeeTable();

                idField.setText("");
                nameField.setText("");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID must be a number!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnViewTasks = new JButton("View employee Tasks");
        bottomPanel.add(btnViewTasks);

        btnViewTasks.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                Employee emp = tm.searchEmployee(id);
                if (emp == null) throw new Exception("Employee not found!");

                java.util.List<Task> tasks = tm.getTaskMap().get(emp);
                if (tasks == null || tasks.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No tasks assigned to this employee");
                    return;
                }

                JTextArea textArea = new JTextArea(10, 30);
                textArea.setEditable(false);
                for (ro.utcn.taskManagement.model.Task t : tasks) {
                    textArea.append(t.toString() + " | Duration: " + t.estimateDuration() + "h\n");
                }


                JScrollPane scrollTasks = new JScrollPane(textArea);
                JOptionPane.showMessageDialog(this, scrollTasks, "Tasks for " + emp.getName(), JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid Employee ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnFilter.addActionListener(e -> {
            java.util.List<Employee> result=filtersEmployee(tm);
            String message="";

            if (((java.util.List<?>) result).isEmpty()) {
                message = "No employee has more than 40 completed hours.";
            } else {
                for (Employee emp : result) {
                    message = message + emp.getName() + " has " + tm.calculateEmployeeWorkDuration(emp.getIdEmployee()) + " completed hours.\n";
                }
            }
            JOptionPane.showMessageDialog(this, message, "Filter Results", JOptionPane.INFORMATION_MESSAGE);
        });

        refreshEmployeeTable();
        return panel;
    }

    //Creates the tab responsible for assigning tasks, modifying their status,
    //and managing the composition of ComplexTasks.
    //return->A JPanel containing the task management user interface.
    private JPanel createTaskTab() {
        JPanel panel = new JPanel(new BorderLayout());


        // Control Area
        JPanel controlPanel = new JPanel(new GridLayout(4, 1));

        // ROW 1: Assign Root Task
        JPanel assignPanel = new JPanel(new FlowLayout());
        JTextField idEmpField = new JTextField(4);
        JTextField idTaskField = new JTextField(4);
        JComboBox<String> typeTaskCombo = new JComboBox<>(new String[]{"SimpleTask", "ComplexTask"});
        JTextField startField = new JTextField(3);
        JTextField endField = new JTextField(3);
        JButton btnAssign = new JButton("Assign Task (Root)");

        // Dynamic logic: Disable start/end hours for ComplexTasks
        typeTaskCombo.addActionListener(e -> {
            boolean isSimple = typeTaskCombo.getSelectedItem().equals("SimpleTask");
            startField.setEnabled(isSimple);
            endField.setEnabled(isSimple);

            if (!isSimple) {
                startField.setText("");
                endField.setText("");
            }
        });

        assignPanel.add(new JLabel("ID Emp:")); assignPanel.add(idEmpField);
        assignPanel.add(new JLabel("ID Task:")); assignPanel.add(idTaskField);
        assignPanel.add(new JLabel("Type:")); assignPanel.add(typeTaskCombo);
        assignPanel.add(new JLabel("Start:")); assignPanel.add(startField);
        assignPanel.add(new JLabel("End:")); assignPanel.add(endField);
        assignPanel.add(btnAssign);

        // ROW 2: Modify Task Status
        JPanel statusPanel = new JPanel(new FlowLayout());
        JTextField idEmpStatusField = new JTextField(4);
        JTextField idTaskStatusField = new JTextField(4);
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Completed", "Uncompleted", "In Progress"});
        JButton btnStatus = new JButton("Update Status");

        statusPanel.add(new JLabel("ID Emp:")); statusPanel.add(idEmpStatusField);
        statusPanel.add(new JLabel("ID Task:")); statusPanel.add(idTaskStatusField);
        statusPanel.add(new JLabel("New Status:")); statusPanel.add(statusCombo);
        statusPanel.add(btnStatus);

        // ROW 3: Sub-Task Inputs (Composite Pattern Builder)
        JPanel subTaskInputs = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField idEmpSubField = new JTextField(4);
        JTextField idComplexTaskField = new JTextField(4);
        JTextField idSubTaskField = new JTextField(4);

        JComboBox<String> subTaskTypeCombo = new JComboBox<>(new String[]{"SimpleTask", "ComplexTask"});
        JTextField subStartField = new JTextField(3);
        JTextField subEndField = new JTextField(3);

        subTaskTypeCombo.addActionListener(e -> {
            boolean isSimple = "SimpleTask".equals(subTaskTypeCombo.getSelectedItem());
            subStartField.setEnabled(isSimple);
            subEndField.setEnabled(isSimple);
            if (!isSimple) {
                subStartField.setText("");
                subEndField.setText("");
            }
        });

        subTaskInputs.add(new JLabel("ID Emp:")); subTaskInputs.add(idEmpSubField);
        subTaskInputs.add(new JLabel("ID Parent Complex:")); subTaskInputs.add(idComplexTaskField);
        subTaskInputs.add(new JLabel("ID New Sub-Task:")); subTaskInputs.add(idSubTaskField);
        subTaskInputs.add(new JLabel("Type:")); subTaskInputs.add(subTaskTypeCombo);
        subTaskInputs.add(new JLabel("Start(h):")); subTaskInputs.add(subStartField);
        subTaskInputs.add(new JLabel("End(h):")); subTaskInputs.add(subEndField);

        // ROW 4: Sub-Task Button
        JPanel subTaskButtonPanel = new JPanel(new FlowLayout());
        JButton btnAddSubTask = new JButton("Add Sub-Task to Complex");
        subTaskButtonPanel.add(btnAddSubTask);

        // Add all rows to the main control panel
        controlPanel.add(assignPanel);
        controlPanel.add(statusPanel);
        controlPanel.add(subTaskInputs);
        controlPanel.add(subTaskButtonPanel);

        panel.add(controlPanel, BorderLayout.NORTH);

        // ACTION LISTENERS (LOGIC)

        btnAssign.addActionListener(e -> {
            try {
                int idEmp = Integer.parseInt(idEmpField.getText());
                int idTask = Integer.parseInt(idTaskField.getText());
                String type = (String) typeTaskCombo.getSelectedItem();

                Task newTask;
                if (type.equals("SimpleTask")) {
                    int start = Integer.parseInt(startField.getText());
                    int end = Integer.parseInt(endField.getText());
                    newTask = new SimpleTask("In Progress", idTask, start, end);
                } else {
                    newTask = new ComplexTask(idTask, "In Progress");
                }

                tm.assignTaskToEmployee(idEmp, newTask);
                refreshEmployeeTable();
                JOptionPane.showMessageDialog(this, "Root task assigned successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnStatus.addActionListener(e -> {
            try {
                int idEmp = Integer.parseInt(idEmpStatusField.getText());
                int idTask = Integer.parseInt(idTaskStatusField.getText());
                String newStatus = (String) statusCombo.getSelectedItem();

                tm.modifyTaskStatus(idEmp, idTask, newStatus);
                refreshEmployeeTable();
                JOptionPane.showMessageDialog(this, "Status updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to update status: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnAddSubTask.addActionListener(e -> {
            try {
                int idEmp = Integer.parseInt(idEmpSubField.getText());
                int idComplex = Integer.parseInt(idComplexTaskField.getText());
                int idSub = Integer.parseInt(idSubTaskField.getText());


                //the type of the subtask
                String selectedType = (String) subTaskTypeCombo.getSelectedItem();
                Task subTaskToInsert;
                if ("SimpleTask".equals(selectedType)) {
                    int start = Integer.parseInt(subStartField.getText());
                    int end = Integer.parseInt(subEndField.getText());
                    subTaskToInsert = new SimpleTask("In Progress", idSub, start, end);
                } else{
                    subTaskToInsert = new ComplexTask(idSub, "In Progress");
                }
                tm.addSubTaskToComplexTask(idEmp, idComplex, subTaskToInsert);

                refreshEmployeeTable();
                JOptionPane.showMessageDialog(this, "Sub-task added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);


            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error building composite task: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    // Creates the tab for displaying task statistics for all employees.
    // return->A JPanel containing the statistics.

    private JPanel createStatisticsTab() {
        JPanel panel = new JPanel(new BorderLayout());

        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        panel.add(new JScrollPane(statsArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton btnGenerateStats = new JButton("Generate Task Statistics");
        bottomPanel.add(btnGenerateStats);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        btnGenerateStats.addActionListener(e -> {
            statsArea.setText("");

            Map<String, Map<String, Integer>> stats = Utility.computesTasks(tm);

            if (stats.isEmpty()) {
                statsArea.append("No data available.");
                return;
            }


            for (Map.Entry<String, Map<String, Integer>> entry : stats.entrySet()) {
                String empName = entry.getKey();
                Map<String, Integer> details = entry.getValue();

                statsArea.append("Employee: " + empName + "\n");
                statsArea.append("  -> Completed Tasks   " + details.get("Completed") + "\n");
                statsArea.append("  -> Uncompleted Tasks: " + details.get("Uncompleted") + "\n");
                statsArea.append("--------------------------------------------------\n");
            }
        });

        return panel;
    }


    private void refreshEmployeeTable() {
        employeeTableModel.setRowCount(0);

        for (Employee emp : tm.getTaskMap().keySet()) {
            int totalHours = tm.calculateEmployeeWorkDuration(emp.getIdEmployee());

            employeeTableModel.addRow(new Object[]{emp.getIdEmployee(), emp.getName(), totalHours});
        }
    }

    //Application entry point.
    public static void main(String[] args) {
        // SwingUtilities ensures that the GUI starts correctly.
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
