import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GUI extends JFrame {
    private JButton deleteButton;
    private int selectedIndex=-1;
    private JList<String> jList;
    private RestaurantBackEnd backEnd;
    private String specialOffer="";
    private static DefaultListModel<String> model = new DefaultListModel<String>();



    public GUI() throws IOException {
        super("Restaurant GUI");
        setSize(200, 600);
        defineClosingBehavior();
        backEnd = new RestaurantBackEnd("134.193.112.95", 12345);	
        createGUI();
        pack();
        setVisible(true);
    }

    protected void defineClosingBehavior() {
        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                backEnd.close();
                GUI.this.dispose();
            }	
        });
    }

    public void createGUI() throws IOException {	
        File file = new File("Orders.txt");		
        JList<String> jList = initJList(file);
        initScrollPane(jList);		
        initDeleteButton();
        initMenu();
    }

    protected void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu options = new JMenu("Options");
        JMenuItem pastOrders = new JMenuItem("Past Orders");
        pastOrders.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date date = new Date();
                    File file = new File(System.getProperty("user.home")+System.lineSeparator()+"Orders from " + df.format(date)+".txt");
                    PrintWriter writer = new PrintWriter(file, "UTF-8");
                    String[] oldOrders = getOrders(backEnd.update(40));
                    for(int i=0; i<oldOrders.length; i++) {
                        writer.println(oldOrders[i]); 
                    }
                    writer.close();
                    JOptionPane.showMessageDialog(GUI.this, "Successfully wrote to File!");
                } catch (FileNotFoundException | UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(GUI.this,
                            "Could Not Write to File",
                            "ERROR",
                            JOptionPane.ERROR_MESSAGE);
                }
            }			
        });
        JMenuItem offers = new JMenuItem("Add Offer");
        offers.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                specialOffer = (String)JOptionPane.showInputDialog(GUI.this, "Add New Special Offer");
                int status = backEnd.sendOffer(specialOffer);
                if(status<0)
                    JOptionPane.showMessageDialog(GUI.this, "Could not create special offer","ERROR", JOptionPane.ERROR_MESSAGE);
                else{
                    JOptionPane.showMessageDialog(GUI.this, "Successfully created special offer","SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                }
            }		
        });
        JMenuItem menu = new JMenuItem("Edit Menu");
        menu.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new MenuEditor();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        });
        options.add(pastOrders);
        options.add(offers);
        options.add(menu);
        menuBar.add(options);
        setJMenuBar(menuBar);
    }

    private void initDeleteButton() {
        deleteButton = new JButton("DELETE ORDER");
        deleteButton.setPreferredSize(new Dimension(200,75));
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(selectedIndex!=-1)
                    model.removeRange(selectedIndex, selectedIndex+1);			
            }

        });
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(deleteButton,BorderLayout.SOUTH);
        getContentPane().add(panel,BorderLayout.SOUTH);
    }

    private JList<String> initJList(File file) throws IOException {
        for(String order : getOrders(backEnd.update(10))){	//TODO Determine whether to update or get all orders
            addItemToGUI(order,model);						//10 for new, 20 for all
        }
//        readFileToGui(file,model);
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

//    public static void readFileToGui(DefaultListModel<String> model ) throws IOException {      
//        BufferedReader br = new BufferedReader(new FileReader(file));
//        String line;
//        while ((line = br.readLine()) != null) {
//            addItemToGUI(line,model);     
//        }
//        br.close();
//    }

    public static void writeMenuFile(){

        for (Enumeration<String> items = model.elements(); items.hasMoreElements();){
            String item =  formatItemForFile(items.nextElement()); 
            //TODO Complete this ish
        }
    }

    public static void addItemToGUI(String item,DefaultListModel<String> model) {
        //        String[] currentOrder = line.split(";");
        //        String formattedOrder = "<html>" + "Item: "  + currentOrder[0] +"<br>"+ "Price[s]: " +currentOrder[1];
        model.addElement(formatItemForGUI(item));
        model.addElement(JlistRenderer.SEPARATOR);
    }

    public static String formatItemForGUI(String item){
        item = "<html>" + item + "</html>";
        item = item.replace(";", "<br>");
        return item;
    }

    public static String formatItemForFile(String item){
        item = item.replace("</html>", "");
        item = item.replace("<html>", "");
        item = item.replace("<br>", ";");
        return item;
    }

    private String[] getOrders(ArrayList<String[]> orders) {
        ArrayList<String> result = new ArrayList<String>();
        for(int i=0; i<orders.size(); i++) {
            String temp = "";
            for(int j=0; j<orders.get(i).length-1; j++) {
                if(j==orders.get(i).length-2){
                    if(orders.get(i)[j].equals("0"))
                        temp+="Take Out";
                    else{temp+="Delivery";}
                }
                else{
                    temp += (orders.get(i)[j] + ";");
                }
                
            }
            result.add(temp);
        }
        return result.toArray(new String[result.size()]);
    }

    public static void main(String args[]) throws IOException {
        new GUI();
    }
}
