package service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import model.Bug;

public class BugService {

    private ArrayList<Bug> bugList = new ArrayList<>();

    // ===== GET ALL BUGS =====
    public ArrayList<Bug> getAllBugs() {
        return bugList;
    }

    // ===== CREATE BUG =====
    public void createBug(Bug bug) {
        bugList.add(bug);
    }

    // ===== DELETE BUG =====
    public boolean deleteBug(int id) {
        return bugList.removeIf(bug -> bug.id == id);
    }

    // ===== CHANGE STATUS =====
    public boolean changeStatus(int id, String status) {
        for (Bug bug : bugList) {
            if (bug.id == id) {
                bug.status = status;
                return true;
            }
        }
        return false;
    }

    // ===== ASSIGN BUG =====
    public boolean assignBug(int id, String devName) {
        for (Bug bug : bugList) {
            if (bug.id == id) {
                bug.assignedTo = devName;
                bug.status = "In Progress";
                return true;
            }
        }
        return false;
    }

    // ===== SEARCH BUG =====
    public Bug searchBug(String input) {
        for (Bug bug : bugList) {
            if (String.valueOf(bug.id).equals(input)
                    || bug.title.equalsIgnoreCase(input)) {
                return bug;
            }
        }
        return null;
    }

    // ===== COUNT BY PRIORITY (For Bar Chart) =====
    public long countByPriority(String priority) {
        return bugList.stream()
                .filter(b -> b.priority.equals(priority))
                .count();
    }

    // ===== COUNT BY STATUS (For Pie Chart) =====
    public long countByStatus(String status) {
        return bugList.stream()
                .filter(b -> b.status.equals(status))
                .count();
    }

    // ===== SAVE TO FILE =====
    public void saveToFile() {
        try {
            FileWriter writer = new FileWriter("bugs.txt");

            for (Bug bug : bugList) {
                writer.write(
                        bug.id + "," +
                        bug.title + "," +
                        bug.description + "," +
                        bug.priority + "," +
                        bug.status + "," +
                        (bug.assignedTo == null ? "" : bug.assignedTo)
                        + "\n"
                );
            }

            writer.close();

        } catch (IOException e) {
            System.out.println("Error saving data.");
        }
    }

    // ===== LOAD FROM FILE =====
    public void loadFromFile() {
        try {
            File file = new File("bugs.txt");

            if (!file.exists()) return;

            bugList.clear(); // IMPORTANT: prevent duplicate loading

            Scanner fileScanner = new Scanner(file);

            while (fileScanner.hasNextLine()) {

                String[] data = fileScanner.nextLine().split(",");

                if (data.length < 5) continue; // safety check

                Bug bug = new Bug(
                        Integer.parseInt(data[0]),
                        data[1],
                        data[2],
                        data[3]
                );

                bug.status = data[4];

                if (data.length >= 6) {
                    bug.assignedTo = data[5];
                }

                bugList.add(bug);
            }

            fileScanner.close();

        } catch (Exception e) {
            System.out.println("Error loading data.");
        }
    }
}