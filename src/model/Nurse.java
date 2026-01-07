package model;

public class Nurse extends Clinician {
    public Nurse(String clinicianId, String firstName, String lastName, String title,
                 String speciality, String gmcNumber, String phoneNumber, String email,
                 String workplaceId, String workplaceType, String employmentStatus,
                 String startDate) {
        super(clinicianId, firstName, lastName, title, speciality, gmcNumber, phoneNumber, email,
            workplaceId, workplaceType, employmentStatus, startDate);
    }
}
