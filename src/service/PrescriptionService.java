package service;

import model.Patient;
import model.Prescription;

import java.util.List;

public class PrescriptionService {
    private final List<Prescription> prescriptions;

    public PrescriptionService(List<Prescription> prescriptions) {
        this.prescriptions = prescriptions;
    }

    public void createPrescription(Prescription prescription, Patient patient) {
        prescriptions.add(prescription);
        if (patient != null) {
            patient.getHealthRecord().addPrescription(prescription);
        }
    }
}
