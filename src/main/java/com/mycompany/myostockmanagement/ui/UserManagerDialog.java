package com.mycompany.myostockmanagement.ui;

import com.mycompany.myostockmanagement.dao.UserDAO;
import com.mycompany.myostockmanagement.service.UserService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class UserManagerDialog extends JDialog {
    private final UserService service=new UserService();
    private final DefaultTableModel model=new DefaultTableModel(new Object[]{"ID","Employee ID","Username","Full Name","Role"},0){@Override public boolean isCellEditable(int r,int c){return false;}};
    private final JTable table=new JTable(model);
    private final JTextField employee=new JTextField(10),username=new JTextField(10),fullName=new JTextField(14),password=new JPasswordField(10);
    private final JComboBox<String> role=new JComboBox<>(new String[]{"Admin","Employee"});
    public UserManagerDialog(Window owner){super(owner,"User Management",ModalityType.APPLICATION_MODAL);build();load();setSize(850,500);setLocationRelativeTo(owner);}
    private void build(){setLayout(new BorderLayout(8,8));JPanel f=new JPanel(new GridLayout(2,5,6,6));f.setBorder(BorderFactory.createEmptyBorder(8,8,0,8));f.add(new JLabel("Employee ID *"));f.add(new JLabel("Username *"));f.add(new JLabel("Password (required for new user)"));f.add(new JLabel("Full Name *"));f.add(new JLabel("Role *"));f.add(employee);f.add(username);f.add(password);f.add(fullName);f.add(role);JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT));JButton add=new JButton("Add"),update=new JButton("Update Selected"),del=new JButton("Delete Selected"),refresh=new JButton("Refresh"),clear=new JButton("Clear");actions.add(add);actions.add(update);actions.add(del);actions.add(refresh);actions.add(clear);JPanel top=new JPanel(new BorderLayout());top.add(f,BorderLayout.CENTER);top.add(actions,BorderLayout.SOUTH);add(top,BorderLayout.NORTH);add(new JScrollPane(table),BorderLayout.CENTER);table.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting())fill();});add.addActionListener(e->run(()->{if(new String(((JPasswordField)password).getPassword()).isBlank())throw new IllegalArgumentException("Password is required for a new user.");service.create(employee.getText(),username.getText(),new String(((JPasswordField)password).getPassword()),fullName.getText(),(String)role.getSelectedItem());clear();load();}));update.addActionListener(e->run(()->{int id=selectedId();service.update(id,employee.getText(),username.getText(),new String(((JPasswordField)password).getPassword()),fullName.getText(),(String)role.getSelectedItem());clear();load();}));del.addActionListener(e->run(()->{if(JOptionPane.showConfirmDialog(this,"Delete selected user?","Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){service.delete(selectedId());clear();load();}}));refresh.addActionListener(e->load());clear.addActionListener(e->clear());}
    private int selectedId(){int row=table.getSelectedRow();if(row<0)throw new IllegalArgumentException("Select a user first.");return ((Number)model.getValueAt(table.convertRowIndexToModel(row),0)).intValue();}
    private void fill(){int row=table.getSelectedRow();if(row<0)return;row=table.convertRowIndexToModel(row);employee.setText(""+model.getValueAt(row,1));username.setText(""+model.getValueAt(row,2));fullName.setText(""+model.getValueAt(row,3));role.setSelectedItem(""+model.getValueAt(row,4));password.setText("");}
    private void clear(){table.clearSelection();employee.setText("");username.setText("");fullName.setText("");password.setText("");role.setSelectedIndex(0);}
    private void load(){try{model.setRowCount(0);for(UserDAO.User u:service.findAll())model.addRow(new Object[]{u.id(),u.employeeId(),u.username(),u.fullName(),u.role()});}catch(SQLException ex){error(ex);}}
    private void run(Action a){try{a.run();}catch(Exception ex){error(ex);}}
    private void error(Exception ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"Users",JOptionPane.ERROR_MESSAGE);}
    @FunctionalInterface private interface Action{void run()throws Exception;}
}
