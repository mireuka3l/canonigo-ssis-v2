/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */

/**
 *
 * @author reyvi
 */
public class UpdateStudentDialog extends javax.swing.JDialog {

private String originalId;
    /**
     * Creates new form AddStudentDialog
     */
    public UpdateStudentDialog(java.awt.Frame parent, boolean modal, String id, String firstname, String lastname,
                                String programCode, String year, String gender) {
        super(parent, modal);
        initComponents();
        this.originalId = id;
        
        jTextField3.setText(id);
        jTextField1.setText(firstname);
        jTextField2.setText(lastname);
        jComboBox3.setSelectedItem(gender);
        jComboBox2.setSelectedItem(year);
        
        try {
            jComboBox1.removeAllItems();
            for (java.util.Map<String,String> p : SqliteDb.programList("code", false, 0, 0)) {
                jComboBox1.addItem(p.get("code"));
            }
            jComboBox1.setSelectedItem(programCode);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        
        
        fixComboBox(jComboBox1);
        fixComboBox(jComboBox2);
        fixComboBox(jComboBox3);
        
        jTextField1.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(224, 221, 216), 1),
            javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        jTextField2.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(224, 221, 216), 1),
            javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        jTextField3.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(224, 221, 216), 1),
            javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

    }

    private void fixComboBox(javax.swing.JComboBox combo) {
        combo.setBackground(java.awt.Color.WHITE);
        combo.setForeground(new java.awt.Color(26, 26, 46));
        combo.setFocusable(false);
        combo.setFont(new java.awt.Font("Syne", java.awt.Font.PLAIN, 13));

        combo.setRenderer(new javax.swing.DefaultListCellRenderer() {
            public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    setBackground(new java.awt.Color(232, 79, 39));
                    setForeground(java.awt.Color.WHITE);
                } else {
                    setBackground(java.awt.Color.WHITE);
                    setForeground(new java.awt.Color(26, 26, 46));
                }
                setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
    }

    private void styleNavButton(javax.swing.JButton btn, javax.swing.JButton[] allBtns) {
        java.awt.Color normal     = new java.awt.Color(26, 26, 46);
        java.awt.Color hover      = new java.awt.Color(40, 40, 62);
        java.awt.Color active     = new java.awt.Color(60, 35, 30);
        java.awt.Color textNormal = new java.awt.Color(150, 150, 170);
        java.awt.Color textActive = java.awt.Color.WHITE;

        btn.setBackground(normal);
        btn.setForeground(textNormal);
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!btn.getBackground().equals(active)) {
                    btn.setBackground(hover);
                    btn.setForeground(textActive);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!btn.getBackground().equals(active)) {
                    btn.setBackground(normal);
                    btn.setForeground(textNormal);
                }
            }
            public void mousePressed(java.awt.event.MouseEvent e) {
                // Reset all buttons
                for (javax.swing.JButton b : allBtns) {
                    b.setBackground(normal);
                    b.setForeground(textNormal);
                }
                // Set this one as active
                btn.setBackground(active);
                btn.setForeground(textActive);
            }
        });
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jComboBox1 = new javax.swing.JComboBox<>();
        jTextField2 = new javax.swing.JTextField();
        jTextField1 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(240, 238, 233));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(240, 238, 233));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton3.setBackground(new java.awt.Color(26, 26, 46));
        jButton3.setFont(new java.awt.Font("Syne", 0, 13)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Save");
        jButton3.setBorderPainted(false);
        jButton3.setFocusPainted(false);
        jButton3.setOpaque(true);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 350, 90, 31));

        jButton1.setBackground(new java.awt.Color(254, 254, 254));
        jButton1.setFont(new java.awt.Font("Syne", 0, 13)); // NOI18N
        jButton1.setForeground(new java.awt.Color(102, 102, 102));
        jButton1.setText("Cancel");
        jButton1.setBorderPainted(false);
        jButton1.setFocusPainted(false);
        jButton1.setOpaque(true);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 350, 90, 31));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(26, 26, 46));
        jLabel7.setText("Update Student");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, -1, -1));

        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });
        jPanel1.add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 278, 37));

        jLabel6.setFont(new java.awt.Font("Syne", 0, 13)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(26, 26, 46));
        jLabel6.setText("STUDENT  ID (YYYY-NNNN)");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, -1, -1));

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Female", "Male", "Other" }));
        jPanel1.add(jComboBox3, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 290, 130, 36));

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });
        jPanel1.add(jComboBox2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 290, 130, 37));

        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        jPanel1.add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 220, 278, 37));

        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });
        jPanel1.add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 150, 130, 37));

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        jPanel1.add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, 130, 37));

        jLabel5.setFont(new java.awt.Font("Syne", 0, 13)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(26, 26, 46));
        jLabel5.setText("GENDER");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 270, -1, -1));

        jLabel4.setFont(new java.awt.Font("Syne", 0, 13)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(26, 26, 46));
        jLabel4.setText("YEAR");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 270, -1, -1));

        jLabel3.setFont(new java.awt.Font("Syne", 0, 13)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(26, 26, 46));
        jLabel3.setText("PROGRAM");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, -1, -1));

        jLabel2.setFont(new java.awt.Font("Syne", 0, 13)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(26, 26, 46));
        jLabel2.setText("LAST NAME");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 130, -1, -1));

        jLabel1.setFont(new java.awt.Font("Syne", 0, 13)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(26, 26, 46));
        jLabel1.setText("FIRST NAME");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 130, -1, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 340, 410));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        String newId     = jTextField3.getText().trim();
        String firstname = jTextField1.getText().trim();
        String lastname  = jTextField2.getText().trim();
        String program   = jComboBox1.getSelectedItem() != null ? jComboBox1.getSelectedItem().toString() : "";
        String year      = jComboBox2.getSelectedItem().toString();
        String gender    = jComboBox3.getSelectedItem().toString();

        if (firstname.isEmpty() || lastname.isEmpty() || program.isEmpty() || newId.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "All fields are required.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newId.matches("\\d{4}-\\d{4}")) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "ID must be in format YYYY-NNNN (e.g. 2024-0001).", "Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idYear = Integer.parseInt(newId.substring(0, 4));
        int currentYear = java.time.Year.now().getValue();
        if (idYear < 2010 || idYear > currentYear + 1) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "ID year must be between 2010 and " + (currentYear + 1) + ".", "Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            SqliteDb.studentUpdateFull(originalId, newId, firstname, lastname, program, year, gender);
            if (getOwner() instanceof GUI) ((GUI) getOwner()).reloadAllTables();
            dispose();
        } catch (SqliteDb.StudentError | java.sql.SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AddStudentDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AddStudentDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AddStudentDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AddStudentDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AddStudentDialog dialog = new AddStudentDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables
}
