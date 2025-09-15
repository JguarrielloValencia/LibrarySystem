/**
 * Joseph Guarriello
 * Course: Software Development
 * CRN: CEN3024
 * Project LMS
 * DATE: 09/15/2025
 */

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner IN = new Scanner(System.in);
    private static final LibrarySystem LMS = new LibrarySystem();

    /** The choices what customer wants when code is ran */

    public static void main(String[] args) {
        while (true) {
            printMenu();
            int choice = readInt();
            switch (choice) {
                case 1 -> addPatronManual();
                case 2 -> importFromFile();
                case 3 -> removePatron();
                case 4 -> listPatrons();
                case 5 -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice. Please select 1-5.");
            }
            System.out.println();
        }
    }
    /** When code is ran this should prompt in window */

    private static void printMenu() {
        System.out.println("==============================================");
        System.out.println(" Library Patron Management System (Console)");
        System.out.println("==============================================");
        System.out.println("1) Add Patron (Manual)");
        System.out.println("2) Import Patrons (From File)");
        System.out.println("3) Remove Patron (By ID)");
        System.out.println("4) Display All Patrons");
        System.out.println("5) Exit");
        System.out.println("==============================================");
    }
    /** Patron adding  */
    private static void addPatronManual() {
        System.out.println("-- Add Patron (Manual) --");

        int id = read7DigitId("Enter 7-digit ID: ");
        String name = readNonEmpty("Enter name: ");
        String address = readNonEmpty("Enter address: ");
        double fine = readDoubleInRange();

        Patron p = new Patron(id, name, address, fine);
        try {
            boolean added = LMS.addPatron(p);
            if (added) {
                System.out.println("Patron added successfully.");
            } else {
                System.out.println("Duplicate ID. Patron NOT added.");
            }
        } catch (IllegalArgumentException ex) {
            System.out.println("Validation error: " + ex.getMessage());
        }
    }

    private static void importFromFile() {
        System.out.println("-- Import Patrons (From File) --");
        System.out.print("Enter file path: ");
        String pathStr = IN.nextLine().trim();
        try {
            var result = LMS.importFromFile(Path.of(pathStr));
            System.out.println(result);
            if (!result.errors.isEmpty()) {
                System.out.println("Errors:");
                result.errors.forEach(err -> System.out.println("  - " + err));
            }
        } catch (Exception e) {
            System.out.println("Failed to import: " + e.getMessage());
        }
    }
    /** patron removed */
    private static void removePatron() {
        System.out.println("-- Remove Patron (By ID) --");
        int id = read7DigitId("Enter 7-digit ID to remove: ");
        boolean removed = LMS.removePatron(id);
        System.out.println(removed ? "Patron removed." : "No patron found with that ID.");
    }
/** lists current patrons */
    private static void listPatrons() {
        System.out.println("-- Current Patrons --");
        List<Patron> list = LMS.getAllPatrons();
        if (list.isEmpty()) {
            System.out.println("(none)");
            return;
        }
        System.out.printf("%-7s | %-20s | %-40s | %s%n", "ID", "Name", "Address", "Fine");
        System.out.println("-------------------------------------------------------------------------------");
        for (Patron p : list) {
            System.out.println(p);
        }
    }


    /** input helpers when selecting wrong integer */


    private static int readInt() {
        while (true) {
            System.out.print("Choose an option (1-5): ");
            String s = IN.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }
/** helper method should keep looping 7 digits only */

    private static int read7DigitId(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = IN.nextLine().trim();
            if (s.matches("\\d{7}")) {
                try {
                    return Integer.parseInt(s);
                } catch (NumberFormatException ignored) { }
            }
            System.out.println("Invalid ID. It must be exactly 7 digits.");
        }
    }
/** Prompts the user to enter a fine amount between 0.0 and 250.0.*/

    private static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = IN.nextLine().trim();
            if (!s.isBlank()) return s;
            System.out.println("Value cannot be blank.");
        }
    }

    private static double readDoubleInRange() {
        while (true) {
            System.out.print("Enter fine amount (0 - 250), no $ sign: ");
            String s = IN.nextLine().trim();
            try {
                double v = Double.parseDouble(s);
                if (v >= 0.0 && v <= 250.0) return v;
                System.out.printf("Value must be between %.2f and %.2f.%n", 0.0, 250.0);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number (no $ sign).");
            }
        }
    }
}
