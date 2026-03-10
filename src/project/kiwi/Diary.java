import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Desciption.
 * * @author Banana Monster
 *
 */


class Diary implements Serializable {
    private static final long serialVersionUID = 1L;
    // TreeMap keeps dates sorted; LinkedList within each date for fast add/remove
    private TreeMap<LocalDate, LinkedList<Appointment>> entries = new TreeMap<>();

    public void addAppointment(Appointment a) {
        entries.computeIfAbsent(a.getDate(), k -> new LinkedList<>()).add(a);
    }

    public boolean removeAppointment(String appointmentId) {
        for (Map.Entry<LocalDate, LinkedList<Appointment>> e : entries.entrySet()) {
            if (e.getValue().removeIf(a -> a.getId().equals(appointmentId))) {
                if (e.getValue().isEmpty()) entries.remove(e.getKey());
                return true;
            }
        }
        return false;
    }

    public List<Appointment> getAllAppointments() {
        return entries.values().stream().flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<Appointment> getAppointmentsOnDate(LocalDate date) {
        return entries.getOrDefault(date, new LinkedList<>());
    }

    public Optional<Appointment> findById(String id) {
        return getAllAppointments().stream().filter(a -> a.getId().equals(id)).findFirst();
    }
}