package util;

import java.util.HashMap;
import java.util.Map;

public final class I18n {
    private static final Map<Language, Map<String, String>> STRINGS = new HashMap<>();
    private static Language language = Language.EN;

    static {
        Map<String, String> en = new HashMap<>();
        en.put("app.title", "Healthcare Management System");
        en.put("label.language", "Language");
        en.put("label.role", "Role");
        en.put("label.user", "User");
        en.put("tab.patients", "Patients");
        en.put("tab.clinicians", "Clinicians");
        en.put("tab.appointments", "Appointments");
        en.put("tab.prescriptions", "Prescriptions");
        en.put("tab.referrals", "Referrals");
        en.put("tab.facilities", "Facilities");
        en.put("tab.staff", "Staff");
        en.put("role.patient", "Patient");
        en.put("role.clinician", "Clinician");
        en.put("role.admin", "Admin");
        en.put("button.add", "Add");
        en.put("button.edit", "Edit");
        en.put("button.delete", "Delete");
        en.put("button.export", "Export");
        en.put("button.updateStatus", "Update Status");
        en.put("button.cancel", "Cancel");
        en.put("button.login", "Login");
        en.put("button.register", "Register");
        en.put("button.switchUser", "Switch User");
        en.put("button.save", "Save All");
        en.put("button.refresh", "Refresh");
        en.put("dialog.addPatient", "Add Patient");
        en.put("dialog.editPatient", "Edit Patient");
        en.put("dialog.addClinician", "Add Clinician");
        en.put("dialog.editClinician", "Edit Clinician");
        en.put("dialog.addAppointment", "Add Appointment");
        en.put("dialog.editAppointment", "Edit Appointment");
        en.put("dialog.addPrescription", "Add Prescription");
        en.put("dialog.editPrescription", "Edit Prescription");
        en.put("dialog.addReferral", "Add Referral");
        en.put("dialog.editReferral", "Edit Referral");
        en.put("dialog.login", "Login");
        en.put("message.selectRow", "Please select a row first.");
        en.put("message.selectUser", "Please select a user.");
        en.put("message.noUsers", "No users available for this role.");
        en.put("message.conflict", "Appointment conflict for the clinician at that time.");
        en.put("message.exportSuccess", "Exported to: %s");
        en.put("message.exportFailed", "Export failed.");
        en.put("message.saved", "Saved to: %s");
        en.put("message.saveFailedDetail", "Save failed: %s");

        addPatientLabels(en);
        addClinicianLabels(en);
        addAppointmentLabels(en);
        addPrescriptionLabels(en);
        addReferralLabels(en);
        addFacilityLabels(en);
        addStaffLabels(en);

        Map<String, String> zh = new HashMap<>();
        zh.put("app.title", "医疗管理系统");
        zh.put("label.language", "语言");
        zh.put("label.role", "角色");
        zh.put("label.user", "用户");
        zh.put("tab.patients", "患者");
        zh.put("tab.clinicians", "临床医生");
        zh.put("tab.appointments", "预约");
        zh.put("tab.prescriptions", "处方");
        zh.put("tab.referrals", "转诊");
        zh.put("tab.facilities", "机构");
        zh.put("tab.staff", "员工");
        zh.put("role.patient", "患者");
        zh.put("role.clinician", "医护人员");
        zh.put("role.admin", "行政人员");
        zh.put("button.add", "新增");
        zh.put("button.edit", "编辑");
        zh.put("button.delete", "删除");
        zh.put("button.export", "导出");
        zh.put("button.updateStatus", "更新状态");
        zh.put("button.cancel", "取消");
        zh.put("button.login", "登录");
        zh.put("button.register", "注册");
        zh.put("button.switchUser", "切换用户");
        zh.put("button.save", "保存全部");
        zh.put("button.refresh", "刷新");
        zh.put("dialog.addPatient", "新增患者");
        zh.put("dialog.editPatient", "编辑患者");
        zh.put("dialog.addClinician", "新增临床医生");
        zh.put("dialog.editClinician", "编辑临床医生");
        zh.put("dialog.addAppointment", "新增预约");
        zh.put("dialog.editAppointment", "编辑预约");
        zh.put("dialog.addPrescription", "新增处方");
        zh.put("dialog.editPrescription", "编辑处方");
        zh.put("dialog.addReferral", "新增转诊");
        zh.put("dialog.editReferral", "编辑转诊");
        zh.put("dialog.login", "登录");
        zh.put("message.selectRow", "请先选择一行。");
        zh.put("message.selectUser", "请选择用户。");
        zh.put("message.noUsers", "该角色暂无可用用户。");
        zh.put("message.conflict", "该时间段医生已有预约冲突。");
        zh.put("message.exportSuccess", "已导出到: %s");
        zh.put("message.exportFailed", "导出失败。");
        zh.put("message.saved", "已保存到: %s");
        zh.put("message.saveFailedDetail", "保存失败: %s");

        addPatientLabels(zh, true);
        addClinicianLabels(zh, true);
        addAppointmentLabels(zh, true);
        addPrescriptionLabels(zh, true);
        addReferralLabels(zh, true);
        addFacilityLabels(zh, true);
        addStaffLabels(zh, true);

        STRINGS.put(Language.EN, en);
        STRINGS.put(Language.ZH, zh);
    }

