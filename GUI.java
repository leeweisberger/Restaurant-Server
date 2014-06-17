import java.awt.BorderLayout;
import java.awt.Component;
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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GUI extends JFrame {
	private JButton deleteButton;
	private int selectedIndex=-1;
	private JList<String> jList;
	private RestaurantBackEnd backEnd;
	private String specialOffer="";
	
	
	public GUI() throws IOException {
		super("Restaurant GUI");
		setSize(200, 600);
		defineClosingBehavior();
		backEnd = new RestaurantBackEnd();	//TODO need server info
		createGUI();
		pack();
		setVisible(true);
	}

	protected void defineClosingBehavior() {
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
	          GUI.this.dispose();
	        }	
		});
	}

	public void createGUI() throws IOException {	
		File file = new File("/Users/lw033589/Documents/Orders.txt");		
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
					writer.println("testtestest"); //TODO: Add Relevant tests to file
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
			}		
		});
		options.add(pastOrders);
		options.add(offers);
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
				DefaultListModel model = (DefaultListModel) jList.getModel();
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
		DefaultListModel<String> model = new DefaultListModel<String>();
		for(String order : getOrders(backEnd.update(10))){	//TODO Determine whether to update or get all orders
			model.addElement(order);
		}
//		for(String order : (readFile(file))){
//			model.addElement(order);
//		}
		jList = new JList<>(model);
		
		jList.setCellRenderer(new JlistRenderer());
		
		jList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		jList.setLayoutOrientation(JList.VERTICAL);
		jList.setVisible(true);
		jList.addListSelectionListener(new ListSelectionListener() {

			@Override
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

	private String[] readFile(File file) throws IOException {
		List<String> orders = new ArrayList<String>();
		String currentOrder = "<html>";
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {
			currentOrder += line;
			currentOrder += "<br>";
			if (line.length() == 0) {
				orders.add(currentOrder+"</html>");
				orders.add(JlistRenderer.SEPARATOR);
				currentOrder = "<html>";
			}
		}
		br.close();
		return orders.toArray(new String[orders.size()]);

	}
	
	private String[] getOrders(ArrayList<String[]> orders) {
		ArrayList<String> result = new ArrayList<String>();
		for(int i=0; i<orders.size(); i++) {
			String temp = "";
			for(int j=0; j<orders.get(i).length; j++) {
				temp += orders.get(i)[j];
			}
			result.add(temp);
		}
		return result.toArray(new String[result.size()]);
	}

	public class JlistRenderer extends JLabel implements ListCellRenderer {
		JSeparator separator;
		public final static String SEPARATOR = "SEPARATOR";
		public JlistRenderer() {
			setOpaque(true);
			setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
			separator = new JSeparator(JSeparator.HORIZONTAL);
		}
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			String str = (value == null) ? "" : value.toString();
			if (str.equals(SEPARATOR)) {
				return separator;
			}
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setFont(list.getFont());
			setText(str);
			return this;
		}
	}
	public static void main(String args[]) throws IOException {
		new GUI();
	}
}
