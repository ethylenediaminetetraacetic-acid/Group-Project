

package project.banana;


import java.time.LocalDateTime;

/**
 * @author Mohamed Hda
 */

public class HealthProfessional {

    String name;
    String id;
    String profession;
    Diary diary;
    String workLocation;


    public HealthProfessional(String name, String id, String profession, String workLocation) {
        this.name = name;
        this.id = id;
        this.diary = new Diary(); // Each professional has their own diary
        this.profession = profession;
        this.workLocation = workLocation;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProfession() {
        return profession;
    }

    public String getWorkLocation(){
        return workLocation;
    }

    public Diary getDiary() {
        return diary;
    }

    public static void main(String[] args) {
        
    LocalDateTime start = LocalDateTime.of(2024, 6, 1, 9, 0);
    LocalDateTime end = LocalDateTime.of(2024, 6, 1, 10, 0);
    Appointment appt = new Appointment("John Doe", start, end, "Physical Therapy");

    HealthProfessional drSmith = new HealthProfessional("Dr. Smith", "001", "Surgeon", "City Hospital");
        drSmith.getDiary().addAppointment(appt);
    System.out.println("Name: " + drSmith.getName());
    System.out.println("ID: " + drSmith.getId());
    System.out.println("Profession: " + drSmith.getProfession());
    System.out.println("Work Location: " + drSmith.getWorkLocation());

    }
}
