import java.io.Serializable;
import java.util.UUID;

/**
 * Tasks tab back-end.
 * * @author Huy Ngo
 *
 */


class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    public enum Priority { HIGH, MEDIUM, LOW }
    private String id;
    private String description;
    private Priority priority;
    private boolean done;

    public Task(String description, Priority priority) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.description = description;
        this.priority = priority;
        this.done = false;
    }
    public String getId()          { return id; }
    public String getDescription() { return description; }
    public Priority getPriority()  { return priority; }
    public boolean isDone()        { return done; }
    public void setDescription(String d) { this.description = d; }
    public void setPriority(Priority p)  { this.priority = p; }
    public void setDone(boolean d)       { this.done = d; }
}
