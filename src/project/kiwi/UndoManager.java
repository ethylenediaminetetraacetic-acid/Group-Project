import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Desciption.
 * * @author Banana Monster
 *
 */


class UndoManager {
    enum ActionType { ADD_PROFESSIONAL, DELETE_PROFESSIONAL, EDIT_PROFESSIONAL,
                      ADD_APPOINTMENT, DELETE_APPOINTMENT, EDIT_APPOINTMENT,
                      ADD_TASK, DELETE_TASK, EDIT_TASK }

    static class UndoAction {
        ActionType type;
        Object before;
        Object after;
        String targetId;
        String professionalId;

        UndoAction(ActionType type, String targetId, String professionalId,
                   Object before, Object after) {
            this.type = type;
            this.targetId = targetId;
            this.professionalId = professionalId;
            this.before = before;
            this.after = after;
        }
    }

    private final Deque<UndoAction> stack = new ArrayDeque<>();

    public void push(UndoAction action) { stack.push(action); }
    public UndoAction pop()  { return stack.isEmpty() ? null : stack.pop(); }
    public boolean canUndo() { return !stack.isEmpty(); }
    public String peekDescription() {
        if (stack.isEmpty()) return "";
        return stack.peek().type.toString();
    }
}