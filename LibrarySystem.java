/**
 * Joseph Guarriello
 * Course: Software Development
 * CRN: CEN3024
 * Project LMS
 * DATE: 09/15/2025
 */



import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * In-memory LMS for patrons.
 * Uses LinkedHashMap to maintain insertion order for listing.
 */
public class LibrarySystem {

    private final Map<Integer, Patron> patrons = new LinkedHashMap<>();

    /** Adds a patron if valid & unique. Returns true if added. */
    public boolean addPatron(Patron p) {
        validatePatron(p);
        if (patrons.containsKey(p.id())) {
            return false; // duplicate ID
        }
        patrons.put(p.id(), p);
        return true;
    }

    /** Removes by ID. Returns true if removed. */
    public boolean removePatron(int id) {
        return patrons.remove(id) != null;
    }

    /** Returns all patrons (in insertion order). */
    public List<Patron> getAllPatrons() {
        return new ArrayList<>(patrons.values());
    }

    /** Basic validation per requirements. */
    private void validatePatron(Patron p) throws IllegalArgumentException {
        if (p.id() < 1_000_000 || p.id() > 9_999_999)
            throw new IllegalArgumentException("ID must be exactly 7 digits.");
        if (p.name() == null || p.name().isBlank())
            throw new IllegalArgumentException("Name cannot be blank.");
        if (p.address() == null || p.address().isBlank())
            throw new IllegalArgumentException("Address cannot be blank.");
        if (p.fine() < 0.0 || p.fine() > 250.0)
            throw new IllegalArgumentException("Fine must be between 0 and 250.");
    }

    /** Parses a line in the form ID-Name-Address-Fine. Returns Patron. */
    public static Patron parsePatronLine(String line) {
        // Split into 4 parts only; address may contain dashes after the first 2 fields,
        // so we split with a limit and then recombine the middle if needed.
        String[] parts = line.split("-", 4);
        if (parts.length != 4) {
            throw new IllegalArgumentException("Line does not have 4 fields separated by dashes.");
        }
        String idStr = parts[0].trim();
        String name = parts[1].trim();
        String address = parts[2].trim();
        String fineStr = parts[3].trim();

        if (!idStr.matches("\\d{7}")) {
            throw new IllegalArgumentException("ID must be exactly 7 digits: " + idStr);
        }
        int id = Integer.parseInt(idStr);

        double fine;
        try {
            fine = Double.parseDouble(fineStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Fine is not a valid number: " + fineStr);
        }

        return new Patron(id, name, address, fine);
    }

    /** ImportResult captures stats from a file import. */
    public static class ImportResult {
        public int totalLines;
        public int added;
        public int duplicates;
        public int invalid;
        public final List<String> errors = new ArrayList<>();

        @Override
        public String toString() {
            return String.format(
                    "Import summary: lines=%d, added=%d, duplicates=%d, invalid=%d",
                    totalLines, added, duplicates, invalid
            );
        }
    }

    /** Imports patrons from a file (UTF-8). Returns summary result. */
    public ImportResult importFromFile(Path path) throws IOException {
        ImportResult res = new ImportResult();
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            int lineNo = 0;
            while ((line = br.readLine()) != null) {
                lineNo++;
                String raw = line.strip();
                if (raw.isEmpty()) continue;

                res.totalLines++;
                try {
                    Patron p = parsePatronLine(raw);
                    boolean added = addPatron(p);
                    if (added) {
                        res.added++;
                    } else {
                        res.duplicates++;
                    }
                } catch (IllegalArgumentException ex) {
                    res.invalid++;
                    res.errors.add("Line " + lineNo + ": " + ex.getMessage());
                }
            }
        }
        return res;
    }
}
