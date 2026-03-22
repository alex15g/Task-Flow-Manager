package ro.utcn.taskManagement.dataaccess;

import ro.utcn.taskManagement.logic.TaskManagement;

import java.io.*;

//Provides static methods for saving and loading the TaskManagement application state.
//This class manages the serialization and deserialization of the main data model

public class SerializationOperations {
    // Serializes the given TaskManagement object and saves it to a specified file.
    // The TaskManagement class must implement the Serializable interface.
    // tm->The TaskManagement instance containing the state to be saved.
    // fileName->The path and name of the destination file.
    public static void saveDatabase(TaskManagement tm, String fileName) {
        // The declaration of the files->try()
        try (FileOutputStream fileOut = new FileOutputStream(fileName);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {

            out.writeObject(tm);
            System.out.println("The data was saved in: " + fileName);

        } catch (IOException e) {
            // e.printStackTrace() shows where the program failed
            System.err.println("Critical error while trying to save the data:");
            e.printStackTrace();
        }
    }

    //Deserializes a TaskManagement object from the specified file.
    //fileName->The path and name of the file to read from.
    //return->The loaded TaskManagement object, or a new empty instance if loading fails.
    public static TaskManagement loadDatabase(String fileName) {
        try (FileInputStream fileIn = new FileInputStream(fileName);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {

            TaskManagement tm = (TaskManagement) in.readObject();
            System.out.println("Data loaded successfully from: " + fileName);
            return tm;

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Missing or corrupt file (" +  fileName +"). Initialize a new empty database.");
            return new TaskManagement();
        }
    }
}