    private I18n() {
    }

    public static void setLanguage(Language newLanguage) {
        language = newLanguage;
    }

    public static Language getLanguage() {
        return language;
    }

    public static String t(String key, Object... args) {
        Map<String, String> bundle = STRINGS.getOrDefault(language, STRINGS.get(Language.EN));
        String value = bundle.getOrDefault(key, key);
        if (args != null && args.length > 0) {
            return String.format(value, args);
        }
        return value;
    }

    private static void addPatientLabels(Map<String, String> map) {
        addPatientLabels(map, false);
    }

    private static void addPatientLabels(Map<String, String> map, boolean zh) {
        map.put("patient.patient_id", zh ? "患者编号" : "Patient ID");
        map.put("patient.first_name", zh ? "名" : "First Name");
        map.put("patient.last_name", zh ? "姓" : "Last Name");
        map.put("patient.date_of_birth", zh ? "出生日期" : "Date of Birth");
        map.put("patient.nhs_number", zh ? "NHS 编号" : "NHS Number");
        map.put("patient.gender", zh ? "性别" : "Gender");
        map.put("patient.phone_number", zh ? "电话" : "Phone" );
        map.put("patient.email", zh ? "邮箱" : "Email");
        map.put("patient.address", zh ? "地址" : "Address");
        map.put("patient.postcode", zh ? "邮编" : "Postcode");
        map.put("patient.emergency_contact_name", zh ? "紧急联系人" : "Emergency Contact Name");
        map.put("patient.emergency_contact_phone", zh ? "紧急联系人电话" : "Emergency Contact Phone");
        map.put("patient.registration_date", zh ? "注册日期" : "Registration Date");
        map.put("patient.gp_surgery_id", zh ? "全科诊所ID" : "GP Surgery ID");
    }

    private static void addClinicianLabels(Map<String, String> map) {
        addClinicianLabels(map, false);
    }

    private static void addClinicianLabels(Map<String, String> map, boolean zh) {
        map.put("clinician.clinician_id", zh ? "医生编号" : "Clinician ID");
        map.put("clinician.first_name", zh ? "名" : "First Name");
        map.put("clinician.last_name", zh ? "姓" : "Last Name");
        map.put("clinician.title", zh ? "职称" : "Title");
        map.put("clinician.speciality", zh ? "专业" : "Speciality");
        map.put("clinician.gmc_number", zh ? "GMC 编号" : "GMC Number");
        map.put("clinician.phone_number", zh ? "电话" : "Phone");
        map.put("clinician.email", zh ? "邮箱" : "Email");
        map.put("clinician.workplace_id", zh ? "工作地点ID" : "Workplace ID");
        map.put("clinician.workplace_type", zh ? "工作地点类型" : "Workplace Type");
        map.put("clinician.employment_status", zh ? "雇佣状态" : "Employment Status");
        map.put("clinician.start_date", zh ? "入职日期" : "Start Date");
    }

    private static void addAppointmentLabels(Map<String, String> map) {
        addAppointmentLabels(map, false);
    }

    private static void addAppointmentLabels(Map<String, String> map, boolean zh) {
        map.put("appointment.appointment_id", zh ? "预约编号" : "Appointment ID");
        map.put("appointment.patient_id", zh ? "患者编号" : "Patient ID");
        map.put("appointment.clinician_id", zh ? "医生编号" : "Clinician ID");
        map.put("appointment.facility_id", zh ? "机构编号" : "Facility ID");
        map.put("appointment.appointment_date", zh ? "日期" : "Date");
        map.put("appointment.appointment_time", zh ? "时间" : "Time");
        map.put("appointment.duration_minutes", zh ? "时长(分钟)" : "Duration (min)");
        map.put("appointment.appointment_type", zh ? "类型" : "Type");
        map.put("appointment.status", zh ? "状态" : "Status");
        map.put("appointment.reason_for_visit", zh ? "就诊原因" : "Reason");
        map.put("appointment.notes", zh ? "备注" : "Notes");
        map.put("appointment.created_date", zh ? "创建日期" : "Created" );
        map.put("appointment.last_modified", zh ? "最后修改" : "Last Modified");
    }

    private static void addPrescriptionLabels(Map<String, String> map) {
        addPrescriptionLabels(map, false);
    }

