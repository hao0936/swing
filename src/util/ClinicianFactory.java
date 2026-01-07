package util;

import model.Clinician;
import model.GeneralPractitioner;
import model.Nurse;
import model.SpecialistDoctor;

public final class ClinicianFactory {
    private ClinicianFactory() {
    }

    public static Clinician create(String clinicianId, String firstName, String lastName, String title,
                                   String speciality, String gmcNumber, String phoneNumber, String email,
                                   String workplaceId, String workplaceType, String employmentStatus,
                                   String startDate) {
        String titleLower = title == null ? "" : title.toLowerCase();
        String specialityLower = speciality == null ? "" : speciality.toLowerCase();
        if (titleLower.contains("nurse")) {
            return new Nurse(clinicianId, firstName, lastName, title, speciality, gmcNumber, phoneNumber,
                email, workplaceId, workplaceType, employmentStatus, startDate);
        }
        if ("gp".equalsIgnoreCase(title) || specialityLower.contains("general practice")) {
            return new GeneralPractitioner(clinicianId, firstName, lastName, title, speciality, gmcNumber,
                phoneNumber, email, workplaceId, workplaceType, employmentStatus, startDate);
        }
        return new SpecialistDoctor(clinicianId, firstName, lastName, title, speciality, gmcNumber,
            phoneNumber, email, workplaceId, workplaceType, employmentStatus, startDate);
    }
}
