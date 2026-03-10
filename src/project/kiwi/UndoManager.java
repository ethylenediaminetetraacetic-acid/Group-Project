import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Manages the undo stack for the Operation Scheduler.
 * Supports undoing add, delete, and edit operations on
 * professionals, appointments, and tasks.
 *
 * @author Hda Mohamed
 */
class UndoManager {

    // All possible action types that can be undone
    enum ActionType {
        ADD_PROFESSIONAL, DELETE_PROFESSIONAL, EDIT_PROFESSIONAL,
        ADD_APPOINTMENT,  DELETE_APPOINTMENT,  EDIT_APPOINTMENT,
        ADD_TASK,         DELETE_TASK,         EDIT_TASK
    }

    /**
     * Represents a single reversible action.
     * Stores the state before and after the change so it can be undone.
     */
    static class UndoAction {
        ActionType type;
        Object before;         // state before the operation (null for ADD)
        Object after;          // state after the operation  (null for DELETE)
        String targetId;       // ID of the affected item
        String professionalId; // ID of the professional (for appointment/task actions)

        UndoAction(ActionType type, String targetId, String professionalId,
                   Object before, Object after) {
            this.type = type;
            this.targetId = targetId;
            this.professionalId = professionalId;
            this.before = before;
            this.after = after;
        }
    }

    // LIFO stack — most recent action is always on top
    // ArrayDeque chosen over legacy Stack class: faster, not synchronised unnecessarily
    private final Deque<UndoAction> stack = new ArrayDeque<>();

    /** Push a new action onto the undo stack */
    public void push(UndoAction action) { stack.push(action); }

    /** Pop and return the most recent action, or null if nothing to undo */
    public UndoAction pop() { return stack.isEmpty() ? null : stack.pop(); }

    /** Returns true if there is at least one action to undo */
    public boolean canUndo() { return !stack.isEmpty(); }

    /** Returns a description of the next action that would be undone */
    public String peekDescription() {
        if (stack.isEmpty()) return "";
        return stack.peek().type.toString();
    }
}
