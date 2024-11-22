/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package projekakhir;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.*;
import java.io.*;
/**
 *
 * @author Lenovo
 */
public class Admin extends javax.swing.JFrame {
    private DefaultTableModel modelProduk;
    private byte[] imageBytes = null; // Menyimpan gambar dalam format byte array
    private Connection conn;

  public Admin() {
    initComponents(); // Inisialisasi komponen GUI

    // Inisialisasi koneksi database
    conn = koneksi.getConnection();

    if (conn == null) {
        JOptionPane.showMessageDialog(this, "Koneksi ke database gagal!", "Error", JOptionPane.ERROR_MESSAGE);
    } else {
        System.out.println("Koneksi berhasil diinisialisasi di Admin.");
    }

    // Panggil loadAdminOrderRecap setelah koneksi dibuat
    loadAdminOrderRecap();

    // Set up model untuk tabel Produk
    modelProduk = new DefaultTableModel();
    tblProduk.setModel(modelProduk);
    modelProduk.addColumn("ID Produk");
    modelProduk.addColumn("Nama Produk");
    modelProduk.addColumn("Harga");
    modelProduk.addColumn("Stok");
    modelProduk.addColumn("Deskripsi");
    modelProduk.addColumn("Image URL");

    tblProduk.setRowHeight(50); // Set tinggi baris untuk menampilkan gambar
    tblProduk.getColumnModel().getColumn(5).setCellRenderer(new ImageRenderer());
}

private void loadDataProduk() {
    // Clear existing rows
    modelProduk.setRowCount(0);

    try {
        String sql = "SELECT * FROM produk";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            int idProduk = rs.getInt("id_produk");
            String namaProduk = rs.getString("nama_produk");
            double hargaProduk = rs.getDouble("harga");
            int stokProduk = rs.getInt("stok");
            String deskripsiProduk = rs.getString("deskripsi");
            byte[] gambarProduk = rs.getBytes("image_url"); // Ambil gambar produk dalam format byte[]

            modelProduk.addRow(new Object[]{idProduk, namaProduk, hargaProduk, stokProduk, deskripsiProduk, gambarProduk});
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error loading product data: " + e.getMessage());
    }

    // Set renderer and row height after loading data
    tblProduk.getColumnModel().getColumn(5).setCellRenderer(new ImageRenderer());
    tblProduk.setRowHeight(50); // Set tinggi baris untuk menampilkan gambar
}

private void btnPilihGambarActionPerformed(java.awt.event.ActionEvent evt) {
    JFileChooser fileChooser = new JFileChooser(); // Untuk memilih gambar produk
    fileChooser.setDialogTitle("Pilih Gambar Produk");
    FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png");
    fileChooser.setFileFilter(filter);

    int result = fileChooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        String filePath = file.getAbsolutePath();
        ImageIcon icon = new ImageIcon(filePath);

        // Resize image untuk ditampilkan di JLabel
        Image image = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        lblGambarProduk.setIcon(new ImageIcon(image)); // Menampilkan gambar di label

        // Convert gambar menjadi byte[] untuk disimpan di database
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            imageBytes = bos.toByteArray();
            fis.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error membaca gambar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

   private void loadTableData() {
    DefaultTableModel model = (DefaultTableModel) tblProduk.getModel();
    model.setRowCount(0); // Menghapus semua data yang ada di tabel sebelum memuat ulang

    // Koneksi ke database dan mengeksekusi query SELECT
    try (Connection conn = koneksi.getConnection()) { // Menggunakan try-with-resources untuk otomatis menutup koneksi
        String sql = "SELECT * FROM produk"; // Query untuk mengambil semua data produk
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(); // Eksekusi query dan mendapatkan hasil

        // Menambahkan data ke dalam model tabel
        while (rs.next()) {
            String idProduk = rs.getString("id_produk");
            String namaProduk = rs.getString("nama_produk");
            String harga = rs.getString("harga");
            String stok = rs.getString("stok");
            String deskripsi = rs.getString("deskripsi");

            // Menambahkan baris ke dalam tabel
            model.addRow(new Object[]{idProduk, namaProduk, harga, stok, deskripsi});
        }
    } catch (SQLException e) {
        // Menangani exception dengan menampilkan pesan error
        JOptionPane.showMessageDialog(null, "Terjadi kesalahan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace(); // Menampilkan stack trace untuk debugging
    }
}

  
  // Fungsi untuk mengambil gambar dalam bentuk BLOB dari database
    private ImageIcon getImageFromDatabase(int productId) {
        ImageIcon imageIcon = null;
        
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ecommerce", "root", "")) {
            String query = "SELECT image_url FROM produk WHERE product_id = ?";
            try (PreparedStatement pst = conn.prepareStatement(query)) {
                pst.setInt(1, productId);
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    // Ambil gambar dalam bentuk BLOB
                    byte[] imgBytes = rs.getBytes("image_url");
                    if (imgBytes != null) {
                        // Konversi byte array menjadi ImageIcon
                        imageIcon = new ImageIcon(imgBytes);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return imageIcon;
    }

    // Fungsi untuk menampilkan gambar pada JLabel
    private void displayProductImage(int productId) {
        ImageIcon productImage = getImageFromDatabase(productId); // Ambil gambar dari database
        if (productImage != null) {
            Image img = productImage.getImage(); // Mendapatkan image dari ImageIcon
            Image scaledImg = img.getScaledInstance(150, 150, Image.SCALE_SMOOTH); // Resize gambar
            ImageIcon scaledImageIcon = new ImageIcon(scaledImg); // Membuat ImageIcon dari gambar yang di-resize
            JLabel imageLabel = new JLabel(scaledImageIcon); // Membuat JLabel dengan ImageIcon

            // Menambahkan JLabel ke panel utama (jPanel2)
            jPanel2.add(imageLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 100, 150, 150)); // Sesuaikan posisi dan ukuran
            revalidate();
            repaint();
        } else {
            System.out.println("Gambar tidak ditemukan di database.");
        }
    }


private void updateProduct() {
    String idProduk = tfIdProduk.getText();
    String namaProduk = tfNamaProduk.getText();
    String harga = tfHarga.getText();
    String stok = tfStok.getText();
    String deskripsi = taDeskripsi.getText();

    try (Connection conn = koneksi.getConnection()) {
        String sql = "UPDATE produk SET nama_produk = ?, harga = ?, stok = ?, deskripsi = ? WHERE id_produk = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, namaProduk);
        ps.setString(2, harga);
        ps.setString(3, stok);
        ps.setString(4, deskripsi);
        ps.setString(5, idProduk);
        
        int rowsAffected = ps.executeUpdate(); // Mengeksekusi query dan mendapatkan jumlah baris yang terpengaruh
        if (rowsAffected > 0) {
            // Jika data berhasil diperbarui
            JOptionPane.showMessageDialog(null, "Data produk berhasil diperbarui!", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Jika gagal memperbarui data
            JOptionPane.showMessageDialog(null, "Gagal memperbarui data produk.", "Gagal", JOptionPane.ERROR_MESSAGE);
        }

        // Refresh tabel setelah update data
        loadTableData();
    } catch (SQLException e) {
        // Menangani kesalahan dan memberi pesan jika terjadi error
        JOptionPane.showMessageDialog(null, "Terjadi kesalahan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

private void deleteProduct() {
    String idProduk = tfIdProduk.getText();

    try (Connection conn = koneksi.getConnection()) {
        String sql = "DELETE FROM produk WHERE id_produk = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, idProduk);
        
        int rowsAffected = ps.executeUpdate(); // Mengeksekusi query dan mendapatkan jumlah baris yang terpengaruh
        if (rowsAffected > 0) {
            // Jika data berhasil dihapus
            JOptionPane.showMessageDialog(null, "Data produk berhasil dihapus!", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Jika gagal menghapus data
            JOptionPane.showMessageDialog(null, "Gagal menghapus data produk.", "Gagal", JOptionPane.ERROR_MESSAGE);
        }

        // Refresh tabel setelah menghapus data
        loadTableData();
    } catch (SQLException e) {
        // Menangani kesalahan dan memberi pesan jika terjadi error
        JOptionPane.showMessageDialog(null, "Terjadi kesalahan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

// Renderer untuk menampilkan gambar pada JTable
private class ImageRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = new JLabel();
        if (value != null) {
            byte[] imageBytes = (byte[]) value;
            ImageIcon icon = new ImageIcon(imageBytes);
            Image scaledImage = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaledImage));
        }
        return label;
    }
}

//tab rekap orderan
private void loadAdminOrderRecap() {
    DefaultTableModel model = (DefaultTableModel) adminOrderTable.getModel(); // adminOrderTable adalah JTable di tab rekapan order
    model.setRowCount(0);

    String sql = "SELECT order_id, user_id, total_price, order_status, order_date FROM orders";
    try (PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
            int orderId = rs.getInt("order_id");
            int userId = rs.getInt("user_id");
            double totalPrice = rs.getDouble("total_price");
            String status = rs.getString("order_status");
            Date orderDate = rs.getDate("order_date");

            model.addRow(new Object[]{orderId, userId, totalPrice, status, orderDate});
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Gagal memuat rekapan order: " + e.getMessage());
    }
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
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProduk = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblGambarProduk = new javax.swing.JLabel();
        tfIdProduk = new javax.swing.JTextField();
        tfHarga = new javax.swing.JTextField();
        tfStok = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        taDeskripsi = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        tfNamaProduk = new javax.swing.JTextField();
        btnTambahGambar = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        btnkembali = new javax.swing.JButton();
        jFileChooser1 = new javax.swing.JFileChooser();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        adminOrderTable = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 204, 204));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblProduk.setFont(new java.awt.Font("Serif", 0, 12)); // NOI18N
        tblProduk.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblProduk);

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 470, 470, 130));

        jLabel1.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Nama Produk  :");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 90, -1, -1));

        jLabel3.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Harga :");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 130, 60, -1));

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Deskripsi :");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 160, -1, -1));

        jLabel5.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Stok :");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 210, -1, -1));

        jLabel12.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Gambar :");
        jPanel2.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 250, -1, -1));
        jPanel2.add(lblGambarProduk, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 280, 100, 100));

