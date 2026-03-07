

package project.banana;

import java.time.LocalDateTime;

/**
 * @author Mohamed Hda
 */

public class Appointment {

    String patientName;
    LocalDateTime startTime;
    LocalDateTime endTime;
    String treatmentType;

    public Appointment(String patientName, LocalDateTime startTime, LocalDateTime endTime, String treatmentType) {
        this.patientName = patientName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.treatmentType = treatmentType;

    }

     String getTreatmentType() {
            return treatmentType;
        }

        LocalDateTime getStartTime() {
                return startTime;
            }

        LocalDateTime getEndTime() {
                return endTime;
            }   

        String getPatientName() {
                return patientName;
            }

    public static void main(String[] args) {

        //Basic testing, i'm creating an appointment and printing out the details to check the getters work correctly
        LocalDateTime start = LocalDateTime.of(2024, 6, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 6, 1, 11, 0);
        Appointment appt = new Appointment("John Doe", start, end, "Physical Therapy");
        System.out.println("Patient: " + appt.getPatientName());
        System.out.println("Treatment: " + appt.getTreatmentType());
        System.out.println("Start Time: " + appt.getStartTime());
        System.out.println("End Time: " + appt.getEndTime());

}

}