package view;

import model.Prescription;
import util.I18n;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class PrescriptionTableModel extends AbstractTableModel {
    private final List<Prescription> prescriptions;
    private final String[] columns = {
        "prescription.prescription_id",
        "prescription.patient_id",
        "prescription.clinician_id",
        "prescription.appointment_id",
        "prescription.prescription_date",
        "prescription.medication_name",
        "prescription.dosage",
        "prescription.frequency",
        "prescription.duration_days",
        "prescription.quantity",
        "prescription.instructions",
        "prescription.pharmacy_name",
        "prescription.status",
        "prescription.issue_date",
        "prescription.collection_date"
    };

    public PrescriptionTableModel(List<Prescription> prescriptions) {
        this.prescriptions = prescriptions;
    }

    public Prescription getPrescriptionAt(int row) {
        return prescriptions.get(row);
    }

    @Override
    public int getRowCount() {
        return prescriptions.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return I18n.t(columns[column]);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Prescription prescription = prescriptions.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return prescription.getPrescriptionId();
            case 1:
                return prescription.getPatientId();
            case 2:
                return prescription.getClinicianId();
            case 3:
                return prescription.getAppointmentId();
            case 4:
                return prescription.getPrescriptionDate();
            case 5:
                return prescription.getMedicationName();
            case 6:
                return prescription.getDosage();
            case 7:
                return prescription.getFrequency();
            case 8:
                return prescription.getDurationDays();
            case 9:
                return prescription.getQuantity();
            case 10:
                return prescription.getInstructions();
            case 11:
                return prescription.getPharmacyName();
            case 12:
                return prescription.getStatus();
            case 13:
                return prescription.getIssueDate();
            case 14:
                return prescription.getCollectionDate();
            default:
                return "";
        }
    }
}
