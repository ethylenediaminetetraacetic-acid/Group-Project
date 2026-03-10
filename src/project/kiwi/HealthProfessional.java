import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a health professional (e.g. doctor, nurse, physiotherapist).
 * Each professional has a unique auto-generated ID and personal details.
 *
 * Implements Serializable so professionals can be saved to and loaded from disk.
 *
 * @author Hda Mohamed
 */
class HealthProfessional implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;         // unique ID auto-generated on creation
    private String name;
    private String profession; // e.g. "Doctor", "Nurse", "Physiotherapist"
    private String location;   // work location / office

    /**
     * Creates a new health professional with a randomly generated ID.
     */
    public HealthProfessional(String name, String profession, String location) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.name = name;
        this.profession = profession;
        this.location = location;
    }

    // --- Getters ---
    public String getId()         { return id; }
    public String getName()       { return name; }
    public String getProfession() { return profession; }
    public String getLocation()   { return location; }

    // --- Setters (used by edit operations) ---
    public void setName(String n)       { this.name = n; }
    public void setProfession(String p) { this.profession = p; }
    public void setLocation(String l)   { this.location = l; }

    @Override
    public String toString() {
        return name + " (" + profession + ")";
    }
}
