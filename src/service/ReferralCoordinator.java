package service;

import data.DataStore;
import model.Patient;
import model.Referral;

import java.util.ArrayList;
import java.util.List;

public final class ReferralCoordinator {
    private static final ReferralCoordinator INSTANCE = new ReferralCoordinator();

    private final List<Referral> referralQueue = new ArrayList<>();
    private final List<String> auditLog = new ArrayList<>();
    private final EHRService ehrService = new EHRService();
    private DataStore dataStore;

    private ReferralCoordinator() {
    }

    public static ReferralCoordinator getInstance() {
        return INSTANCE;
    }

    public void initialize(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void submitReferral(Referral referral) {
        if (referral == null) {
            return;
        }
        if (!containsReferral(referral.getReferralId())) {
            referralQueue.add(referral);
            if (dataStore != null) {
                dataStore.getReferrals().add(referral);
                Patient patient = dataStore.findPatient(referral.getPatientId());
                if (patient != null) {
                    ehrService.updateReferralStatus(patient.getHealthRecord(), referral);
                }
            }
            auditLog.add("Referral submitted: " + referral.getReferralId());
            sendReferralEmail(referral);
        }
    }

    public void updateStatus(Referral referral, String status) {
        if (referral == null) {
            return;
        }
        referral.setStatus(status);
        referral.setLastUpdated(DateTimeService.today());
        auditLog.add("Referral status updated: " + referral.getReferralId() + " -> " + status);
    }

    public List<Referral> getReferralQueue() {
        return referralQueue;
    }

    public List<String> getAuditLog() {
        return auditLog;
    }

    private void sendReferralEmail(Referral referral) {
        auditLog.add("Referral email generated for: " + referral.getReferralId());
    }

    private boolean containsReferral(String referralId) {
        for (Referral referral : referralQueue) {
            if (referral.getReferralId().equalsIgnoreCase(referralId)) {
                return true;
            }
        }
        return false;
    }
}
