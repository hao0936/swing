package controller;

import data.DataStore;
import model.Appointment;
import model.Clinician;
import model.Facility;
import model.Patient;
import model.Prescription;
import model.Referral;
import model.Staff;
import service.AppointmentService;
import service.PrescriptionService;
import service.ReferralCoordinator;
import service.TextExportService;
import util.IdGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AppController {
    private final DataStore dataStore;
    private final AppointmentService appointmentService;
    private final PrescriptionService prescriptionService;
    private final ReferralCoordinator referralCoordinator;
    private final TextExportService textExportService;
    private final Path dataRoot;

    public AppController(Path dataRoot) throws IOException {
        this.dataRoot = dataRoot;
        this.dataStore = new DataStore();
        this.dataStore.loadAll(dataRoot);
        this.appointmentService = new AppointmentService(dataStore.getAppointments());
        this.prescriptionService = new PrescriptionService(dataStore.getPrescriptions());
        this.referralCoordinator = ReferralCoordinator.getInstance();
        this.referralCoordinator.initialize(dataStore);
        this.textExportService = new TextExportService();
    }

    public Path saveAll() throws IOException {
        try {
            dataStore.saveAll(dataRoot);
            return dataRoot;
        } catch (IOException ex) {
            Path fallback = dataRoot.resolve("output");
            try {
                Files.createDirectories(fallback);
                dataStore.saveAll(fallback);
                return fallback;
            } catch (IOException fallbackEx) {
                fallbackEx.addSuppressed(ex);
                throw fallbackEx;
            }
        }
    }

    public List<Patient> getPatients() {
        return dataStore.getPatients();
    }

    public List<Clinician> getClinicians() {
        return dataStore.getClinicians();
    }

    public List<Facility> getFacilities() {
        return dataStore.getFacilities();
    }

    public List<Appointment> getAppointments() {
        return dataStore.getAppointments();
    }

    public List<Prescription> getPrescriptions() {
        return dataStore.getPrescriptions();
    }

    public List<Referral> getReferrals() {
        return dataStore.getReferrals();
    }

    public List<Staff> getStaff() {
        return dataStore.getStaff();
    }

    public Patient findPatient(String patientId) {
        return dataStore.findPatient(patientId);
    }

    public Clinician findClinician(String clinicianId) {
        return dataStore.findClinician(clinicianId);
    }

    public Facility findFacility(String facilityId) {
        return dataStore.findFacility(facilityId);
    }

    public String nextPatientId() {
        return IdGenerator.nextId("P", collectPatientIds(), 3);
    }

    public String nextClinicianId() {
        return IdGenerator.nextId("C", collectClinicianIds(), 3);
    }

    public String nextAppointmentId() {
        return IdGenerator.nextId("A", collectAppointmentIds(), 3);
    }

    public String nextPrescriptionId() {
        return IdGenerator.nextId("RX", collectPrescriptionIds(), 3);
    }

    public String nextReferralId() {
        return IdGenerator.nextId("R", collectReferralIds(), 3);
    }

    public String nextFacilityId() {
        return IdGenerator.nextId("F", collectFacilityIds(), 3);
    }

    public String nextStaffId() {
        return IdGenerator.nextId("ST", collectStaffIds(), 3);
    }

    public void addPatient(Patient patient) {
        dataStore.getPatients().add(patient);
    }

    public void deletePatient(Patient patient) {
        dataStore.getPatients().remove(patient);
    }

    public void addClinician(Clinician clinician) {
        dataStore.getClinicians().add(clinician);
    }

    public void deleteClinician(Clinician clinician) {
        dataStore.getClinicians().remove(clinician);
    }

    public void addFacility(Facility facility) {
        dataStore.getFacilities().add(facility);
    }

    public void deleteFacility(Facility facility) {
        dataStore.getFacilities().remove(facility);
    }

    public void addStaff(Staff member) {
        dataStore.getStaff().add(member);
    }

    public void deleteStaff(Staff member) {
        dataStore.getStaff().remove(member);
    }

    public boolean addAppointment(Appointment appointment) {
        return appointmentService.createAppointment(appointment);
    }

    public void updateAppointment(Appointment appointment) {
        appointmentService.updateAppointment(appointment);
    }

    public void cancelAppointment(Appointment appointment) {
        appointmentService.cancelAppointment(appointment);
    }

    public void addPrescription(Prescription prescription) {
        Patient patient = findPatient(prescription.getPatientId());
        prescriptionService.createPrescription(prescription, patient);
    }

    public void deletePrescription(Prescription prescription) {
        dataStore.getPrescriptions().remove(prescription);
    }

    public void addReferral(Referral referral) {
        referralCoordinator.submitReferral(referral);
    }

    public void updateReferralStatus(Referral referral, String status) {
        referralCoordinator.updateStatus(referral, status);
    }

    public void deleteReferral(Referral referral) {
        dataStore.getReferrals().remove(referral);
        referralCoordinator.getReferralQueue().remove(referral);
    }

    public Path exportPrescription(Prescription prescription) throws IOException {
        Patient patient = findPatient(prescription.getPatientId());
        Clinician clinician = findClinician(prescription.getClinicianId());
        return textExportService.exportPrescription(prescription, patient, clinician, dataRoot.resolve("output"));
    }

    public Path exportReferral(Referral referral) throws IOException {
        Patient patient = findPatient(referral.getPatientId());
        Clinician referringClinician = findClinician(referral.getReferringClinicianId());
        Clinician referredClinician = findClinician(referral.getReferredToClinicianId());
        Facility referringFacility = findFacility(referral.getReferringFacilityId());
        Facility referredFacility = findFacility(referral.getReferredToFacilityId());
        return textExportService.exportReferral(referral, patient, referringClinician, referredClinician,
            referringFacility, referredFacility, dataRoot.resolve("output"));
    }

    private List<String> collectPatientIds() {
        List<String> ids = new ArrayList<>();
        for (Patient patient : dataStore.getPatients()) {
            ids.add(patient.getPatientId());
        }
        return ids;
    }

    private List<String> collectAppointmentIds() {
        List<String> ids = new ArrayList<>();
        for (Appointment appointment : dataStore.getAppointments()) {
            ids.add(appointment.getAppointmentId());
        }
        return ids;
    }

    private List<String> collectClinicianIds() {
        List<String> ids = new ArrayList<>();
        for (Clinician clinician : dataStore.getClinicians()) {
            ids.add(clinician.getClinicianId());
        }
        return ids;
    }

    private List<String> collectPrescriptionIds() {
        List<String> ids = new ArrayList<>();
        for (Prescription prescription : dataStore.getPrescriptions()) {
            ids.add(prescription.getPrescriptionId());
        }
        return ids;
    }

    private List<String> collectReferralIds() {
        List<String> ids = new ArrayList<>();
        for (Referral referral : dataStore.getReferrals()) {
            ids.add(referral.getReferralId());
        }
        return ids;
    }

    private List<String> collectFacilityIds() {
        List<String> ids = new ArrayList<>();
        for (Facility facility : dataStore.getFacilities()) {
            ids.add(facility.getFacilityId());
        }
        return ids;
    }

    private List<String> collectStaffIds() {
        List<String> ids = new ArrayList<>();
        for (Staff member : dataStore.getStaff()) {
            ids.add(member.getStaffId());
        }
        return ids;
    }
}