        tfIdProduk.setFont(new java.awt.Font("Serif", 0, 12)); // NOI18N
        tfIdProduk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfIdProdukActionPerformed(evt);
            }
        });
        jPanel2.add(tfIdProduk, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 60, 310, -1));

        tfHarga.setFont(new java.awt.Font("Serif", 0, 12)); // NOI18N
        tfHarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfHargaActionPerformed(evt);
            }
        });
        jPanel2.add(tfHarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 120, 310, -1));

        tfStok.setFont(new java.awt.Font("Serif", 0, 12)); // NOI18N
        tfStok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfStokActionPerformed(evt);
            }
        });
        jPanel2.add(tfStok, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 210, 310, -1));

        taDeskripsi.setColumns(20);
        taDeskripsi.setFont(new java.awt.Font("Serif", 0, 12)); // NOI18N
        taDeskripsi.setRows(5);
        jScrollPane2.setViewportView(taDeskripsi);

        jPanel2.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 150, 310, 50));

        jLabel6.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Id Produk :");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 60, -1, -1));

        tfNamaProduk.setFont(new java.awt.Font("Serif", 0, 12)); // NOI18N
        jPanel2.add(tfNamaProduk, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 90, 310, -1));

        btnTambahGambar.setFont(new java.awt.Font("Serif", 0, 12)); // NOI18N
        btnTambahGambar.setText("Tambah");
        btnTambahGambar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahGambarActionPerformed(evt);
            }
        });
        jPanel2.add(btnTambahGambar, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 430, -1, -1));

        btnUpdate.setFont(new java.awt.Font("Serif", 0, 12)); // NOI18N
        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        jPanel2.add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 430, -1, -1));

        btnHapus.setFont(new java.awt.Font("Serif", 0, 12)); // NOI18N
        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });
        jPanel2.add(btnHapus, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 430, -1, -1));

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Serif", 3, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("RESELLER  ENOLA");
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, -1, -1));

        btnkembali.setFont(new java.awt.Font("Serif", 0, 12)); // NOI18N
        btnkembali.setText("Kembali");
        btnkembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnkembaliActionPerformed(evt);
            }
        });
        jPanel2.add(btnkembali, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 430, -1, -1));
        jPanel2.add(jFileChooser1, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 260, 330, 150));
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 710));

        jTabbedPane1.addTab("Produk", jPanel2);

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("Serif", 3, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("REKAPAN ORDERAN");
        jPanel3.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 50, -1, -1));

        adminOrderTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(adminOrderTable);

        jPanel3.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 110, -1, 350));

        jLabel10.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Id Produk :");
        jPanel3.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 500, -1, -1));
        jPanel3.add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 490, 140, -1));

        jButton4.setText("Update");
        jPanel3.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 580, -1, -1));

        jButton5.setText("Hapus");
        jPanel3.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 580, -1, -1));

        jLabel11.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Status :");
        jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 530, -1, -1));
        jPanel3.add(jTextField6, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 530, 140, -1));
        jPanel3.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 539, 637));

        jTabbedPane1.addTab("Transaksi", jPanel3);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 531, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 751, Short.MAX_VALUE)
                .addContainerGap())
        );

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

    private void tfIdProdukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfIdProdukActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfIdProdukActionPerformed

    private void tfHargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfHargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfHargaActionPerformed

    private void tfStokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfStokActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfStokActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        updateProduct();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnTambahGambarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahGambarActionPerformed

  if (conn == null) {
            JOptionPane.showMessageDialog(this, "Koneksi ke database tidak tersedia.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    String namaProduk = tfNamaProduk.getText();
    String hargaProduk = tfHarga.getText();
    String stokProduk = tfStok.getText();
    String deskripsiProduk = taDeskripsi.getText();
    
    JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                // Baca file gambar ke byte array
                FileInputStream fis = new FileInputStream(file);
                imageBytes = fis.readAllBytes();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    if (namaProduk.isEmpty() || hargaProduk.isEmpty() || stokProduk.isEmpty() || deskripsiProduk.isEmpty() || imageBytes == null) {
        JOptionPane.showMessageDialog(this, "Semua field harus diisi dan gambar harus dipilih!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return; // Hentikan eksekusi jika ada input kosong
    }

    try {
        String sql = "INSERT INTO produk (nama_produk, harga, stok, deskripsi, image_url) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, namaProduk);
        ps.setDouble(2, Double.parseDouble(hargaProduk));
        ps.setInt(3, Integer.parseInt(stokProduk));
        ps.setString(4, deskripsiProduk);
        ps.setBytes(5, imageBytes); // Menyimpan gambar dalam bentuk byte[]

        ps.executeUpdate(); // Eksekusi query untuk menambahkan produk

        loadDataProduk(); // Update tabel produk
        JOptionPane.showMessageDialog(this, "Produk berhasil ditambahkan!");
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error menambahkan produk: " + e.getMessage());
    }

    }//GEN-LAST:event_btnTambahGambarActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        deleteProduct();
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnkembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnkembaliActionPerformed
        new login().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnkembaliActionPerformed

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
            java.util.logging.Logger.getLogger(Admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Admin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable adminOrderTable;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnTambahGambar;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnkembali;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JLabel lblGambarProduk;
    private javax.swing.JTextArea taDeskripsi;
    private javax.swing.JTable tblProduk;
    private javax.swing.JTextField tfHarga;
    private javax.swing.JTextField tfIdProduk;
    private javax.swing.JTextField tfNamaProduk;
    private javax.swing.JTextField tfStok;
    // End of variables declaration//GEN-END:variables
}
