
package ro.utcn.taskManagement; // Ajustează pachetul dacă e nevoie

import ro.utcn.taskManagement.dataaccess.SerializationOperations;
import ro.utcn.taskManagement.logic.TaskManagement;
import ro.utcn.taskManagement.logic.Utility;
import ro.utcn.taskManagement.model.ComplexTask;
import ro.utcn.taskManagement.model.Employee;
import ro.utcn.taskManagement.model.SimpleTask;

import java.util.Map;
// import ro.utcn.taskManagement.model.Task; // Dacă e nevoie

public class Main {
    public static void main(String[] args) {
        // 1. Inițializăm "creierul" aplicației
        TaskManagement tm = new TaskManagement();

        // 2. Creăm angajații și îi adăugăm în sistem
        Employee emp1 = new Employee(1, "Alex");
        Employee emp2 = new Employee(2, "Maria");
        tm.addEmployee(emp1);
        tm.addEmployee(emp2);

        // 3. Creăm task-uri (simple și complexe)
        // Task simplu care NU e gata (durată: 3 ore)
        SimpleTask st1 = new SimpleTask("In Progress", 101, 9, 12);

        // Task simplu gata (durată: 2 ore)
        SimpleTask st2 = new SimpleTask("Completed", 102, 14, 16);

        // Task simplu peste noapte, gata (durată: 22:00 -> 02:00 = 4 ore)
        SimpleTask st3 = new SimpleTask("Completed", 103, 22, 2);

        // Task complex format din 2 sub-task-uri, ambele gata (durată totală: 1 + 2 = 3 ore)
        ComplexTask ct1 = new ComplexTask(201, "Completed");
        ct1.addTask(new SimpleTask("Completed", 202, 10, 11));
        ct1.addTask(new SimpleTask("Completed", 203, 12, 14));

        // 4. Atribuim task-urile
        tm.assignTaskToEmployee(1, st1); // Alex primește st1
        tm.assignTaskToEmployee(1, st2); // Alex primește st2
        tm.assignTaskToEmployee(1, ct1); // Alex primește ct1

        tm.assignTaskToEmployee(2, st3); // Maria primește st3

        // 5. RULĂM TESTELE (Aici facem debugging)
        System.out.println("=== TEST 1: Calcul durată inițială ===");
        // Alex are: st1(In Progress, 3h) + st2(Completed, 2h) + ct1(Completed, 3h).
        // Așteptăm: 2 + 3 = 5 ore (st1 trebuie ignorat).
        System.out.println("Durata lui Alex (Așteptat: 5): " + tm.calculateEmployeeWorkDuration(1));

        // Maria are: st3(Completed, 4h).
        // Așteptăm: 4 ore.
        System.out.println("Durata Mariei (Așteptat: 4): " + tm.calculateEmployeeWorkDuration(2));

        System.out.println("\n=== TEST 2: Modificare status ===");
        // Trecem task-ul st1 al lui Alex din "In Progress" în "Completed"
        tm.modifyTaskStatus(1, 101, "Completed");

        // Acum st1 (3 ore) ar trebui să se adune la total
        // Așteptăm: 5 (vechi) + 3 (nou) = 8 ore.
        System.out.println("Noua durată a lui Alex (Așteptat: 8): " + tm.calculateEmployeeWorkDuration(1));

        System.out.println("\n=== TEST 3: Verificare structură Map ===");
        // Printăm tot conținutul să vedem dacă to string-urile tale merg bine
        tm.getTaskMap().forEach((angajat, listaTaskuri) -> {
            System.out.println(angajat.toString());
            for (int i = 0; i < listaTaskuri.size(); i++) {
                System.out.println("  -> " + listaTaskuri.get(i).toString());
            }
        });
        System.out.println("\n=== PREGATIRE DATE PENTRU TESTELE UTILITY ===");
        // Creăm "sclavi pe plantație" ca să depășim 40 de ore
        Employee emp3 = new Employee(3, "Ion");
        Employee emp4 = new Employee(4, "Ana");
        tm.addEmployee(emp3);
        tm.addEmployee(emp4);

        // Ion va avea 50 de ore în total (îi dăm 2 task-uri mari)
        tm.assignTaskToEmployee(3, new SimpleTask("Completed", 301, 0, 25));
        tm.assignTaskToEmployee(3, new SimpleTask("Completed", 302, 0, 25));

        // Ana va avea 45 de ore în total
        tm.assignTaskToEmployee(4, new SimpleTask("Completed", 401, 0, 20));
        tm.assignTaskToEmployee(4, new SimpleTask("Completed", 402, 0, 25));

        // Îi dăm Mariei un task neterminat ca să vedem dacă statisticile detectează asta
        tm.assignTaskToEmployee(2, new SimpleTask("In Progress", 104, 10, 15));


        // 6. RULĂM TESTELE PENTRU CLASA UTILITY
        System.out.println("\n=== TEST 4: Utility - Filtrare angajati > 40 ore ===");
        // Așteptăm: Ana (45 ore) să apară PRIMA, urmată de Ion (50 ore) - sortare crescătoare.
        // Alex (8h) și Maria (4h) trebuie să fie ignorați complet.
        Utility.filtersEmployee(tm);

        System.out.println("\n=== TEST 5: Utility - Statistici Task-uri ===");
        // Așteptăm ca Maria să aibă 1 Completed și 1 Uncompleted. Alex să aibă 3 Completed și 0 Uncompleted.
        Map<String, Map<String, Integer>> stats = Utility.computesTasks(tm);

        // Afișăm manual rezultatul Map-ului tău complex
        for (Map.Entry<String, Map<String, Integer>> intrare : stats.entrySet()) {
            String numeAngajat = intrare.getKey();
            Map<String, Integer> detalii = intrare.getValue();

            System.out.println("Angajat: " + numeAngajat);
            System.out.println("  -> Completed: " + detalii.get("Completed"));
            System.out.println("  -> Uncompleted: " + detalii.get("Uncompleted"));
        }

        // 7. RULĂM TESTUL PENTRU SERIALIZARE (I/O)
        System.out.println("\n=== TEST 6: Serializare (Salvare pe disc) ===");
        // Salvăm întregul obiect tm în fișier
        SerializationOperations.saveDatabase(tm, "baza_date_test.ser");

        System.out.println("\n=== TEST 7: Deserializare (Incarcare de pe disc) ===");
        // Creăm o variabilă COMPLET NOUĂ și o umplem cu ce citim din fișier
        TaskManagement tmIncarcatDinFisier = SerializationOperations.loadDatabase("baza_date_test.ser");

        // Verificăm dacă obiectul nou conține angajații și task-urile salvate
        System.out.println("Verificam datele din sistemul recuperat:");
        if (tmIncarcatDinFisier != null && tmIncarcatDinFisier.getTaskMap() != null) {
            tmIncarcatDinFisier.getTaskMap().forEach((angajat, listaTaskuri) -> {
                System.out.println("Recuperat cu succes: " + angajat.getName() + " -> are " + listaTaskuri.size() + " task-uri.");
            });
        } else {
            System.out.println("EROARE: Sistemul incarcat este gol sau corupt!");
        }
    }
}