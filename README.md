# Employee Task Flow Manager

A desktop application designed to manage employee workloads, task delegation, and productivity tracking. Built with Java 17, this project implements an Object-Oriented layered architecture to separate business logic from the graphical user interface and data persistence mechanisms.

## 🚀 Key Features

* **Hierarchical Task Management:** Utilizes the Composite Design Pattern to allow the creation of atomic tasks (`SimpleTask`) and nested tasks (`ComplexTask`).
* **Automated Time Tracking:** Recursively calculates the total estimated duration of complex task trees and tracks overall employee hours based strictly on "Completed" tasks.
* **Productivity Analytics:** Generates reports detailing completed vs. uncompleted tasks per employee and filters staff exceeding the standard 40-hour work threshold.
* **Data Persistence:** Ensures zero data loss between sessions using Java Serialization for the entire application state.

## 🛠️ Technical Stack

* **Language:** Java 17+ (Features `sealed` classes for task hierarchy integrity).
* **Architecture:** Layered (GUI, Business Logic, Data Access, Model).
* **GUI Framework:** Java Swing.

## 📸 Application Interface

### Employee Overview & Task Hierarchy
Managers can view all registered employees, their total logged hours, and inspect the specific recursive structure of their assigned tasks.
![Employee Management](<img width="1562" height="1177" alt="ss_task_flow_manager_1" src="https://github.com/user-attachments/assets/7a5fe988-7ba3-4ff4-8c80-d69cb85866d3" />
)

### Task Assignment & Status Updates
The system allows dynamic assignment of simple or complex tasks, appending sub-tasks to existing complex structures, and real-time status modifications.
![Task Management](docs/task_management.png)

### Productivity Statistics
Automated generation of performance metrics, categorizing task completion statuses across the entire workforce.
![Statistics](docs/statistics.png)
