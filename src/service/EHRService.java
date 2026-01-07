package service;

import model.HealthRecord;
import model.Referral;

public class EHRService {
    public void updateReferralStatus(HealthRecord record, Referral referral) {
        if (record != null && referral != null) {
            record.addReferral(referral);
        }
    }

    public void addClinicalNote(HealthRecord record, String note) {
        if (record != null) {
            record.addNote(note);
        }
    }
}
