/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package projekakhir;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
/**
 *
 * @author Lenovo
 */
public class buat_akun extends javax.swing.JFrame {

    
    public buat_akun() {
        initComponents();
    }

    private void registerUser() {
        String username = tfusername.getText();
        String email = tfemail.getText();
        String password = tfpw.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Koneksi ke database
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ecommerce", "root", "")) {
            String query = "INSERT INTO users (username, email, password,role) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pst = conn.prepareStatement(query)) {
                pst.setString(1, username);
                pst.setString(2, email);
                pst.setString(3, password);
                pst.setString(4, "pembeli");

                int rowsInserted = pst.executeUpdate();

                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Akun berhasil dibuat!");
                    this.dispose();  // Menutup frame setelah berhasil daftar
                    new login().setVisible(true);
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                JOptionPane.showMessageDialog(this, "Username atau Email sudah digunakan.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal membuat akun.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tfusername = new javax.swing.JTextField();
        tfemail = new javax.swing.JTextField();
        tfpw = new javax.swing.JTextField();
        btndaftar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 204, 204));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Serif", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Kamu  Belum Buat Akun?");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 130, -1, -1));

        jLabel3.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Buat Akun Sekarang!");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 170, -1, -1));

        jLabel4.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Username :");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 220, -1, -1));

        jLabel5.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Email :");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 250, -1, -1));

        jLabel6.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Password :");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 280, -1, -1));

        tfusername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfusernameActionPerformed(evt);
            }
        });
        jPanel1.add(tfusername, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 220, 160, -1));
        jPanel1.add(tfemail, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 250, 160, -1));
        jPanel1.add(tfpw, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 280, 160, -1));

        btndaftar.setText("Daftar");
        btndaftar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btndaftarActionPerformed(evt);
            }
        });
        jPanel1.add(btndaftar, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 350, -1, -1));

        jLabel1.setBackground(new java.awt.Color(204, 255, 204));
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 550, 550));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tfusernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfusernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfusernameActionPerformed

    private void btndaftarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btndaftarActionPerformed
        registerUser();
    }//GEN-LAST:event_btndaftarActionPerformed

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
            java.util.logging.Logger.getLogger(buat_akun.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(buat_akun.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(buat_akun.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(buat_akun.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
 
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new buat_akun().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btndaftar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField tfemail;
    private javax.swing.JTextField tfpw;
    private javax.swing.JTextField tfusername;
    // End of variables declaration//GEN-END:variables
}
