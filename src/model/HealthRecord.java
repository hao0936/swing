package model;

import java.util.ArrayList;
import java.util.List;

public class HealthRecord {
    private final String patientId;
    private final List<String> clinicalNotes = new ArrayList<>();
    private final List<Prescription> prescriptions = new ArrayList<>();
    private final List<Referral> referrals = new ArrayList<>();

    public HealthRecord(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientId() {
        return patientId;
    }

    public List<String> getClinicalNotes() {
        return clinicalNotes;
    }

    public List<Prescription> getPrescriptions() {
        return prescriptions;
    }

    public List<Referral> getReferrals() {
        return referrals;
    }

    public void addNote(String note) {
        if (note != null && !note.trim().isEmpty()) {
            clinicalNotes.add(note.trim());
        }
    }

    public void addPrescription(Prescription prescription) {
        if (prescription != null) {
            prescriptions.add(prescription);
        }
    }

    public void addReferral(Referral referral) {
        if (referral != null) {
            referrals.add(referral);
        }
    }
}
