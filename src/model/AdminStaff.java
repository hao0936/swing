package model;

public class AdminStaff extends Staff {
    public AdminStaff(String staffId, String firstName, String lastName, String role, String department,
                      String facilityId, String phoneNumber, String email, String employmentStatus,
                      String startDate, String lineManager, String accessLevel) {
        super(staffId, firstName, lastName, role, department, facilityId, phoneNumber, email,
            employmentStatus, startDate, lineManager, accessLevel);
    }
}
