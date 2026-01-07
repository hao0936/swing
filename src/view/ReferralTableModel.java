package view;

import model.Referral;
import util.I18n;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ReferralTableModel extends AbstractTableModel {
    private final List<Referral> referrals;
    private final String[] columns = {
        "referral.referral_id",
        "referral.patient_id",
        "referral.referring_clinician_id",
        "referral.referred_to_clinician_id",
        "referral.referring_facility_id",
        "referral.referred_to_facility_id",
        "referral.referral_date",
        "referral.urgency_level",
        "referral.referral_reason",
        "referral.clinical_summary",
        "referral.requested_investigations",
        "referral.status",
        "referral.appointment_id",
        "referral.notes",
        "referral.created_date",
        "referral.last_updated"
    };

    public ReferralTableModel(List<Referral> referrals) {
        this.referrals = referrals;
    }

    public Referral getReferralAt(int row) {
        return referrals.get(row);
    }

    @Override
    public int getRowCount() {
        return referrals.size();
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
        Referral referral = referrals.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return referral.getReferralId();
            case 1:
                return referral.getPatientId();
            case 2:
                return referral.getReferringClinicianId();
            case 3:
                return referral.getReferredToClinicianId();
            case 4:
                return referral.getReferringFacilityId();
            case 5:
                return referral.getReferredToFacilityId();
            case 6:
                return referral.getReferralDate();
            case 7:
                return referral.getUrgencyLevel();
            case 8:
                return referral.getReferralReason();
            case 9:
                return referral.getClinicalSummary();
            case 10:
                return referral.getInvestigationsAsString();
            case 11:
                return referral.getStatus();
            case 12:
                return referral.getAppointmentId();
            case 13:
                return referral.getNotes();
            case 14:
                return referral.getCreatedDate();
            case 15:
                return referral.getLastUpdated();
            default:
                return "";
        }
    }
}
