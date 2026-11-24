package com.mycompany.myostockmanagement.ui;

import com.mycompany.myostockmanagement.dao.ProductDAO;
import com.mycompany.myostockmanagement.dao.RackDAO;
import com.mycompany.myostockmanagement.service.StockMovementService;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class StockMovementDialog extends JDialog {
    private final ProductDAO products=new ProductDAO();
    private final RackDAO racks=new RackDAO();
    private final StockMovementService service=new StockMovementService();
    private final JComboBox<ProductChoice> product=new JComboBox<>();
    private final JComboBox<String> rack=new JComboBox<>();
    private final JTextField quantity=new JTextField("1",12), reference=new JTextField(18), remarks=new JTextField(18);
    private final String type, employeeId;
    public StockMovementDialog(Window owner,String type,String employeeId)
    {super(owner,type.equals("IN")?"Record Stock In":"Record Stock Out",ModalityType.APPLICATION_MODAL);
    this.type=type;this.employeeId=employeeId;build();loadChoices();setSize(440,330);setLocationRelativeTo(owner);}
    
    
    private void build(){setLayout(new BorderLayout(8,8));JPanel form=new JPanel(new GridLayout(5,2,8,10));
    form.setBorder(BorderFactory.createEmptyBorder(15,15,10,15));form.add(new JLabel("Product *"));
    form.add(product);form.add(new JLabel("Active Rack *"));form.add(rack);form.add(new JLabel("Quantity *"));form.add(quantity);form.add(new JLabel("Reference No."));form.add(reference);form.add(new JLabel("Remarks"));form.add(remarks);add(form,BorderLayout.CENTER);JButton save=new JButton(type.equals("IN")?"Save Stock In":"Save Stock Out"),cancel=new JButton("Cancel");JPanel actions=new JPanel();actions.add(save);actions.add(cancel);add(actions,BorderLayout.SOUTH);save.addActionListener(e->save());cancel.addActionListener(e->dispose());}
   
    
    private void loadChoices(){try{for(ProductDAO.Product p:products.findAll())
        product.addItem(new ProductChoice(p.id(),p.name()+" (ID "+p.id()+", total "+p.quantity()+")"));
    for(RackDAO.Rack r:racks.findActive())rack.addItem(r.code());}catch(SQLException ex)
    {JOptionPane.showMessageDialog(this,ex.getMessage(),"Load Error",JOptionPane.ERROR_MESSAGE);}}
    
    
    private void save(){try{if(product.getSelectedItem()==null)throw new IllegalArgumentException
        ("No product available. Add a product first.");if(rack.getSelectedItem()==null)
            throw new IllegalArgumentException("No active rack available.");
        int qty=Integer.parseInt(quantity.getText().trim());ProductChoice p=(ProductChoice)product.getSelectedItem();
        String r=(String)rack.getSelectedItem();if(type.equals("IN"))service.recordStockIn(p.id,r,employeeId,qty,reference.getText(),remarks.getText());
        else service.recordStockOut(p.id,r,employeeId,qty,reference.getText(),remarks.getText());
        JOptionPane.showMessageDialog(this,type.equals("IN")?
                "Stock In saved successfully.":"Stock Out saved successfully.");dispose();}
    catch(NumberFormatException ex){JOptionPane.showMessageDialog(this,"Quantity must be a whole number.",
            "Invalid Quantity",JOptionPane.WARNING_MESSAGE);}catch(SQLException|IllegalArgumentException ex)
            {JOptionPane.showMessageDialog(this,ex.getMessage(),"Stock Movement Failed",JOptionPane.ERROR_MESSAGE);}}
    
    
    private record ProductChoice(int id,String label){@Override public String toString(){return label;}}
}
