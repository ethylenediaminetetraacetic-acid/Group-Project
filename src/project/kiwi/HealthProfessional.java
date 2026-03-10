import java.io.Serializable;
import java.util.UUID;

/**
 * Desciption.
 * * @author Banana Monster
 *
 */


class HealthProfessional implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String profession;
    private String location;

    public HealthProfessional(String name, String profession, String location) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.name = name;
        this.profession = profession;
        this.location = location;
    }

    public String getId()         { return id; }
    public String getName()       { return name; }
    public String getProfession() { return profession; }
    public String getLocation()   { return location; }
    public void setName(String n)       { this.name = n; }
    public void setProfession(String p) { this.profession = p; }
    public void setLocation(String l)   { this.location = l; }

    @Override public String toString() { return name + " (" + profession + ")"; }
}
