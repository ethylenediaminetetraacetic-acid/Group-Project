import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.*;

/**
 * Desciption.
 * * @author Banana Monster
 *
 */

class Appointment implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String treatmentType;
    private String patientName;
    private List<String> coWorkerIds; // IDs of other professionals also booked

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

    public String getId()            { return id; }
    public LocalDate getDate()       { return date; }
    public LocalTime getStartTime()  { return startTime; }
    public LocalTime getEndTime()    { return endTime; }
    public String getTreatmentType() { return treatmentType; }
    public String getPatientName()   { return patientName; }
    public List<String> getCoWorkerIds() { return coWorkerIds; }

    public void setDate(LocalDate d)         { this.date = d; }
    public void setStartTime(LocalTime s)    { this.startTime = s; }
    public void setEndTime(LocalTime e)      { this.endTime = e; }
    public void setTreatmentType(String t)   { this.treatmentType = t; }
    public void setPatientName(String p)     { this.patientName = p; }
    public void setCoWorkerIds(List<String> c) { this.coWorkerIds = c; }

    @Override public String toString() {
        return String.format("[%s] %s %s-%s  %s  Patient: %s",
            date, treatmentType,
            startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
            endTime.format(DateTimeFormatter.ofPattern("HH:mm")),
            treatmentType, patientName);
    }
}