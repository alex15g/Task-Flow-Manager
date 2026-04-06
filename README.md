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
<img width="1562" height="1177" alt="ss_task_flow_manager_1" src="https://github.com/user-attachments/assets/5f3782af-3a19-4dd6-afd5-d371694c2197" />


### Task Assignment & Status Updates
The system allows dynamic assignment of simple or complex tasks, appending sub-tasks to existing complex structures, and real-time status modifications.
<img width="1568" height="509" alt="ss_task_flow_manager_2" src="https://github.com/user-attachments/assets/66930666-b60f-47aa-92a6-184288394530" />


### Productivity Statistics
Automated generation of performance metrics, categorizing task completion statuses across the entire workforce.
<img width="1564" height="1172" alt="ss_task_flow_manager_3" src="https://github.com/user-attachments/assets/cae3cb99-b37a-4a6b-bc34-29af92026bae" />
