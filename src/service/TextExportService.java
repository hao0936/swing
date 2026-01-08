package service;

import model.Clinician;
import model.Facility;
import model.Patient;
import model.Prescription;
import model.Referral;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TextExportService {
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Path exportPrescription(Prescription prescription, Patient patient, Clinician clinician, Path outputDir)
        throws IOException {
        Files.createDirectories(outputDir);
        String fileName = "prescription_" + prescription.getPrescriptionId() + ".txt";
        Path target = resolveUnique(outputDir.resolve(fileName));
        String content = buildPrescriptionText(prescription, patient, clinician);
        Files.write(target, content.getBytes(StandardCharsets.UTF_8));
        return target;
    }

    public Path exportReferral(Referral referral, Patient patient, Clinician referringClinician,
                               Clinician referredClinician, Facility referringFacility,
                               Facility referredFacility, Path outputDir) throws IOException {
        Files.createDirectories(outputDir);
        String fileName = "referral_" + referral.getReferralId() + ".txt";
        Path target = resolveUnique(outputDir.resolve(fileName));
        String content = buildReferralText(referral, patient, referringClinician, referredClinician,
            referringFacility, referredFacility);
        Files.write(target, content.getBytes(StandardCharsets.UTF_8));
        return target;
    }

    private Path resolveUnique(Path target) {
        if (!Files.exists(target)) {
            return target;
        }

        String fileName = target.getFileName().toString();
        String base = fileName;
        String ext = "";
        int dot = fileName.lastIndexOf('.');
        if (dot > 0) {
            base = fileName.substring(0, dot);
            ext = fileName.substring(dot);
        }

        int counter = 2;
        Path dir = target.getParent();
        Path candidate = target;
        while (Files.exists(candidate)) {
            candidate = dir.resolve(base + "_" + counter + ext);
            counter++;
        }
        return candidate;
    }

    private String buildPrescriptionText(Prescription prescription, Patient patient, Clinician clinician) {
        StringBuilder sb = new StringBuilder();
        sb.append("Prescription Summary\n");
        sb.append("Generated: ").append(LocalDateTime.now().format(STAMP)).append("\n\n");
        sb.append("Prescription ID: ").append(prescription.getPrescriptionId()).append("\n");
        sb.append("Patient: ").append(formatPerson(patient)).append("\n");
        sb.append("Clinician: ").append(formatPerson(clinician)).append("\n");
        sb.append("Appointment ID: ").append(prescription.getAppointmentId()).append("\n");
        sb.append("Date: ").append(prescription.getPrescriptionDate()).append("\n\n");
        sb.append("Medication: ").append(prescription.getMedicationName()).append("\n");
        sb.append("Dosage: ").append(prescription.getDosage()).append("\n");
        sb.append("Frequency: ").append(prescription.getFrequency()).append("\n");
        sb.append("Duration: ").append(prescription.getDurationDays()).append(" days\n");
        sb.append("Quantity: ").append(prescription.getQuantity()).append("\n");
        sb.append("Instructions: ").append(prescription.getInstructions()).append("\n\n");
        sb.append("Pharmacy: ").append(prescription.getPharmacyName()).append("\n");
        sb.append("Status: ").append(prescription.getStatus()).append("\n");
        sb.append("Issue Date: ").append(prescription.getIssueDate()).append("\n");
        sb.append("Collection Date: ").append(prescription.getCollectionDate()).append("\n");
        return sb.toString();
    }

    private String buildReferralText(Referral referral, Patient patient, Clinician referringClinician,
                                    Clinician referredClinician, Facility referringFacility,
                                    Facility referredFacility) {
        StringBuilder sb = new StringBuilder();
        sb.append("Referral Summary\n");
        sb.append("Generated: ").append(LocalDateTime.now().format(STAMP)).append("\n\n");
        sb.append("Referral ID: ").append(referral.getReferralId()).append("\n");
        sb.append("Patient: ").append(formatPerson(patient)).append("\n");
        sb.append("Referring Clinician: ").append(formatPerson(referringClinician)).append("\n");
        sb.append("Referred Clinician: ").append(formatPerson(referredClinician)).append("\n");
        sb.append("Referring Facility: ").append(formatFacility(referringFacility)).append("\n");
        sb.append("Referred Facility: ").append(formatFacility(referredFacility)).append("\n");
        sb.append("Referral Date: ").append(referral.getReferralDate()).append("\n");
        sb.append("Urgency: ").append(referral.getUrgencyLevel()).append("\n\n");
        sb.append("Reason: ").append(referral.getReferralReason()).append("\n");
        sb.append("Clinical Summary: ").append(referral.getClinicalSummary()).append("\n");
        sb.append("Requested Investigations: ").append(referral.getInvestigationsAsString()).append("\n");
        sb.append("Status: ").append(referral.getStatus()).append("\n");
        sb.append("Appointment ID: ").append(referral.getAppointmentId()).append("\n");
        sb.append("Notes: ").append(referral.getNotes()).append("\n");
        sb.append("Created: ").append(referral.getCreatedDate()).append("\n");
        sb.append("Last Updated: ").append(referral.getLastUpdated()).append("\n");
        return sb.toString();
    }

    private String formatPerson(Patient patient) {
        if (patient == null) {
            return "(unknown)";
        }
        return patient.getFullName() + " [" + patient.getPatientId() + "]";
    }

    private String formatPerson(Clinician clinician) {
        if (clinician == null) {
            return "(unknown)";
        }
        return clinician.getFullName() + " [" + clinician.getClinicianId() + "]";
    }

    private String formatFacility(Facility facility) {
        if (facility == null) {
            return "(unknown)";
        }
        return facility.getFacilityName() + " [" + facility.getFacilityId() + "]";
    }
}
