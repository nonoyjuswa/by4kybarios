
package com.mycompany.myostockmanagement;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.mycompany.myostockmanagement.dao.StockMovementDAO;
import com.mycompany.myostockmanagement.dao.InventoryDAO;
import com.mycompany.myostockmanagement.dao.DashboardDAO;
import com.mycompany.myostockmanagement.ui.InventoryManagerDialog;
import com.mycompany.myostockmanagement.ui.StockMovementDialog;
import com.mycompany.myostockmanagement.ui.UserManagerDialog;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

public class MainFrame extends javax.swing.JFrame {

    private Timer clockTimer;
    private String currentEmployeeId = "";

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(MainFrame.class.getName());

    // Constructor: initialize the GUI first
    public MainFrame(String f_name, String role, String employee_id) {

        initComponents();

        // Display the signed-in user's information
        lblUsername.setText(
                f_name == null || f_name.trim().isEmpty()
                        ? "User"
                        : f_name
        );

        lblUsers.setText(
                (role == null ? "User" : role)
                + " | "
                + (employee_id == null ? "" : employee_id)
        );

        currentEmployeeId = employee_id == null ? "" : employee_id;
        startClock();
        wireNavigationButtons();
        loadRestockingData();
        loadLatestMovements();

        setLocationRelativeTo(null);
    }

    // Display the current date and time
    private void startClock() {

        DateTimeFormatter timeFormat =
                DateTimeFormatter.ofPattern("HH:mm:ss");

        DateTimeFormatter dateFormat =
                DateTimeFormatter.ofPattern("EEE dd MMM yyyy", Locale.ENGLISH);

        Runnable updateClock = () -> {
            LocalDateTime now = LocalDateTime.now();

            clock.setText(now.format(timeFormat));
            lbldate.setText(now.format(dateFormat));
        };

        updateClock.run();

        clockTimer = new Timer(1000, e -> updateClock.run());
        clockTimer.start();
    }

