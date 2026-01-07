package view;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public final class FormDialog {
    private FormDialog() {
    }

    public static String[] showDialog(Component parent, String title, String[] labels,
                                      String[] initialValues, boolean[] editable) {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField[] fields = new JTextField[labels.length];
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.weightx = 0.3;
            form.add(new JLabel(labels[i]), gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7;
            JTextField field = new JTextField(initialValues != null && i < initialValues.length
                ? initialValues[i] : "");
            field.setEditable(editable == null || i >= editable.length || editable[i]);
            fields[i] = field;
            form.add(field, gbc);
            gbc.gridy++;
        }

        JScrollPane scrollPane = new JScrollPane(form);
        scrollPane.setPreferredSize(new Dimension(520, Math.min(520, labels.length * 34 + 40)));

        int result = JOptionPane.showConfirmDialog(parent, scrollPane, title,
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        String[] values = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            values[i] = fields[i].getText().trim();
        }
        return values;
    }
}
