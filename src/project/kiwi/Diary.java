import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Electronic diary for a single health professional.
 *
 * Data structure: TreeMap<LocalDate, LinkedList<Appointment>>
 *   - TreeMap key = appointment date, so entries are always sorted chronologically.
 *     No manual sorting needed — display and range queries are automatic.
 *   - LinkedList per date = multiple appointments can exist on the same day,
 *     and multiple professionals can share the same time slot (e.g. surgeon + anaesthetist).
 *
 *
 * Implements Serializable so diaries can be saved to and loaded from disk.
 *
 * @author Hda Mohamed
 */
class Diary implements Serializable {
    private static final long serialVersionUID = 1L;

    // TreeMap keeps dates sorted; LinkedList within each date for fast add/remove
    private TreeMap<LocalDate, LinkedList<Appointment>> entries = new TreeMap<>();

    /**
     * Add an appointment. If another appointment already exists on the same date,
     * it is added to that date's list (supports multiple bookings per day).
     */
    public void addAppointment(Appointment a) {
        // computeIfAbsent: creates a new LinkedList only if this date is new
        entries.computeIfAbsent(a.getDate(), k -> new LinkedList<>()).add(a);
    }

    /**
     * Remove an appointment by its unique ID.
     * Returns true if found and removed, false if not found.
     */
    public boolean removeAppointment(String appointmentId) {
        for (Map.Entry<LocalDate, LinkedList<Appointment>> e : entries.entrySet()) {
            if (e.getValue().removeIf(a -> a.getId().equals(appointmentId))) {
                // Clean up the map entry if the date's list is now empty
                if (e.getValue().isEmpty()) entries.remove(e.getKey());
                return true;
            }
        }
        return false;
    }

    /**
     * Get all appointments in chronological order.
     * TreeMap guarantees date-sorted iteration — no sorting needed.
     */
    public List<Appointment> getAllAppointments() {
        return entries.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Get all appointments on a specific date.
     * Returns an empty list if no appointments exist on that date.
     */
    public List<Appointment> getAppointmentsOnDate(LocalDate date) {
        return entries.getOrDefault(date, new LinkedList<>());
    }

    /**
     * Find a specific appointment by its ID.
     * Returns an Optional — empty if not found.
     */
    public Optional<Appointment> findById(String id) {
        return getAllAppointments().stream()
                .filter(a -> a.getId().equals(id))
                .findFirst();
    }
}
