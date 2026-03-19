package ro.utcn.taskManagement.dataaccess;

import ro.utcn.taskManagement.logic.TaskManagement;

import java.io.*;

public class SerializationOperations {

    public static void saveDatabase(TaskManagement tm, String fileName) {
        // Punem declararea fișierelor direct în parantezele de la try.
        // Java le va da automat .close() la final, orice s-ar întâmpla.
        try (FileOutputStream fileOut = new FileOutputStream(fileName);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {

            out.writeObject(tm);
            System.out.println("Datele au fost salvate cu succes in: " + fileName);

        } catch (IOException e) {
            // e.printStackTrace() îți arată exact linia unde a crăpat, nu doar un mesaj scurt
            System.err.println("Eroare critica la salvare:");
            e.printStackTrace();
        }
    }

    public static TaskManagement loadDatabase(String fileName) {
        try (FileInputStream fileIn = new FileInputStream(fileName);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {

            TaskManagement tm = (TaskManagement) in.readObject();
            System.out.println("Date incarcate din: " + fileName);
            return tm;

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Fisier lipsa sau corupt. Initializam sistem gol.");
            return new TaskManagement();
        }
    }
}