    // Dashboard data is loaded through DAO classes.
    private void loadLatestMovements() {
        DefaultTableModel model = (DefaultTableModel) tblmovements.getModel();
        model.setRowCount(0);
        try {
            for (Object[] row : new StockMovementDAO().findLatest(10)) {
                model.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Unable to load latest movements:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRestockingData() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        try {
            for (Object[] row : new DashboardDAO().findRestockingProducts()) {
                model.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Unable to load restocking data:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sidebarPanel = new javax.swing.JPanel();
        btndashboard = new javax.swing.JButton();
        btninventory = new javax.swing.JButton();
        btnrackmap = new javax.swing.JButton();
        btnstockin = new javax.swing.JButton();
        btnstockout = new javax.swing.JButton();
        btnledger = new javax.swing.JButton();
        btnusers = new javax.swing.JButton();
        btnSignout = new javax.swing.JButton();
        lblTitle1 = new javax.swing.JLabel();
        datetimePanel = new javax.swing.JPanel();
        lblClock = new javax.swing.JLabel();
        lblDateTimeTitle = new javax.swing.JLabel();
        lbldate = new javax.swing.JLabel();
        clock = new javax.swing.JLabel();
        lblSignedinas = new javax.swing.JLabel();
        lblUsername = new javax.swing.JLabel();
        contentPanel = new javax.swing.JPanel();
        btnStockIn1 = new javax.swing.JButton();
        btnStockOut = new javax.swing.JButton();
        lblTitle = new javax.swing.JLabel();
        lblSubtitle = new javax.swing.JLabel();
        lblUsers = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lbllatestmovements = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblmovements = new javax.swing.JTable();
        needsrestockingpanel = new javax.swing.JPanel();
        tblrestocking = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        lblforRestocking = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        sidebarPanel.setBackground(new java.awt.Color(204, 255, 204));
        sidebarPanel.setMinimumSize(new java.awt.Dimension(250, 0));
        sidebarPanel.setPreferredSize(new java.awt.Dimension(250, 0));

        btndashboard.setText("DASHBOARD");
        btndashboard.setFocusPainted(false);
        btndashboard.setMargin(new java.awt.Insets(5, 12, 5, 5));
        btndashboard.setPreferredSize(new java.awt.Dimension(220, 40));

        btninventory.setText("INVENTORY");

        btnrackmap.setText("RACK MAP");

        btnstockin.setText("STOCK IN");
        btnstockin.addActionListener(this::btnstockinActionPerformed);

        btnstockout.setText("STOCK OUT ");

        btnledger.setText("LEDGER");

        btnusers.setText("USERS");
        btnusers.addActionListener(this::btnusersActionPerformed);

        btnSignout.setText("SIGN OUT");
        btnSignout.addActionListener(this::btnSignoutActionPerformed);

        lblTitle1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTitle1.setText("My0_Hardware");
        lblTitle1.setPreferredSize(new java.awt.Dimension(250, 32));

        datetimePanel.setPreferredSize(new java.awt.Dimension(230, 125));

        lblDateTimeTitle.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblDateTimeTitle.setText("Date and Time:");

        lbldate.setText("date");

        clock.setText("time");

        javax.swing.GroupLayout datetimePanelLayout = new javax.swing.GroupLayout(datetimePanel);
        datetimePanel.setLayout(datetimePanelLayout);
        datetimePanelLayout.setHorizontalGroup(
            datetimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(datetimePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(datetimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(datetimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(lblClock, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblDateTimeTitle, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbldate, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(clock, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        datetimePanelLayout.setVerticalGroup(
            datetimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(datetimePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDateTimeTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbldate, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clock, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(lblClock)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblSignedinas.setText("Signed In As:");

        lblUsername.setText("name");

        javax.swing.GroupLayout sidebarPanelLayout = new javax.swing.GroupLayout(sidebarPanel);
        sidebarPanel.setLayout(sidebarPanelLayout);
        sidebarPanelLayout.setHorizontalGroup(
            sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sidebarPanelLayout.createSequentialGroup()
                .addGap(0, 2, Short.MAX_VALUE)
                .addComponent(lblTitle1, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(sidebarPanelLayout.createSequentialGroup()
                .addGroup(sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sidebarPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btndashboard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btninventory, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnrackmap, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnstockin, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnstockout, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnledger, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnusers, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(datetimePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)))
                    .addGroup(sidebarPanelLayout.createSequentialGroup()
                        .addGroup(sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(sidebarPanelLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblSignedinas, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(sidebarPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(btnSignout, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        sidebarPanelLayout.setVerticalGroup(
            sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(btndashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btninventory, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnrackmap, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnstockin, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnstockout, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnledger, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnusers, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(datetimePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSignedinas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSignout)
                .addGap(45, 45, 45))
        );

        contentPanel.setPreferredSize(new java.awt.Dimension(1200, 750));

        btnStockIn1.setBackground(new java.awt.Color(0, 153, 51));
        btnStockIn1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnStockIn1.setForeground(new java.awt.Color(255, 255, 255));
        btnStockIn1.setText("RECORD STOCK IN");
        btnStockIn1.setBorderPainted(false);
        btnStockIn1.setFocusPainted(false);
        btnStockIn1.addActionListener(this::btnStockIn1ActionPerformed);

        btnStockOut.setBackground(new java.awt.Color(204, 0, 0));
        btnStockOut.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnStockOut.setForeground(new java.awt.Color(255, 255, 255));
        btnStockOut.setText("RECORD  STOCK OUT");
        btnStockOut.setBorderPainted(false);
        btnStockOut.setFocusPainted(false);
        btnStockOut.addActionListener(this::btnStockOutActionPerformed);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTitle.setText("WAREHOUSE OVERVIEW");

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSubtitle.setText("Posistion of Stocks, Racks, and Movements");

        lblUsers.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblUsers.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblUsers.setText("Admin | ADM-404");

        lbllatestmovements.setFont(new java.awt.Font("Segoe UI Black", 2, 14)); // NOI18N
        lbllatestmovements.setText("LATEST MOVEMENTS");

        tblmovements.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Date", "Time", "StockMovement", "Product", "QTY", "Rack", "Employee"
            }
        ));
        jScrollPane1.setViewportView(tblmovements);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Product", "Rack", "Onhand", "Status"
            }
        ));
        tblrestocking.setViewportView(jTable1);

        javax.swing.GroupLayout needsrestockingpanelLayout = new javax.swing.GroupLayout(needsrestockingpanel);
        needsrestockingpanel.setLayout(needsrestockingpanelLayout);
        needsrestockingpanelLayout.setHorizontalGroup(
            needsrestockingpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(needsrestockingpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tblrestocking, javax.swing.GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE)
                .addContainerGap())
        );
        needsrestockingpanelLayout.setVerticalGroup(
            needsrestockingpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tblrestocking, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        lblforRestocking.setFont(new java.awt.Font("Segoe UI Black", 2, 14)); // NOI18N
        lblforRestocking.setText("for RESTOCKING");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 601, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbllatestmovements, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(needsrestockingpanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(lblforRestocking, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(243, 243, 243)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbllatestmovements)
                    .addComponent(lblforRestocking))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(needsrestockingpanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSubtitle)
                            .addGroup(contentPanelLayout.createSequentialGroup()
                                .addComponent(btnStockIn1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnStockOut, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addComponent(lblTitle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblUsers, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41))))
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitle)
                    .addComponent(lblUsers))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSubtitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnStockIn1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnStockOut, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(sidebarPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1065, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(sidebarPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void wireNavigationButtons() {
        btndashboard.addActionListener(e -> {
            loadRestockingData();
            loadLatestMovements();
        });
        btninventory.addActionListener(e -> new InventoryManagerDialog(this, currentEmployeeId).setVisible(true));
        btnstockin.addActionListener(e -> openStockMovement("IN"));
        btnstockout.addActionListener(e -> openStockMovement("OUT"));
        btnusers.addActionListener(e -> new UserManagerDialog(this).setVisible(true));
        btnrackmap.addActionListener(e -> showRackMap());
        btnledger.addActionListener(e -> showLedger());
    }

    private void openStockMovement(String type) {
        StockMovementDialog dialog = new StockMovementDialog(this, type, currentEmployeeId);
        dialog.setVisible(true);
        loadRestockingData();
        loadLatestMovements();
    }

     private void showTableDialog(String title, javax.swing.JTable table) {
        table.setRowHeight(26);
        table.setFont(table.getFont().deriveFont(14f));
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(java.awt.Font.BOLD, 14f));
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Size each column to fit its content
        for (int c = 0; c < table.getColumnCount(); c++) {
            javax.swing.table.TableColumn col = table.getColumnModel().getColumn(c);
            int max = table.getTableHeader().getDefaultRenderer()
                    .getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, c).getPreferredSize().width;
            for (int r = 0; r < table.getRowCount(); r++) {
                max = Math.max(max, table.getCellRenderer(r, c)
                        .getTableCellRendererComponent(table, table.getValueAt(r, c), false, false, r, c).getPreferredSize().width);
            }
            col.setPreferredWidth(max + 30);
        }

        java.awt.Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        javax.swing.JDialog dialog = new javax.swing.JDialog(this, title, true);
        dialog.setLayout(new java.awt.BorderLayout());
        dialog.add(new javax.swing.JScrollPane(table), java.awt.BorderLayout.CENTER);
        javax.swing.JButton ok = new javax.swing.JButton("OK");
        ok.addActionListener(e -> dialog.dispose());
        javax.swing.JPanel south = new javax.swing.JPanel();
        south.add(ok);
        dialog.add(south, java.awt.BorderLayout.SOUTH);
        dialog.setSize((int) (screen.width * 0.8), (int) (screen.height * 0.7));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void showRackMap() {
        try {
            java.util.List<Object[]> inventoryRows = new InventoryDAO().findAll();
            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"Inventory ID", "Product ID", "Product", "Rack", "Quantity", "Updated At"}, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
            for (Object[] row : inventoryRows) model.addRow(row);
            
            JTable table = new JTable(model);
            table.setAutoCreateRowSorter(true);
            showTableDialog("Stock by Rack", table);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Rack Map Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showLedger() {
        try {
            Object[][] rows = new StockMovementDAO().findLatest(200);
            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"Date", "Time", "Movement", "Product", "Quantity", "Rack", "Employee"}, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
            for (Object[] row : rows) model.addRow(row);
            JTable table = new JTable(model);
            table.setAutoCreateRowSorter(true);
            showTableDialog("Stock Movement Ledger", table);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Ledger Error", JOptionPane.ERROR_MESSAGE);
        }
    }

       
    private void btnSignoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSignoutActionPerformed
        if (clockTimer != null) {
        clockTimer.stop();
    }
    LoginFrame login = new LoginFrame();
    login.setLocationRelativeTo(null);
    login.setVisible(true);
    this.dispose();
    }//GEN-LAST:event_btnSignoutActionPerformed

    private void btnStockIn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStockIn1ActionPerformed
        openStockMovement("IN");
    }//GEN-LAST:event_btnStockIn1ActionPerformed

    private void btnStockOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStockOutActionPerformed
        openStockMovement("OUT");
    }//GEN-LAST:event_btnStockOutActionPerformed

    private void btnusersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnusersActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnusersActionPerformed

    private void btnstockinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnstockinActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnstockinActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> { new LoginFrame().setVisible(true); });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSignout;
    private javax.swing.JButton btnStockIn1;
    private javax.swing.JButton btnStockOut;
    private javax.swing.JButton btndashboard;
    private javax.swing.JButton btninventory;
    private javax.swing.JButton btnledger;
    private javax.swing.JButton btnrackmap;
    private javax.swing.JButton btnstockin;
    private javax.swing.JButton btnstockout;
    private javax.swing.JButton btnusers;
    private javax.swing.JLabel clock;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JPanel datetimePanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblClock;
    private javax.swing.JLabel lblDateTimeTitle;
    private javax.swing.JLabel lblSignedinas;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTitle1;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JLabel lblUsers;
    private javax.swing.JLabel lbldate;
    private javax.swing.JLabel lblforRestocking;
    private javax.swing.JLabel lbllatestmovements;
    private javax.swing.JPanel needsrestockingpanel;
    private javax.swing.JPanel sidebarPanel;
    private javax.swing.JTable tblmovements;
    private javax.swing.JScrollPane tblrestocking;
    // End of variables declaration//GEN-END:variables
}
