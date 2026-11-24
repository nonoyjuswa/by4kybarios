package com.mycompany.myostockmanagement.ui;

import com.mycompany.myostockmanagement.dao.ProductDAO;
import com.mycompany.myostockmanagement.dao.RackDAO;
import com.mycompany.myostockmanagement.service.ProductService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

/** CRUD dialog launched from the existing Inventory button; dashboard GUI is unchanged. */
public class InventoryManagerDialog extends JDialog {
    private final ProductService service = new ProductService();
    private final String employeeId;
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"ID","Product","Rack Location","On Hand","Reorder Level"},0){@Override public boolean isCellEditable(int r,int c){return false;}};
    private final JTable table = new JTable(model);
    private final JTextField name = new JTextField(16), quantity = new JTextField("0",6), reorder = new JTextField("5",6);
    private final JComboBox<String> rack = new JComboBox<>();

    public InventoryManagerDialog(Window owner, String employeeId){
        super(owner,"Inventory Management",ModalityType.APPLICATION_MODAL);
        this.employeeId=employeeId;
        build();
        loadRacks();
        load();
        setSize(850,520);
        setLocationRelativeTo(owner);
    }

    private void build(){
        setLayout(new BorderLayout(8,8));
        JPanel fields=new JPanel(new GridLayout(2,4,8,5));
        fields.add(new JLabel("Product Name *"));fields.add(new JLabel("Rack Location *"));fields.add(new JLabel("Quantity *"));fields.add(new JLabel("Reorder Level *"));
        fields.add(name);fields.add(rack);fields.add(quantity);fields.add(reorder);
        JPanel buttons=new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add=new JButton("Add"),update=new JButton("Update Selected"),delete=new JButton("Delete Selected"),refresh=new JButton("Refresh"),clear=new JButton("Clear");
        buttons.add(add);buttons.add(update);buttons.add(delete);buttons.add(refresh);buttons.add(clear);
        JPanel north=new JPanel(new BorderLayout(5,5));north.add(fields,BorderLayout.CENTER);north.add(buttons,BorderLayout.SOUTH);north.setBorder(BorderFactory.createEmptyBorder(8,8,0,8));add(north,BorderLayout.NORTH);add(new JScrollPane(table),BorderLayout.CENTER);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);table.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting())fillFromSelection();});
        add.addActionListener(e->runAction(this::addProduct));
        update.addActionListener(e->runAction(()->{int id=selectedId();service.update(id,name.getText(),selectedRack(),number(quantity.getText()),number(reorder.getText()));load();}));
        delete.addActionListener(e->runAction(()->{int id=selectedId();if(JOptionPane.showConfirmDialog(this,"Delete selected product?","Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){service.delete(id);clearFields();load();}}));
        refresh.addActionListener(e->load());clear.addActionListener(e->clearFields());
    }

    private void addProduct() throws Exception {
    String productName = name.getText().trim();
    if (productName.isEmpty()) throw new IllegalArgumentException("Enter a product name.");
    String rackCode = selectedRack();
    int qty = number(quantity.getText());
    int reorderLevel = number(reorder.getText());

    int existingRow = findRowByName(productName);
    if (existingRow >= 0) {
        if (qty <= 0)
            throw new IllegalArgumentException("\"" + productName + "\" already exists. Enter a quantity greater than 0 to add to it.");
        int onHand = ((Number) model.getValueAt(existingRow, 3)).intValue();
        int choice = JOptionPane.showConfirmDialog(this,
                "\"" + productName + "\" already exists with " + onHand + " on hand.\n"
                + "Add " + qty + " to it in rack " + rackCode + "?",
                "Add to existing product", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;
    }

    service.create(productName, rackCode, qty, reorderLevel, employeeId);
    clearFields();
    load();

    if (existingRow >= 0)
        JOptionPane.showMessageDialog(this, "Added " + qty + " to existing product \"" + productName + "\".",
                "Inventory", JOptionPane.INFORMATION_MESSAGE);
}

/** Returns the table row whose product name matches (case-insensitive), or -1. */
private int findRowByName(String productName) {
    for (int i = 0; i < model.getRowCount(); i++) {
        if (String.valueOf(model.getValueAt(i, 1)).trim().equalsIgnoreCase(productName)) return i;
    }
    return -1;
}

    private void loadRacks(){
        try{
            rack.removeAllItems();
            for(RackDAO.Rack r:new RackDAO().findActive()) if("Active".equals(r.status())) rack.addItem(r.code());
            rack.setMaximumRowCount(12);
        }catch(SQLException ex){error(ex);}
    }
    private String selectedRack(){
        Object o=rack.getSelectedItem();
        if(o==null) throw new IllegalArgumentException("Choose a rack location.");
        return o.toString();
    }

    private int number(String s){try{int n=Integer.parseInt(s.trim());if(n<0)throw new NumberFormatException();return n;}catch(Exception ex){throw new IllegalArgumentException("Quantity and reorder level must be whole numbers 0 or greater.");}}
    private int selectedId(){int row=table.getSelectedRow();if(row<0)throw new IllegalArgumentException("Select a product from the table first.");return ((Number)model.getValueAt(table.convertRowIndexToModel(row),0)).intValue();}
    private void fillFromSelection(){int row=table.getSelectedRow();if(row<0)return;row=table.convertRowIndexToModel(row);name.setText(String.valueOf(model.getValueAt(row,1)));rack.setSelectedItem(String.valueOf(model.getValueAt(row,2)));quantity.setText(String.valueOf(model.getValueAt(row,3)));reorder.setText(String.valueOf(model.getValueAt(row,4)));}
    private void clearFields(){table.clearSelection();name.setText("");if(rack.getItemCount()>0)rack.setSelectedIndex(0);quantity.setText("0");reorder.setText("5");}
    private void load(){try{model.setRowCount(0);for(ProductDAO.Product p:service.findAll())model.addRow(new Object[]{p.id(),p.name(),p.rackLocation(),p.quantity(),p.reorderLevel()});}catch(SQLException ex){error(ex);}}
    private void runAction(SqlAction action){try{action.run();}catch(Exception ex){error(ex);}}
    private void error(Exception ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"Inventory",JOptionPane.ERROR_MESSAGE);}
    @FunctionalInterface private interface SqlAction{void run()throws Exception;}
}