/**
 * Joseph Guarriello
 * Course: Software Development
 * CRN: CEN3024
 * Project LMS
 * DATE: 09/15/2025
 */


import java.util.Objects;

/**
 * @param id   7-digit unique
 * @param fine 0..250
 */
public record Patron(int id, String name, String address, double fine) {

    @Override
    public String toString() {
        return String.format("%07d | %-20s | %-40s | $%6.2f",
                id, name, address, fine);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patron patron)) return false;
        return id == patron.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
