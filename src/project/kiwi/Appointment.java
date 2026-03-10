import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a single patient appointment in a health professional's diary.
 * Stores the date, start/end times, treatment type, patient name,
 * and any co-workers booked for the same appointment.
 *
 * Implements Serializable so appointments can be saved to and loaded from disk.
 *
 * @author Hda Mohamed
 */
class Appointment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;             // unique ID auto-generated on creation
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String treatmentType;  // e.g. "Operation", "Consultation", "Blood test"
    private String patientName;
    private List<String> coWorkerIds; // IDs of other professionals booked for this appointment

    /**
     * Creates a new appointment with a randomly generated ID.
     */
    public Appointment(LocalDate date, LocalTime start, LocalTime end,
                       String treatmentType, String patientName) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.date = date;
        this.startTime = start;
        this.endTime = end;
        this.treatmentType = treatmentType;
        this.patientName = patientName;
        this.coWorkerIds = new ArrayList<>();
    }

    // --- Getters ---
    public String getId()                { return id; }
    public LocalDate getDate()           { return date; }
    public LocalTime getStartTime()      { return startTime; }
    public LocalTime getEndTime()        { return endTime; }
    public String getTreatmentType()     { return treatmentType; }
    public String getPatientName()       { return patientName; }
    public List<String> getCoWorkerIds() { return coWorkerIds; }

    // --- Setters (used by edit operations) ---
    public void setDate(LocalDate d)           { this.date = d; }
    public void setStartTime(LocalTime s)      { this.startTime = s; }
    public void setEndTime(LocalTime e)        { this.endTime = e; }
    public void setTreatmentType(String t)     { this.treatmentType = t; }
    public void setPatientName(String p)       { this.patientName = p; }
    public void setCoWorkerIds(List<String> c) { this.coWorkerIds = c; }

    @Override
    public String toString() {
        return String.format("[%s] %s %s-%s  Patient: %s",
            date, treatmentType,
            startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
            endTime.format(DateTimeFormatter.ofPattern("HH:mm")),
            patientName);
    }
}
