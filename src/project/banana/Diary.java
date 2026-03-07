package project.banana;


import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Mohamed Hda
 */

// Diary class: manages a health professional's appointments
// Data structure choice: TreeMap<LocalDateTime, LinkedList<Appointment>>
//  TreeMap: keeps appointments sorted by time
//  LinkedList: allows multiple appointments at the same time 

public class Diary {

    // TreeMap: sorted by LocalDateTime key automatically
    // LinkedList per slot: handles multiple bookings at the same time
    private TreeMap<LocalDateTime, LinkedList<Appointment>> entries;

    public Diary() {
        entries = new TreeMap<>();
    }

    // Add an appointment to the diary, allowing multiple appointments at the same time

    public void addAppointment(Appointment appt) {
        // computeIfAbsent: creates a new LinkedList only if this time slot is new
        entries.computeIfAbsent(appt.getStartTime(), k -> new LinkedList<>())
               .add(appt);
    }

    // Remove an appointment by start time. If multiple appointments exist at that time, removes the first one.
    public Appointment removeAppointment(LocalDateTime startTime) {
        LinkedList<Appointment> slot = entries.get(startTime);
        if (slot == null || slot.isEmpty()) return null;

        Appointment removed = slot.removeFirst();

        // Clean up the map entry if the slot is now empty
        if (slot.isEmpty()) entries.remove(startTime);

        return removed;
    }

    // Get all appointments in the diary (for display or export)
    public List<Appointment> getAllAppointments() {
        List<Appointment> all = new ArrayList<>();
        for (LinkedList<Appointment> slot : entries.values()) {
            all.addAll(slot);
        }
        return all;
    }

    // Get appointments within a specific time range (e.g. for daily view)
    public List<Appointment> getAppointmentsInRange(LocalDateTime from, LocalDateTime to) {
        List<Appointment> result = new ArrayList<>();
        // subMap returns only the relevant portion of the tree — very efficient
        NavigableMap<LocalDateTime, LinkedList<Appointment>> range =
                entries.subMap(from, true, to, true);

        for (LinkedList<Appointment> slot : range.values()) {
            result.addAll(slot);
        }
        return result;
    }

    //Checks if the diary is empty (no appointments)
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    // Get the total number of appointments in the diary
    public int size() {
        return entries.values().stream().mapToInt(LinkedList::size).sum();
    }
}