    private static void addPrescriptionLabels(Map<String, String> map, boolean zh) {
        map.put("prescription.prescription_id", zh ? "处方编号" : "Prescription ID");
        map.put("prescription.patient_id", zh ? "患者编号" : "Patient ID");
        map.put("prescription.clinician_id", zh ? "医生编号" : "Clinician ID");
        map.put("prescription.appointment_id", zh ? "预约编号" : "Appointment ID");
        map.put("prescription.prescription_date", zh ? "处方日期" : "Prescription Date");
        map.put("prescription.medication_name", zh ? "药物" : "Medication");
        map.put("prescription.dosage", zh ? "剂量" : "Dosage");
        map.put("prescription.frequency", zh ? "频次" : "Frequency");
        map.put("prescription.duration_days", zh ? "疗程(天)" : "Duration (days)");
        map.put("prescription.quantity", zh ? "数量" : "Quantity");
        map.put("prescription.instructions", zh ? "用药说明" : "Instructions");
        map.put("prescription.pharmacy_name", zh ? "药房" : "Pharmacy" );
        map.put("prescription.status", zh ? "状态" : "Status");
        map.put("prescription.issue_date", zh ? "开具日期" : "Issue Date");
        map.put("prescription.collection_date", zh ? "取药日期" : "Collection Date");
    }

    private static void addReferralLabels(Map<String, String> map) {
        addReferralLabels(map, false);
    }

    private static void addReferralLabels(Map<String, String> map, boolean zh) {
        map.put("referral.referral_id", zh ? "转诊编号" : "Referral ID");
        map.put("referral.patient_id", zh ? "患者编号" : "Patient ID");
        map.put("referral.referring_clinician_id", zh ? "转诊医生ID" : "Referring Clinician ID");
        map.put("referral.referred_to_clinician_id", zh ? "接诊医生ID" : "Referred Clinician ID");
        map.put("referral.referring_facility_id", zh ? "转诊机构ID" : "Referring Facility ID");
        map.put("referral.referred_to_facility_id", zh ? "接诊机构ID" : "Referred Facility ID");
        map.put("referral.referral_date", zh ? "转诊日期" : "Referral Date");
        map.put("referral.urgency_level", zh ? "紧急程度" : "Urgency" );
        map.put("referral.referral_reason", zh ? "转诊原因" : "Referral Reason");
        map.put("referral.clinical_summary", zh ? "临床摘要" : "Clinical Summary");
        map.put("referral.requested_investigations", zh ? "检查项目" : "Investigations");
        map.put("referral.status", zh ? "状态" : "Status");
        map.put("referral.appointment_id", zh ? "预约编号" : "Appointment ID");
        map.put("referral.notes", zh ? "备注" : "Notes");
        map.put("referral.created_date", zh ? "创建日期" : "Created Date");
        map.put("referral.last_updated", zh ? "最后更新" : "Last Updated");
    }

    private static void addFacilityLabels(Map<String, String> map) {
        addFacilityLabels(map, false);
    }

    private static void addFacilityLabels(Map<String, String> map, boolean zh) {
        map.put("facility.facility_id", zh ? "机构编号" : "Facility ID");
        map.put("facility.facility_name", zh ? "机构名称" : "Facility Name");
        map.put("facility.facility_type", zh ? "机构类型" : "Facility Type");
        map.put("facility.address", zh ? "地址" : "Address");
        map.put("facility.postcode", zh ? "邮编" : "Postcode");
        map.put("facility.phone_number", zh ? "电话" : "Phone");
        map.put("facility.email", zh ? "邮箱" : "Email");
        map.put("facility.opening_hours", zh ? "开放时间" : "Opening Hours");
        map.put("facility.manager_name", zh ? "负责人" : "Manager");
        map.put("facility.capacity", zh ? "容量" : "Capacity");
        map.put("facility.specialities_offered", zh ? "提供服务" : "Specialities Offered");
    }

    private static void addStaffLabels(Map<String, String> map) {
        addStaffLabels(map, false);
    }

    private static void addStaffLabels(Map<String, String> map, boolean zh) {
        map.put("staff.staff_id", zh ? "员工编号" : "Staff ID");
        map.put("staff.first_name", zh ? "名" : "First Name");
        map.put("staff.last_name", zh ? "姓" : "Last Name");
        map.put("staff.role", zh ? "岗位" : "Role");
        map.put("staff.department", zh ? "部门" : "Department");
        map.put("staff.facility_id", zh ? "机构编号" : "Facility ID");
        map.put("staff.phone_number", zh ? "电话" : "Phone");
        map.put("staff.email", zh ? "邮箱" : "Email");
        map.put("staff.employment_status", zh ? "雇佣状态" : "Employment Status");
        map.put("staff.start_date", zh ? "入职日期" : "Start Date");
        map.put("staff.line_manager", zh ? "直属主管" : "Line Manager");
        map.put("staff.access_level", zh ? "权限级别" : "Access Level");
    }
}

