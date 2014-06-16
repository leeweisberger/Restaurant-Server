import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
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

	public GUI() throws IOException {
		super("Restaurant GUI");
		setSize(200, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		backEnd = new RestaurantBackEnd();	//TODO need server info
		createGUI();
		setVisible(true);
	}

	public void createGUI() throws IOException {	
		File file = new File("C:/Users/Lee/Documents/orderTest.txt");		
		JList<String> jList = initJList(file);
		initScrollPane(jList);		
		initDeleteButton();
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
		for(String order : getOrders(backEnd.update(10))){	//TODO
			model.addElement(order);
		}
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
