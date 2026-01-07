package data;

import model.AdminStaff;
import model.Appointment;
import model.Clinician;
import model.Facility;
import model.GeneralPractitioner;
import model.Nurse;
import model.Patient;
import model.Prescription;
import model.Referral;
import model.SpecialistDoctor;
import model.Staff;
import util.CsvUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataStore {
    private final List<Patient> patients = new ArrayList<>();
    private final List<Clinician> clinicians = new ArrayList<>();
    private final List<Facility> facilities = new ArrayList<>();
    private final List<Appointment> appointments = new ArrayList<>();
    private final List<Prescription> prescriptions = new ArrayList<>();
    private final List<Referral> referrals = new ArrayList<>();
    private final List<Staff> staff = new ArrayList<>();

    public void loadAll(Path root) throws IOException {
        loadPatients(root.resolve("patients.csv"));
        loadClinicians(root.resolve("clinicians.csv"));
        loadFacilities(root.resolve("facilities.csv"));
        loadAppointments(root.resolve("appointments.csv"));
        loadPrescriptions(root.resolve("prescriptions.csv"));
        loadReferrals(root.resolve("referrals.csv"));
        loadStaff(root.resolve("staff.csv"));
        linkHealthRecords();
    }

    public void saveAll(Path root) throws IOException {
        savePatients(root.resolve("patients.csv"));
        saveClinicians(root.resolve("clinicians.csv"));
        saveFacilities(root.resolve("facilities.csv"));
        saveAppointments(root.resolve("appointments.csv"));
        savePrescriptions(root.resolve("prescriptions.csv"));
        saveReferrals(root.resolve("referrals.csv"));
        saveStaff(root.resolve("staff.csv"));
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public List<Clinician> getClinicians() {
        return clinicians;
    }

    public List<Facility> getFacilities() {
        return facilities;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public List<Prescription> getPrescriptions() {
        return prescriptions;
    }

    public List<Referral> getReferrals() {
        return referrals;
    }

    public List<Staff> getStaff() {
        return staff;
    }

    public Patient findPatient(String patientId) {
        for (Patient patient : patients) {
            if (patient.getPatientId().equalsIgnoreCase(patientId)) {
                return patient;
            }
        }
        return null;
    }

    public Clinician findClinician(String clinicianId) {
        for (Clinician clinician : clinicians) {
            if (clinician.getClinicianId().equalsIgnoreCase(clinicianId)) {
                return clinician;
            }
        }
        return null;
    }

    public Facility findFacility(String facilityId) {
        for (Facility facility : facilities) {
            if (facility.getFacilityId().equalsIgnoreCase(facilityId)) {
                return facility;
            }
        }
        return null;
    }

    private void linkHealthRecords() {
        for (Prescription prescription : prescriptions) {
            Patient patient = findPatient(prescription.getPatientId());
            if (patient != null) {
                patient.getHealthRecord().addPrescription(prescription);
            }
        }
        for (Referral referral : referrals) {
            Patient patient = findPatient(referral.getPatientId());
            if (patient != null) {
                patient.getHealthRecord().addReferral(referral);
            }
        }
    }

    private void loadPatients(Path path) throws IOException {
        patients.clear();
        List<String[]> rows = CsvUtil.read(path);
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            Patient patient = new Patient(
                get(row, 0),
                get(row, 1),
                get(row, 2),
                get(row, 3),
                get(row, 4),
                get(row, 5),
                get(row, 6),
                get(row, 7),
                get(row, 8),
                get(row, 9),
                get(row, 10),
                get(row, 11),
                get(row, 12),
                get(row, 13)
            );
            patients.add(patient);
        }
    }

    private void loadClinicians(Path path) throws IOException {
        clinicians.clear();
        List<String[]> rows = CsvUtil.read(path);
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            String title = get(row, 3);
            String speciality = get(row, 4);
            String clinicianId = get(row, 0);
            String firstName = get(row, 1);
            String lastName = get(row, 2);
            String gmcNumber = get(row, 5);
            String phone = get(row, 6);
            String email = get(row, 7);
            String workplaceId = get(row, 8);
            String workplaceType = get(row, 9);
            String employmentStatus = get(row, 10);
            String startDate = get(row, 11);

            Clinician clinician;
            String titleLower = title.toLowerCase();
            if (titleLower.contains("nurse")) {
                clinician = new Nurse(clinicianId, firstName, lastName, title, speciality, gmcNumber,
                    phone, email, workplaceId, workplaceType, employmentStatus, startDate);
            } else if ("gp".equalsIgnoreCase(title) || speciality.toLowerCase().contains("general practice")) {
                clinician = new GeneralPractitioner(clinicianId, firstName, lastName, title, speciality,
                    gmcNumber, phone, email, workplaceId, workplaceType, employmentStatus, startDate);
            } else {
                clinician = new SpecialistDoctor(clinicianId, firstName, lastName, title, speciality,
                    gmcNumber, phone, email, workplaceId, workplaceType, employmentStatus, startDate);
            }
            clinicians.add(clinician);
        }
    }

    private void loadFacilities(Path path) throws IOException {
        facilities.clear();
        List<String[]> rows = CsvUtil.read(path);
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            List<String> specialities = splitList(get(row, 10));
            Facility facility = new Facility(
                get(row, 0),
                get(row, 1),
                get(row, 2),
                get(row, 3),
                get(row, 4),
                get(row, 5),
                get(row, 6),
                get(row, 7),
                get(row, 8),
                get(row, 9),
                specialities
            );
            facilities.add(facility);
        }
    }

    private void loadAppointments(Path path) throws IOException {
        appointments.clear();
        List<String[]> rows = CsvUtil.read(path);
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            Appointment appointment = new Appointment(
                get(row, 0),
                get(row, 1),
                get(row, 2),
                get(row, 3),
                get(row, 4),
                get(row, 5),
                get(row, 6),
                get(row, 7),
                get(row, 8),
                get(row, 9),
                get(row, 10),
                get(row, 11),
                get(row, 12)
            );
            appointments.add(appointment);
        }
    }

    private void loadPrescriptions(Path path) throws IOException {
        prescriptions.clear();
        List<String[]> rows = CsvUtil.read(path);
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            Prescription prescription = new Prescription(
                get(row, 0),
                get(row, 1),
                get(row, 2),
                get(row, 3),
                get(row, 4),
                get(row, 5),
                get(row, 6),
                get(row, 7),
                get(row, 8),
                get(row, 9),
                get(row, 10),
                get(row, 11),
                get(row, 12),
                get(row, 13),
                get(row, 14)
            );
            prescriptions.add(prescription);
        }
    }

    private void loadReferrals(Path path) throws IOException {
        referrals.clear();
        List<String[]> rows = CsvUtil.read(path);
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            List<String> investigations = splitList(get(row, 10));
            Referral referral = new Referral(
                get(row, 0),
                get(row, 1),
                get(row, 2),
                get(row, 3),
                get(row, 4),
                get(row, 5),
                get(row, 6),
                get(row, 7),
                get(row, 8),
                get(row, 9),
                investigations,
                get(row, 11),
                get(row, 12),
                get(row, 13),
                get(row, 14),
                get(row, 15)
            );
            referrals.add(referral);
        }
    }

    private void loadStaff(Path path) throws IOException {
        staff.clear();
        List<String[]> rows = CsvUtil.read(path);
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            Staff member = new AdminStaff(
                get(row, 0),
                get(row, 1),
                get(row, 2),
                get(row, 3),
                get(row, 4),
                get(row, 5),
                get(row, 6),
                get(row, 7),
                get(row, 8),
                get(row, 9),
                get(row, 10),
                get(row, 11)
            );
            staff.add(member);
        }
    }

    private void savePatients(Path path) throws IOException {
        String[] header = new String[]{
            "patient_id", "first_name", "last_name", "date_of_birth", "nhs_number", "gender",
            "phone_number", "email", "address", "postcode", "emergency_contact_name",
            "emergency_contact_phone", "registration_date", "gp_surgery_id"
        };
        List<String[]> rows = new ArrayList<>();
        for (Patient patient : patients) {
            rows.add(new String[]{
                patient.getPatientId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getDateOfBirth(),
                patient.getNhsNumber(),
                patient.getGender(),
                patient.getPhoneNumber(),
                patient.getEmail(),
                patient.getAddress(),
                patient.getPostcode(),
                patient.getEmergencyContactName(),
                patient.getEmergencyContactPhone(),
                patient.getRegistrationDate(),
                patient.getGpSurgeryId()
            });
        }
        CsvUtil.write(path, header, rows);
    }

    private void saveClinicians(Path path) throws IOException {
        String[] header = new String[]{
            "clinician_id", "first_name", "last_name", "title", "speciality", "gmc_number",
            "phone_number", "email", "workplace_id", "workplace_type", "employment_status", "start_date"
        };
        List<String[]> rows = new ArrayList<>();
        for (Clinician clinician : clinicians) {
            rows.add(new String[]{
                clinician.getClinicianId(),
                clinician.getFirstName(),
                clinician.getLastName(),
                clinician.getTitle(),
                clinician.getSpeciality(),
                clinician.getGmcNumber(),
                clinician.getPhoneNumber(),
                clinician.getEmail(),
                clinician.getWorkplaceId(),
                clinician.getWorkplaceType(),
                clinician.getEmploymentStatus(),
                clinician.getStartDate()
            });
        }
        CsvUtil.write(path, header, rows);
    }

    private void saveFacilities(Path path) throws IOException {
        String[] header = new String[]{
            "facility_id", "facility_name", "facility_type", "address", "postcode", "phone_number",
            "email", "opening_hours", "manager_name", "capacity", "specialities_offered"
        };
        List<String[]> rows = new ArrayList<>();
        for (Facility facility : facilities) {
            rows.add(new String[]{
                facility.getFacilityId(),
                facility.getFacilityName(),
                facility.getFacilityType(),
                facility.getAddress(),
                facility.getPostcode(),
                facility.getPhoneNumber(),
                facility.getEmail(),
                facility.getOpeningHours(),
                facility.getManagerName(),
                facility.getCapacity(),
                facility.getSpecialitiesAsString()
            });
        }
        CsvUtil.write(path, header, rows);
    }

    private void saveAppointments(Path path) throws IOException {
        String[] header = new String[]{
            "appointment_id", "patient_id", "clinician_id", "facility_id", "appointment_date",
            "appointment_time", "duration_minutes", "appointment_type", "status", "reason_for_visit",
            "notes", "created_date", "last_modified"
        };
        List<String[]> rows = new ArrayList<>();
        for (Appointment appointment : appointments) {
            rows.add(new String[]{
                appointment.getAppointmentId(),
                appointment.getPatientId(),
                appointment.getClinicianId(),
                appointment.getFacilityId(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getDurationMinutes(),
                appointment.getAppointmentType(),
                appointment.getStatus(),
                appointment.getReasonForVisit(),
                appointment.getNotes(),
                appointment.getCreatedDate(),
                appointment.getLastModified()
            });
        }
        CsvUtil.write(path, header, rows);
    }

    private void savePrescriptions(Path path) throws IOException {
        String[] header = new String[]{
            "prescription_id", "patient_id", "clinician_id", "appointment_id", "prescription_date",
            "medication_name", "dosage", "frequency", "duration_days", "quantity", "instructions",
            "pharmacy_name", "status", "issue_date", "collection_date"
        };
        List<String[]> rows = new ArrayList<>();
        for (Prescription prescription : prescriptions) {
            rows.add(new String[]{
                prescription.getPrescriptionId(),
                prescription.getPatientId(),
                prescription.getClinicianId(),
                prescription.getAppointmentId(),
                prescription.getPrescriptionDate(),
                prescription.getMedicationName(),
                prescription.getDosage(),
                prescription.getFrequency(),
                prescription.getDurationDays(),
                prescription.getQuantity(),
                prescription.getInstructions(),
                prescription.getPharmacyName(),
                prescription.getStatus(),
                prescription.getIssueDate(),
                prescription.getCollectionDate()
            });
        }
        CsvUtil.write(path, header, rows);
    }

    private void saveReferrals(Path path) throws IOException {
        String[] header = new String[]{
            "referral_id", "patient_id", "referring_clinician_id", "referred_to_clinician_id",
            "referring_facility_id", "referred_to_facility_id", "referral_date", "urgency_level",
            "referral_reason", "clinical_summary", "requested_investigations", "status",
            "appointment_id", "notes", "created_date", "last_updated"
        };
        List<String[]> rows = new ArrayList<>();
        for (Referral referral : referrals) {
            rows.add(new String[]{
                referral.getReferralId(),
                referral.getPatientId(),
                referral.getReferringClinicianId(),
                referral.getReferredToClinicianId(),
                referral.getReferringFacilityId(),
                referral.getReferredToFacilityId(),
                referral.getReferralDate(),
                referral.getUrgencyLevel(),
                referral.getReferralReason(),
                referral.getClinicalSummary(),
                referral.getInvestigationsAsString(),
                referral.getStatus(),
                referral.getAppointmentId(),
                referral.getNotes(),
                referral.getCreatedDate(),
                referral.getLastUpdated()
            });
        }
        CsvUtil.write(path, header, rows);
    }

    private void saveStaff(Path path) throws IOException {
        String[] header = new String[]{
            "staff_id", "first_name", "last_name", "role", "department", "facility_id",
            "phone_number", "email", "employment_status", "start_date", "line_manager", "access_level"
        };
        List<String[]> rows = new ArrayList<>();
        for (Staff member : staff) {
            rows.add(new String[]{
                member.getStaffId(),
                member.getFirstName(),
                member.getLastName(),
                member.getRole(),
                member.getDepartment(),
                member.getFacilityId(),
                member.getPhoneNumber(),
                member.getEmail(),
                member.getEmploymentStatus(),
                member.getStartDate(),
                member.getLineManager(),
                member.getAccessLevel()
            });
        }
        CsvUtil.write(path, header, rows);
    }

    private String get(String[] row, int index) {
        if (index >= 0 && index < row.length) {
            return row[index];
        }
        return "";
    }

    private List<String> splitList(String value) {
        if (value == null || value.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(value.split("\\|")));
    }
}
