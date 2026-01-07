package view;

import controller.AppController;
import model.UserRole;
import model.UserSession;
import util.I18n;
import util.Language;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class MainFrame extends JFrame {
    private final AppController controller;
    private final JTabbedPane tabs = new JTabbedPane();
    private final JButton saveButton = new JButton();
    private final JButton switchUserButton = new JButton();
    private final JComboBox<String> languageBox = new JComboBox<>(new String[]{"English", "中文"});
    private final JLabel userLabel = new JLabel();
    private final JLabel languageLabel = new JLabel();
    private final List<Localizable> localizables;
    private final List<SessionAware> sessionAwarePanels;
    private final PatientPanel patientPanel;
    private final ClinicianPanel clinicianPanel;
    private final AppointmentPanel appointmentPanel;
    private final PrescriptionPanel prescriptionPanel;
    private final ReferralPanel referralPanel;
    private final FacilityPanel facilityPanel;
    private final StaffPanel staffPanel;

    private UserSession session;
    private JPanel topBar;

    public MainFrame(AppController controller, UserSession session) {
        this.controller = controller;
        this.session = session;

        patientPanel = new PatientPanel(controller);
        clinicianPanel = new ClinicianPanel(controller);
        appointmentPanel = new AppointmentPanel(controller);
        prescriptionPanel = new PrescriptionPanel(controller);
        referralPanel = new ReferralPanel(controller);
        facilityPanel = new FacilityPanel(controller);
        staffPanel = new StaffPanel(controller);

        this.localizables = Arrays.asList(
            patientPanel, clinicianPanel, appointmentPanel, prescriptionPanel, referralPanel,
            facilityPanel, staffPanel
        );
        this.sessionAwarePanels = Arrays.asList(
            patientPanel, clinicianPanel, appointmentPanel, prescriptionPanel, referralPanel,
            facilityPanel, staffPanel
        );

        setLayout(new BorderLayout());
        topBar = buildTopBar();
        add(topBar, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        applySession(session);

        setTitle(I18n.t("app.title"));
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private JPanel buildTopBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(userLabel);
        panel.add(languageLabel);
        panel.add(languageBox);
        panel.add(switchUserButton);
        panel.add(saveButton);

        languageBox.addActionListener(event -> {
            Language language = languageBox.getSelectedIndex() == 1 ? Language.ZH : Language.EN;
            I18n.setLanguage(language);
            applyLanguage();
        });

        switchUserButton.addActionListener(event -> {
            LoginDialog dialog = new LoginDialog(this, controller);
            dialog.setVisible(true);
            UserSession newSession = dialog.getSession();
            if (newSession != null) {
                applySession(newSession);
            }
        });

        saveButton.addActionListener(event -> {
            try {
                Path savedRoot = controller.saveAll();
                JOptionPane.showMessageDialog(this, I18n.t("message.saved", savedRoot.toString()));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, I18n.t("message.saveFailedDetail", ex.getMessage()));
            }
        });

        updateTopBarTexts();
        return panel;
    }

    private void applySession(UserSession newSession) {
        this.session = newSession;
        for (SessionAware sessionAware : sessionAwarePanels) {
            sessionAware.setSession(session);
        }
        configureTabsForRole(session.getRole());
        updateTopBarTexts();
    }

    private void configureTabsForRole(UserRole role) {
        tabs.removeAll();
        if (role == UserRole.ADMIN) {
            tabs.addTab(I18n.t("tab.patients"), patientPanel);
            tabs.addTab(I18n.t("tab.clinicians"), clinicianPanel);
            tabs.addTab(I18n.t("tab.appointments"), appointmentPanel);
            tabs.addTab(I18n.t("tab.prescriptions"), prescriptionPanel);
            tabs.addTab(I18n.t("tab.referrals"), referralPanel);
            tabs.addTab(I18n.t("tab.facilities"), facilityPanel);
            tabs.addTab(I18n.t("tab.staff"), staffPanel);
        } else if (role == UserRole.CLINICIAN) {
            tabs.addTab(I18n.t("tab.patients"), patientPanel);
            tabs.addTab(I18n.t("tab.appointments"), appointmentPanel);
            tabs.addTab(I18n.t("tab.prescriptions"), prescriptionPanel);
            tabs.addTab(I18n.t("tab.referrals"), referralPanel);
        } else {
            tabs.addTab(I18n.t("tab.patients"), patientPanel);
            tabs.addTab(I18n.t("tab.appointments"), appointmentPanel);
            tabs.addTab(I18n.t("tab.prescriptions"), prescriptionPanel);
            tabs.addTab(I18n.t("tab.referrals"), referralPanel);
        }
    }

    private void updateTopBarTexts() {
        languageLabel.setText(I18n.t("label.language"));
        saveButton.setText(I18n.t("button.save"));
        switchUserButton.setText(I18n.t("button.switchUser"));

        String roleName = roleLabel(session.getRole());
        String displayName = session.getDisplayName();
        if (displayName == null || displayName.trim().isEmpty()) {
            displayName = session.getUserId();
        }
        userLabel.setText(I18n.t("label.user") + ": " + displayName + " (" + roleName + ")");
    }

    private String roleLabel(UserRole role) {
        if (role == UserRole.ADMIN) {
            return I18n.t("role.admin");
        }
        if (role == UserRole.CLINICIAN) {
            return I18n.t("role.clinician");
        }
        return I18n.t("role.patient");
    }

    private void applyLanguage() {
        setTitle(I18n.t("app.title"));
        for (Localizable localizable : localizables) {
            localizable.updateTexts();
        }
        configureTabsForRole(session.getRole());
        updateTopBarTexts();
        SwingUtilities.updateComponentTreeUI(this);
    }
}
