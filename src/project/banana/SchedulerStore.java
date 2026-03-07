package project.banana;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Mohamed Hda
 */

/// Central data store for the Operation Scheduler.  
public class SchedulerStore {

    // --- Health Professional Store ---

    private TreeMap<String, HealthProfessional> professionals;

    // --- Undo Stack ---
    private Deque<UndoAction> undoStack;

    public SchedulerStore() {
        professionals = new TreeMap<>();
        undoStack = new ArrayDeque<>();
    }


    // =========================================================
    //  HEALTH PROFESSIONAL CRUD
    // =========================================================

    public void addProfessional(HealthProfessional hp) {
        professionals.put(hp.getId(), hp);
    }

    public boolean deleteProfessional(String id) {
        return professionals.remove(id) != null;
    }

    public HealthProfessional getProfessional(String id) {
        return professionals.get(id);
    }

    // Returns all professionals in sorted ID order (TreeMap guarantees this)
    public Collection<HealthProfessional> getAllProfessionals() {
        return professionals.values();
    }


    // =========================================================
    //  DIARY / APPOINTMENT OPERATIONS  (with undo support)
    // =========================================================


    /// Add an appointment to a professional's diary.
    /// Pushes a reversible action onto the undo stack before making the change.
    public boolean addAppointment(String professionalId, Appointment appt) {
        HealthProfessional hp = professionals.get(professionalId);
        if (hp == null) return false;

        hp.getDiary().addAppointment(appt);

        // Record for undo
        undoStack.push(new UndoAction(UndoAction.Type.ADD, professionalId, null, appt));
        return true;
    }

    /// Delete an appointment by professional ID and start time.
    /// Saves the deleted appointment so undo can restore it.
    public boolean deleteAppointment(String professionalId, LocalDateTime startTime) {
        HealthProfessional hp = professionals.get(professionalId);
        if (hp == null) return false;

        Appointment removed = hp.getDiary().removeAppointment(startTime);
        if (removed == null) return false;

        // Record for undo
        undoStack.push(new UndoAction(UndoAction.Type.DELETE, professionalId, removed, null));
        return true;
    }

    /// Edit an appointment by professional ID and start time.
    public boolean editAppointment(String professionalId, LocalDateTime startTime, Appointment updated) {
        HealthProfessional hp = professionals.get(professionalId);
        if (hp == null) return false;

        Appointment before = hp.getDiary().removeAppointment(startTime);
        if (before == null) return false;

        hp.getDiary().addAppointment(updated);

        // Record for undo
        undoStack.push(new UndoAction(UndoAction.Type.EDIT, professionalId, before, updated));
        return true;
    }

    
    /// Undo the last appointment operation (add, delete, or edit).
    public boolean undo() {
        if (undoStack.isEmpty()) {
            System.out.println("Nothing to undo.");
            return false;
        }

        UndoAction action = undoStack.pop();
        HealthProfessional hp = professionals.get(action.professionalId);
        if (hp == null) return false;

        switch (action.type) {
            case ADD:
                // Undo an add = remove what was added
                hp.getDiary().removeAppointment(action.after.getStartTime());
                break;
            case DELETE:
                // Undo a delete = re-insert what was removed
                hp.getDiary().addAppointment(action.before);
                break;
            case EDIT:
                // Undo an edit = remove updated version, restore original
                hp.getDiary().removeAppointment(action.after.getStartTime());
                hp.getDiary().addAppointment(action.before);
                break;
        }
        return true;
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }


    // =========================================================
    //  INNER CLASS: UndoAction
    //  Stores everything needed to reverse a single operation.
    // =========================================================

    private static class UndoAction {
        enum Type { ADD, DELETE, EDIT }

        final Type type;
        final String professionalId;
        final Appointment before; // state before the operation (null for ADD)
        final Appointment after;  // state after the operation  (null for DELETE)

        UndoAction(Type type, String professionalId, Appointment before, Appointment after) {
            this.type = type;
            this.professionalId = professionalId;
            this.before = before;
            this.after = after;
        }
    }

    public static void main(String[] args) {
        
        // TESTING: Basic add and undo operations

        SchedulerStore store = new SchedulerStore();

    store.addProfessional(new HealthProfessional("Dr. Smith", "001", "Surgeon", "City Hospital"));

    // Add an appointment and then undo it
    LocalDateTime start = LocalDateTime.of(2026, 3, 10, 9, 0);
    LocalDateTime end   = LocalDateTime.of(2026, 3, 10, 10, 0);
    store.addAppointment("001", new Appointment("John Doe", start, end, "Operation"));
    // Check that the appointment was added
    System.out.println(store.getProfessional("001").getDiary().getAllAppointments().size()); // 1
    store.undo();
    // Check that the appointment was removed
    System.out.println(store.getProfessional("001").getDiary().getAllAppointments().size()); // 0

    }
}
