import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class MenuEditor extends JFrame {
    private JButton deleteButton;
    private JList<String> jList;
    private int selectedIndex=-1;
    private JButton addButton;
    private static DefaultListModel<String> model = new DefaultListModel<String>();


    public MenuEditor() throws IOException{
        super("Menu Editor");
        setSize(200, 600);
        createGUI();
        pack();
        setVisible(true);
    }

    private void createGUI() throws IOException {
        File file = new File("Orders from 2014-06-17 14:46.txt");       
        JList<String> jList = initJList(file);
        initScrollPane(jList);      
        initDeleteButton();
        initAddButton();
    }
    private void initDeleteButton() {
        deleteButton = new JButton("DELETE ITEM");
        deleteButton.setPreferredSize(new Dimension(100,75));
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(selectedIndex!=-1)
                    model.removeRange(selectedIndex, selectedIndex+1);          
            }
        });  
    }
    private void initAddButton() {
        addButton = new JButton("ADD ITEM");
        addButton.setPreferredSize(new Dimension(100,75));
        addButton.setEnabled(true);
        addButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField field1 = new JTextField();
                JTextField field2 = new JTextField();
                JTextField field3 = new JTextField();
                JTextField field4 = new JTextField();
                JTextField field5 = new JTextField();
                JTextField field6 = new JTextField();

                Object[] message = {
                        "Item Name:", field1,
                        "Item Category:",field2,
                        "Item Price (No Options):", field3,
                        "Item Option 1 : Price:", field4,
                        "Item Option 2 : Price:", field5,
                        "Item Option 3 : Price:", field6,

                };

                int option = JOptionPane.showConfirmDialog(MenuEditor.this, message, "Add A Menu Item", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION){
                    String itemName = field1.getText();
                    String itemCategory = field2.getText();
                    String itemPrice = field3.getText();
                    String itemOption1 = field4.getText();
                    String itemOption2 = field5.getText();
                    String itemOption3 = field6.getText();
                    if(itemName.isEmpty() || itemCategory.isEmpty() || (!itemPrice.isEmpty() && !itemOption1.isEmpty()))
                        JOptionPane.showMessageDialog(MenuEditor.this, "Invalid Parametrs","ERROR",JOptionPane.ERROR_MESSAGE);
                    else{
                        String newItem="";
                        if(itemPrice.isEmpty())
                            newItem= (itemName +";" + itemOption1+" "+itemOption2+" "+itemOption3+" "+itemCategory).trim();
                        else{newItem= itemName+";"+itemPrice+";"+itemCategory;}

                        GUI.addItemToGUI(newItem, model);
                    }
                }

            }

        });
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(addButton,BorderLayout.WEST);
        panel.add(deleteButton, BorderLayout.EAST);
        getContentPane().add(panel,BorderLayout.SOUTH);
    }

    private JList<String> initJList(File file) throws IOException {
        //      for(String order : getOrders(backEnd.update(10))){  //TODO Determine whether to update or get all orders
        //          model.addElement(order);                        //10 for new, 20 for all
        //      }
        GUI.readFileToGui(file,model);
        jList = new JList<>(model);

        jList.setCellRenderer(new JlistRenderer());

        jList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jList.setLayoutOrientation(JList.VERTICAL);
        jList.setVisible(true);
        jList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if(jList.getSelectedIndex()==-1 || jList.getSelectedValue().equals(JlistRenderer.SEPARATOR)){
                    deleteButton.setEnabled(false);
                    selectedIndex=-1;
                }           
                else{
                    deleteButton.setEnabled(true);
                    selectedIndex=jList.getSelectedIndex();
                }
            }
        });

        return jList;
    }

    private void initScrollPane(JList<String> list) {
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }
}
